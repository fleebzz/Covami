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
	
	public int nbPlaces;
	
	public Passenger(Announcement announcement, Member member, int nbPlaces){
		this.announcement = announcement;
		this.member = member;
		this.nbPlaces = nbPlaces;
	}
}
