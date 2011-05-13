package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import controllers.Application;
import controllers.Security;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class PendingAnnouncement extends Model {
	
	@Required
	@OneToOne
	public Announcement Announcement;

	@Required
	@OneToOne
	public Member applicant;
	
	@Required
	public Date publicationDate = new Date();
	
	public PendingAnnouncement(Announcement _announcement, Member _applicant) {
		this.Announcement = _announcement;
		this.applicant = _applicant;
	}
}
