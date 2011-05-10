package models;

import java.util.List;

import tags.form.HiddenField;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

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

	@HiddenField
	@ManyToMany
	@JoinTable(name = "PendingAnnouncements")
	public List<Member> applicants;

	public double totalCost;
}