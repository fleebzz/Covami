/**
 * Geo v0.1, by Francois-Guillaume Ribreau.
 *
 * Copyright (c)2010 Francois-Guillaume Ribreau. All rights reserved.
 * Released under the Creative Commons BY-SA Conditions.
 * http://creativecommons.org/licenses/by-sa/3.0/
 *
 * Usage :
 * Geo::distanceBetween(lat1, lon1, lat2, lon2);
 */

package lib;

import java.util.Hashtable;

/**
 * @author FG
 * 
 */
public class Geo {

	private final double lonMin;
	private final double lonMax;
	private final double latMin;
	private final double latMax;

	public Geo(double lonMin, double lonMax, double latMin, double latMax) {
		this.lonMin = lonMin;
		this.lonMax = lonMax;
		this.latMin = latMin;
		this.latMax = latMax;
	}

	public int lonToX(double width, double lon) {
		return (int) (width * (lon - this.lonMin) / (this.lonMax - this.lonMin));
	}

	public int latToY(double height, double lat) {
		return (int) (height - (height * (lat - this.latMin) / (this.latMax - this.latMin)));
	}

	/**
	 * Chaque distance entre 2 points est mise en cache
	 */
	private static Hashtable<String, Double> cache = new Hashtable<String, Double>();

	/**
	 * Calcule la distance à vol d'oiseau entre 2 points géo-localisé
	 * 
	 * @param lat1
	 *            Latitude du point 1
	 * @param lon1
	 *            Longitute du point 1
	 * @param lat2
	 *            Latitude du point 2
	 * @param lon2
	 *            Longitute du point 2
	 * @return la distance en miles nautique
	 * @see <a
	 *      href="http://www.codeguru.com/Cpp/Cpp/algorithms/article.php/c5115">Geographic
	 *      Distance and Azimuth Calculations</a>
	 */

	public static double distanceBetween(double lat1, double lon1, double lat2,
			double lon2) {
		String key = String.format("%f%f%f%f", Math.max(lat1, lat2),
				Math.max(lon1, lon2), Math.min(lat1, lat2),
				Math.min(lon1, lon2));

		if (cache.containsKey(key)) {
			return cache.get(key);
		}

		// Multipler la longitude & lattitude par 0.01745329252
		lat1 *= 0.01745329252;
		lon1 *= 0.01745329252;
		lat2 *= 0.01745329252;
		lon2 *= 0.01745329252;
		Double value = Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		if (Double.isNaN(value)) {// Se produit lorsque lat1:lon1 == lat2:lon2
			value = 0.0;
		} else {
			// Ne pas mettre en cache une mauvaise valeur
			value *= 1.852 * 6378.135;
			cache.put(key, value);
		}
		return value;
	}
}
