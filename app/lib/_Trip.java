package lib;

import java.util.ArrayList;
import java.util.List;

import models.City;

public class _Trip {

	public City from;

	public City to;

	public List<City> cities;

	private double distance;

	public double realDistance;
	private double heuristic;

	public _Trip(City from, City to, double distance) {
		this.distance = distance;
		this.from = from;
		this.to = to;
		this.heuristic = 0;
		this.realDistance = 0;
		this.cities = new ArrayList<City>();
		this.cities.add(from);
	}

	public _Trip(List<City> cities, double distance, double heuristic,
			City from, City to, double realDistance) {
		this.cities = cities;
		this.from = from;
		this.distance = distance;
		this.heuristic = heuristic;
		this.to = to;
		this.realDistance = realDistance;
	}

	public double getHeuristic() {
		return this.heuristic;
	}

	public void setHeuristic(double heuristic) {
		this.heuristic = heuristic;
	}

	public City getLastCity() {
		if (this.cities.size() == 0) {
			return null;
		}

		return this.cities.get(this.cities.size() - 1);
	}

	@Override
	public _Trip clone() {
		return new _Trip((List<City>) ((ArrayList<City>) this.cities).clone(),
				this.distance, this.getHeuristic(), this.from, this.to,
				this.realDistance);
	}

	public _Trip addCity(City a, double heuristic) {
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