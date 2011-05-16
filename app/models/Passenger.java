package models;

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
	
	public Passenger(Announcement announcement, Member member){
		this.announcement = announcement;
		this.member = member;
	}
}
