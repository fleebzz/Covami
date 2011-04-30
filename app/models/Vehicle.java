package models;

import java.util.*;
import javax.persistence.*;

import play.data.validation.Required;
import play.db.jpa.*;

@Entity
public class Vehicle extends Model {
	@Required
	public String registration;
	
//	@Required
	public Date techControlDate;
	
	@Required
	@OneToOne
	public VehicleModel model;
	
}