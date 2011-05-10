package models;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class PendingAnnouncements extends Model {

//	@Required
//	public Long Announcement_id;
//
//	@Required
//	public Long applicants_id;

	public PendingAnnouncements(Long announcementId, Long applicantId) {
		//super(announcementId, applicantId);
//		this.Announcement_id = announcementId;
//		this.applicants_id = applicantId;
	}
}
