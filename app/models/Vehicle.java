package models;

import java.util.*;
import javax.persistence.*;

import play.data.validation.Required;
import play.db.jpa.*;
import tags.form.HiddenField;

@Entity
public class Vehicle extends Model {
	@Required
	public String registration;
	
//	@Required
	public Date techControlDate;
	
	@Required
	@OneToOne
	@HiddenField
	public VehicleModel model;
	
}