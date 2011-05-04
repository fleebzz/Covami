package models;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class MemberFriends extends Model {

	// public Long id;

	@Required
	public Long Member_id;

	@Required
	public Long friends_id;

	public MemberFriends(Long member1Id, Long member2Id) {
		this.Member_id = member1Id;
		this.friends_id = member2Id;
	}
	/*
	 * 
	 * INSERT INTO `MemberFriends` (`Member_id`, `friends_id`) VALUES (12, 11),
	 * (12, 9), (11, 12), (11, 9), (9, 11), (9, 12);
	 */
}
