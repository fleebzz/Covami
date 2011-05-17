package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class PendingReadOnly extends Model {

	@Required
	@OneToOne
	public Member member;

	@Required
	public String type;

	@OneToOne
	public Announcement announcement;

	@OneToOne
	public Member applicant;
	
	public String description;

	public PendingReadOnly(Member _member) {
		this.member = _member;
	}

	public static PendingReadOnly findByAnnouncementAndMember(long announcementId, Long memberId) {
		return PendingReadOnly.find("byAnnouncement_idAndMember_id", announcementId, memberId).first();
	}

	public static List<PendingReadOnly> findByMember(Long memberId) {
		return PendingReadOnly.find("byMember_id", memberId).fetch();
	}
}
