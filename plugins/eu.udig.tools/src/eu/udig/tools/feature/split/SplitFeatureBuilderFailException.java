/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Wien Government 
 *
 *      http://wien.gov.at
 *      http://www.axios.es 
 *
 * (C) 2010, Vienna City - Municipal Department of Automated Data Processing, 
 * Information and Communications Technologies.
 * Vienna City agrees to license under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.feature.split;


/**
 * Custom exception used on {@link SplitFeatureBuilder}.
 * This exception is thrown if the split build fail.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.0
 */
public class SplitFeatureBuilderFailException extends SplitFeatureBuilderException {

	/* UID */
	private static final long	serialVersionUID	= 2977282794220494837L;

	/**
	 * Default constructor.
	 * 
	 * @param ex
	 *            Exception.
	 */
	public SplitFeatureBuilderFailException(String ex) {
		super(ex);
	}

}
