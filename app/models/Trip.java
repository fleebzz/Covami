package models;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lib._Trip;
import lib._TripComparator;

import org.hibernate.annotations.IndexColumn;

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

	@ManyToMany
	@IndexColumn(name = "")
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
	 * @return Les listes des villes
	 */
	public List<City> getCities() {
		return cities;
	}

	/**
	 * Algorithme de recherche a* entre 2 villes
	 * 
	 * @param from
	 *            Ville de départ
	 * @param to
	 *            Ville d'arrivée
	 * @return Retourne la distance du plus court trajet entre les 2 villes
	 */
	public double generatePath() {

		if (this.cities != null) {
			this.cities.clear();
		}

		this.cities = new ArrayList();

		if (from == null || to == null) {
			return 0;
		}

		// Si le départ == l'arrivée
		if (from.equals(to)) {
			return 0;
		}

		_Trip curTrip = null;
		City curCity = null;
		// TripComparator tripComp = new TripComparator();
		TreeSet<_Trip> fileOpen = new TreeSet<_Trip>(new _TripComparator());

		// Ajouter la ville de départ dans la file
		fileOpen.add(new _Trip(this.getFrom(), this.getTo(), City
				.distanceBetween(this.getFrom(), this.getTo())));

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

				System.out.println("-- " + neighbour.name + " --");

				// Ajouter la route à la file
				if (!curTrip.cities.contains(neighbour)) {// Ne pas ajouter
															// une ville
															// déjà dans le
															// trajet

					_Trip t = curTrip.clone().addCity(neighbour,
							City.distanceBetween(neighbour, this.getTo()));
					System.out.println(t.toString());

					t.realDistance += cn.kms;
					fileOpen.add(t);

				}
			}

		} while (!fileOpen.isEmpty()
				&& !fileOpen.first().getLastCity().equals(this.getTo()));

		_Trip finalTrip = fileOpen.first();
		List<City> tmpCities = finalTrip.cities;

		// Maintenant il faut rechercher ces villes via un contexte
		// EntityManager
		// Sinon la sauvegarde ne pourra pas avoir lieu
		for (City c : tmpCities) {
			this.cities.add((City) City.find("byId", c.id).first());
		}

		return finalTrip.realDistance;
	}

	public City getFrom() {
		return this.from;
	}

	public City getTo() {
		return this.to;
	}

	/**
	 * Redéfinition de toString()
	 */
	@Override
	public String toString() {

		// Affiche le trajet
		String str = "Trajet de " + this.from.name + "(" + this.from.insee
				+ ") ";

		str += "à " + this.to.name + "(" + this.to.insee + "):\n";

		if (this.cities.size() == 0) {
			return str;
		}

		for (City c : this.cities) {
			str += "\n - " + c.insee + "\t" + c.name;
		}
		return str;
	}
}
