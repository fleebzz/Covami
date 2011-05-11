package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Passenger extends Model {

	public Long Announcement_id;

	public Long passengers_id;

}
