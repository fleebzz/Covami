package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

import play.data.validation.Required;
import play.db.jpa.Model;
import tags.form.HiddenField;

@Entity
public class Vehicle extends Model {
	@Required
	public String registration;

	// @Required
	@Type(type = "date")
	public Date techControlDate;

	@Required
	@OneToOne
	@HiddenField
	public VehicleModel model;

}