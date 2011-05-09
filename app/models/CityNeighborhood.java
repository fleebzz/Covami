package models;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class CityNeighborhood extends Model {

	@Required
	public Long City_id;

	@Required
	public Long neighborhood_id;

}
