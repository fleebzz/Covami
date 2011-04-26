package models;

import java.util.*;
import javax.persistence.*;

import play.data.validation.Required;
import play.db.jpa.*;

@Entity
public class VehicleModel extends Model {
	@Required
	public String make;
	
	@Required
	public String model;
	
	@Required
	public int nbPlaces;
}