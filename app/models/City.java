package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import lib.Geo;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class City extends Model implements Comparable<City> {

	@Required
	public String name;

	@Column(name = "longitude", nullable = true)
	public double longitude;

	@Column(nullable = true)
	public double latitude;

	@Column(nullable = true)
	public int insee;

	@Column(nullable = true)
	public int postalCode;

	@OneToOne
	public Country country;

	@ManyToMany
	@JoinTable(name = "CityNeighborhood")
	public List<City> neighborhood;

	// Setters
	public void setName(String value) {
		if (value == null || value.length() == 0) {
			throw new IllegalArgumentException("Bad value");
		}

		this.name = value;
	}

	/**
	 * Calcule la distance entre 2 villes
	 * 
	 * @param to
	 *            Ville d'arrivée
	 * @return La distance en km
	 * @see com.fg.model.City#distanceBetween(City, City)
	 */
	public double distanceBetween(City to) {
		return City.distanceBetween(this, to);
	}

	/**
	 * Test si une ville est voisine
	 * 
	 * @param neighbour
	 *            La ville voisine
	 * @return True si neighbour est une ville voisine. False sinon
	 */
	public boolean hasNeighbour(City neighbour) {
		return this.neighborhood.contains(neighbour);
	}

	/**
	 * Test si la ville est la même que la ville b
	 * 
	 * @param b
	 *            La ville à tester
	 * @return True si les villes sont les mêmes, False sinon.
	 */
	public boolean equals(City b) {
		return City.equals(this, b);
	}

	/**
	 * Calcule la distance entre 2 villes
	 * 
	 * @param a
	 *            Ville de départ
	 * @param b
	 *            Ville d'arrivée
	 * @return La distance en km
	 * @see com.fg.comp.Geo#distanceBetween(double, double, double, double)
	 */
	public static double distanceBetween(City a, City b) {
		return Geo.distanceBetween(a.latitude, a.longitude, b.latitude,
				b.longitude);
	}

	/**
	 * Test si la ville a est la même que la ville b
	 * 
	 * @param a
	 *            Ville a
	 * @param b
	 *            Ville b
	 * @return True si les villes sont les mêmes, False sinon.
	 */
	public static boolean equals(City a, City b) {
		if (a == b) {// Même référence
			return true;
		}

		// Sinon test par valeur
		return a != null && b != null && a instanceof City && b instanceof City
				&& a.insee == b.insee;
	}

	/**
	 * Comparer une ville par rapport à une autre
	 * 
	 * @return 0 si égal, -1 si this >
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(City cityB) {
		return City.compare(this, cityB);
	}

	public static Integer compare(City cityA, City cityB) {
		if (cityA.equals(cityB)) {
			return 0;
		}

		return (cityA.insee <= cityB.insee) ? -1 : 1;
	}

	public static City max(City cityA, City cityB) {
		return City.compare(cityA, cityB) == 1 ? cityA : cityB;
	}

	public static City min(City cityA, City cityB) {
		return City.compare(cityA, cityB) == 1 ? cityB : cityA;
	}

	public static List<City> findAllOrderByName() {
		return City.find("order by name").fetch();
	}

}
