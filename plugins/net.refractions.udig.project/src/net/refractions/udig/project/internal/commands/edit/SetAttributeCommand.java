/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.internal.commands.edit;

import java.text.MessageFormat;

import net.refractions.udig.core.IBlockingProvider;
import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.provider.EditFeatureProvider;
import net.refractions.udig.project.command.provider.EditLayerProvider;
import net.refractions.udig.project.command.provider.FIDFeatureProvider;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This command modifies an attribute of the current editFeature(the victim that is currently
 * edittable).
 * 
 * @author jeichar
 * @since 0.3
 */
public class SetAttributeCommand extends AbstractEditCommand implements UndoableMapCommand {
    protected String xpath;

    protected final Object value;

    private Object oldValue;

    private final IBlockingProvider<SimpleFeature> editFeature;

    protected final IBlockingProvider<ILayer> editLayer;

    /**
     * Creates a new instance of SetAttributeCommand.
     * 
     * @param feature the feature to modify
     * @param xpath the xpath that identifies an attribute in the current edit feature.
     * @param value the value that will replace the old attribute value.
     */
    public SetAttributeCommand( IBlockingProvider<SimpleFeature> feature, IBlockingProvider<ILayer> layer, String xpath,
            Object value ) {
        this.xpath = xpath;
        this.value = value;
        editFeature = feature;
        editLayer = layer;
    }

    /**
     * @param featureID
     * @param layer
     * @param xpath2
     * @param geom
     */
    public SetAttributeCommand( String featureID, IBlockingProvider<ILayer> layer, String xpath2,
            Geometry geom ) {
        this( new FIDFeatureProvider(featureID, layer), layer, xpath2, geom );
    }
    /**
     * Creates a new instance of SetAttributeCommand.
     * 
     * @param feature the feature to modify
     * @param xpath the xpath that identifies an attribute in the current edit feature.
     * @param value the value that will replace the old attribute value.
     */
    public SetAttributeCommand( String xpath, Object value ) {
        editFeature=new EditFeatureProvider(this);
        editLayer=new EditLayerProvider(this);
        this.xpath=xpath;
        this.value=value; 
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        ILayer layer = editLayer.get(monitor);
        if( layer==null ){
            System.err.println("class "+editLayer.getClass().getName()+" is returning null");  //$NON-NLS-1$//$NON-NLS-2$
            return;
        }
        FeatureStore<SimpleFeatureType, SimpleFeature> resource = layer.getResource(FeatureStore.class, null);
        //SimpleFeatureStore resource = layer.getResource(SimpleFeatureStore.class, null );
        SimpleFeature feature2 = editFeature.get(monitor);

        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
		Id fidFilter = filterFactory.id(
                FeatureUtils.stringToId(filterFactory,feature2.getID()));

        this.oldValue = feature2.getAttribute(xpath);
        feature2.setAttribute(xpath, value);

        AttributeDescriptor attributeType = layer.getSchema().getDescriptor(xpath);
        resource.modifyFeatures(attributeType, value, fidFilter);
    }

    /**
     * @see net.refractions.udig.project.internal.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {
        SimpleFeature feature = editFeature.get(monitor);
        feature.setAttribute(xpath, oldValue);
        ILayer layer = editLayer.get(monitor);
        FeatureStore<SimpleFeatureType, SimpleFeature> resource = layer.getResource(FeatureStore.class, null);
        AttributeDescriptor attributeType = layer.getSchema().getDescriptor(xpath);
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
		Id id = filterFactory.id(
                FeatureUtils.stringToId(filterFactory, feature.getID()));
        resource.modifyFeatures(attributeType, oldValue, id);
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return MessageFormat.format(
                Messages.SetAttributeCommand_setFeatureAttribute, new Object[]{xpath}); 
    }

    @Override
    public void setMap( IMap map ) {
        super.setMap(map);
    }
}
