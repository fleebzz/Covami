package models;

<<<<<<< HEAD
import java.util.*;

import javax.persistence.*;

import play.data.validation.Required;
import play.db.jpa.*;
import tags.form.HiddenField;
=======
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.data.validation.Required;
import play.db.jpa.Model;
>>>>>>> 0abe8e90f864d3e7b5fcb26882a4392d4999b384

@Entity
public class Announcement extends Model {

	@Required
	@OneToOne
	public Member member;

	@Required
	@OneToOne
	public Vehicle vehicle;

	@Required
	@OneToOne
	public Trip trip;

	@Required
	public Date publicationDate;

	@Required
	public Date startDate;

	@Required
	public double kilometers;

	@Required
	public int freePlaces;

	@Required
<<<<<<< HEAD
	public float totalCost;

	@HiddenField
	@ManyToMany
	@JoinTable(name = "PendingAnnouncements")
	public List<Member> applicants;
=======
	public double totalCost;

>>>>>>> 0abe8e90f864d3e7b5fcb26882a4392d4999b384
}