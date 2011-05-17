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
	@OneToOne
	public City from;
	
	@Required
	@OneToOne
	public City to;
	
	public int nbPassengers;
	
	public int cost;
	
	@Required
	public Date publicationDate = new Date();
	
	public PendingAnnouncement(Announcement _announcement, Member _applicant, City from, City to, int nbPassengers) {
		this.Announcement = _announcement;
		this.applicant = _applicant;
		this.from = from;
		this.to = to;
		this.nbPassengers = nbPassengers;
	}

	public static PendingAnnouncement findByAnnouncementAndApplicant(Long announcementId, Long applicantId) {
		return PendingAnnouncement.find(
				"byAnnouncement_idAndApplicant_id", announcementId, applicantId)
				.first();
	}
}
