package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Passenger extends Model {

	@Required
	@OneToOne
	public Announcement announcement;

	@Required
	@OneToOne
	public Member member;
	
	public int nbPlaces;
	
	public Passenger(Announcement announcement, Member member, int nbPlaces){
		this.announcement = announcement;
		this.member = member;
		this.nbPlaces = nbPlaces;
	}

	public static Passenger findByAnnouncementAndMember(Long announcementId, Long memberId) {
		return Passenger.find("byAnnouncement_idAndMember_id", announcementId, memberId).first();
	}

	public static List<Passenger> findAllByAnnouncement(
			Announcement announcement) {
		return Passenger.find("byAnnouncement", announcement).fetch();
	}

	public static List<Passenger> findByAnnouncement(Long announcementId) {
		return Passenger.find("byAnnouncement_id", announcementId).fetch();
	}

	public static List<Passenger> findByMember(Long memberId) {
		return Passenger.find("byMember_id", memberId).fetch();
	}
}
