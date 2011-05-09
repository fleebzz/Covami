package models;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Trip extends Model {
	@Required
	@ManyToOne
	public City from;

	@Required
	@ManyToOne
	public City to;

	@Required
	public double distance;

	@OneToMany
	public List<City> cities;

	private double heuristic;

	private void setHeuristic(double heuristic) {
		this.heuristic = heuristic;
	}

	public double getHeuristic() {
		return heuristic;
	}

	/**
	 * Constructeur de Trajet
	 * 
	 * @param from
	 *            Ville de départ
	 * @param heuristic
	 *            Distance
	 */
	public Trip(City from, double heuristic) {
		this.cities = new ArrayList<City>();
		this.distance = 0;
		this.setHeuristic(heuristic);
		this.cities.add(from);
	}

	/**
	 * Constructeur de Trajet (utilisé par la méthode static aStar)
	 * 
	 * @param cities
	 * @param distance
	 */
	private Trip(List<City> cities, double distance, double heuristic) {
		this.cities = cities;
		this.distance = distance;
		this.heuristic = heuristic;
	}

	/**
	 * @return Les listes des villes
	 */
	public List<City> getCities() {
		return cities;
	}

	/**
	 * (chainable) Ajoute une ville au trajet
	 * 
	 * @param a
	 *            La ville à ajouter à la fin du trajet
	 * @return True si l'ajout à réussi, False sinon
	 */
	public Trip addCity(City a, double heuristic) {
		City lastCity = this.getLastCity();
		double distance = 0;

		if (lastCity != null) {
			distance = lastCity.distanceBetween(a);
		}

		// MAJ la distance parcourue
		this.distance = this.distance + distance;

		// MAJ le coup du chemin
		this.setHeuristic(this.distance + distance + heuristic);

		this.cities.add(a);

		return this;
	}

	/**
	 * Retourne la dernière ville d'un trajet
	 * 
	 * @return La dernière ville
	 */
	public City getLastCity() {
		if (this.cities.size() == 0) {
			return null;
		}

		return this.cities.get(this.cities.size() - 1);
	}

	public boolean generatePath() {
		return this.generatePath(from, to);
	}

	/**
	 * Algorithme de recherche a* entre 2 villes
	 * 
	 * @param from
	 *            Ville de départ
	 * @param to
	 *            Ville d'arrivée
	 * @return Retourne le plus court trajet entre ces deux villes
	 */
	public boolean generatePath(City from, City to) {
		if (this.cities != null) {
			this.cities.clear();
		}

		this.cities = new ArrayList();

		if (from == null || to == null) {
			return false;
		}

		// Si le départ == l'arrivée
		if (from.equals(to)) {
			this.cities.add(from);
			return true;
		}

		Trip curTrip = null;
		City curCity = null;
		// TripComparator tripComp = new TripComparator();
		TreeSet<Trip> fileOpen = new TreeSet<Trip>(new TripComparator());

		// Ajouter la ville de départ dans la file
		fileOpen.add(new Trip(from, City.distanceBetween(from, to)));

		do {

			curTrip = fileOpen.first();
			fileOpen.remove(curTrip);

			curCity = curTrip.getLastCity();// Prendre la dernière ville de la
											// route

			List<CityNeighborhood> cityNeighborhood = CityNeighborhood.find(
					"byCity_id", curCity.id).fetch();

			// Parcours des voisins
			for (CityNeighborhood cn : cityNeighborhood) {
				City neighbour = (City) City.find("byId", cn.neighborhood_id)
						.first();

				// Ajouter la route à la file
				if (!curTrip.getCities().contains(neighbour)) {// Ne pas ajouter
																// une ville
																// déjà dans le
																// trajet
					fileOpen.add(curTrip.clone().addCity(neighbour,
							City.distanceBetween(neighbour, to)));
				}
			}

		} while (!fileOpen.isEmpty()
				&& !fileOpen.first().getLastCity().equals(to));

		this.cities = fileOpen.first().cities;

		return true;
	}

	/**
	 * Clone un trajet
	 * 
	 * @return L'objet cloné
	 */
	@Override
	public Trip clone() {
		Trip t = Trip.clone(this);

		// FIX: Les modèles play semble avoir un problème avec le clone
		// d'entités
		// contenant des List<T>
		t.cities = (List<City>) ((ArrayList<City>) t.cities).clone();
		return t;
	}

	/**
	 * Clone un trajet
	 * 
	 * @return L'objet cloné
	 */
	@SuppressWarnings("unchecked")
	public static Trip clone(Trip a) {
		return new Trip(a.getCities(), a.distance, a.getHeuristic());
	}

	/**
	 * Redéfinition de toString()
	 */
	@Override
	public String toString() {
		// Affiche le trajet
		String str = "Trajet de " + this.cities.get(0).name + "("
				+ this.cities.get(0).insee + ") ";

		str += "à " + this.cities.get(this.cities.size() - 1).name + "("
				+ this.cities.get(this.cities.size() - 1).insee + "):\n";

		for (City c : this.cities) {
			str += "\n - " + c.insee + "\t" + c.name;
		}
		return str;
	}
}
