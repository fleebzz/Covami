package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class MemberFriends extends Model {

	public int Member_id;

	public int friends_id;

	/*
	 * 
	 * INSERT INTO `MemberFriends` (`Member_id`, `friends_id`) VALUES (12, 11),
	 * (12, 9), (11, 12), (11, 9), (9, 11), (9, 12);
	 */
}
