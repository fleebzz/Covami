package models;

import java.util.List;

import tags.form.HiddenField;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
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
	public int kilometers;

	@Required
	public int freePlaces;

	@HiddenField
	@OneToMany
//	@JoinTable(name = "Passenger")
	public List<Passenger> passengers;

	public int costByPassenger;
	
	public String color;

	public static List<Announcement> findByMember(long memberId) {
		return Announcement.find("byMember_id", memberId).fetch();
	}

	public static List<Announcement> findByMemberAndStartDateLessThanOrderByStartDate(
			Long memberId, Date date) {
		return Announcement.find("member_id = ? and startDate < ? order by startDate",
				memberId, date).fetch();
	}

	public static List<Announcement> findByMemberAndStartDateGreaterThanOrderByStartDate(
			Long memberId, Date date) {
		return Announcement.find("member_id = ? and startDate > ? order by startDate",
				memberId, date).fetch();
	}

	public static List<Announcement> findByStartDateGreaterThanAndStartDateLessThanOrderByStartDate(
			Date startDateMin, Date startDateMax) {
		return Announcement.find("startDate > ? and startDate < ? order by startDate", startDateMin, startDateMax).fetch();
	}

	public static List<Announcement> findAllOrderByStartDate() {
		return Announcement.find("order by startDate").fetch();
	}

	public static List<Announcement> findByStartDateGreaterThan(Date date) {
		return Announcement.find("startDate >= ?", date).fetch();
	}

	public static Announcement findByStartDateGreaterThanAndVehicle(Date date,
			Long vehicleId) {
		return Announcement.find("byStartDateGreaterThanAndVehicle_id", date, vehicleId).first();
	}
}