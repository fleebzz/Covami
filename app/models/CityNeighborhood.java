package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class CityNeighborhood extends Model {

	public Long City_id;

	public Long neighborhood_id;

	public Double kms;

	// autoroute
	public String highway;

}
