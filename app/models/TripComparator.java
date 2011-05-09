package models;

import java.util.Comparator;

/**
 * Classe représentant une ArrayList triée par l'heuristique des Trip
 * 
 * @author FG
 * 
 */
public class TripComparator implements Comparator<Trip> {

	private static final long serialVersionUID = 5439474719803196476L;

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Trip a, Trip b) {
		return Double.compare(a.getHeuristic(), b.getHeuristic());
	}

}
