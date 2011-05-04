package models;

import java.util.List;

import javassist.NotFoundException;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import tags.form.HiddenField;

@Entity
public class Member extends Model {
	@Email
	@MaxSize(200)
	@Required(message = "member.bademail")
	public String email;

	@HiddenField
	public String password;

	@MaxSize(100)
	public String firstname;

	@MaxSize(100)
	public String lastname;

	@HiddenField
	@ManyToMany
	@JoinTable(name = "MemberFriends")
	public List<Member> friends;

	@HiddenField
	@ManyToMany
	@JoinTable(name = "MemberVehicles")
	public List<Vehicle> vehicles;

	// FIXME: Insérer la bonne regex
	/* @Match() */
	public String phone;

	public Member(String email, String password, String firstname,
			String lastname) {
		this.email = email;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public static Member connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}

	public static boolean authentify(String email, String password) {
		return Member.connect(email, password) != null;
	}

	@Override
	public String toString() {
		return firstname + " " + lastname;
	}

	public boolean removeFriend(Member friend) throws NotFoundException {
		return removeFriend(this, friend);
	}

	public static boolean removeFriend(Member member1, Member member2)
			throws NotFoundException {

		if (member1 == null || member2 == null) {
			return false;
		}

		/*
		 * Model.em().remove( Model.em() .createQuery(
		 * "select m from MemberFriends m where m.Member_id =" +
		 * String.valueOf(member1.id) + " and m.friends_id = " +
		 * String.valueOf(member2.id)) .getSingleResult());
		 * 
		 * Model.em().remove( Model.em() .createQuery(
		 * "select m from MemberFriends m where m.Member_id =" +
		 * String.valueOf(member2.id) + " and m.friends_id = " +
		 * String.valueOf(member1.id)) .getSingleResult());
		 */

		MemberFriends.delete("Member_id = ? and friends_id = ?",
				member1.id.intValue(), member2.id.intValue());

		MemberFriends.delete("Member_id = ? and friends_id = ?",
				member2.id.intValue(), member1.id.intValue());
		/*
		 * Model.em() .createQuery(
		 * "delete from MemberFriends m where m.Member_id = ? and m.friends_id = ?"
		 * ) .setParameter(0, member1.id).setParameter(1, member2.id);
		 */
		/*
		 * Model.em().createQuery(
		 * "delete from MemberFriends m where m.Member_id =" +
		 * String.valueOf(member2.id) + " and m.friends_id = " +
		 * String.valueOf(member1.id));
		 */
		// Model.em().remove(Model.em().find("", member2.id, member1.id));

		member1.refresh();
		member2.refresh();

		/*
		 * int posFriend = member1.friends.indexOf(member2); // Trouver l'ami if
		 * (posFriend >= 0) { member1.friends.get(posFriend).delete(); } else {
		 * throw new NotFoundException(
		 * "Member2 not found in Member1's friends"); }
		 * 
		 * posFriend = member2.friends.indexOf(member1); // Trouver l'ami if
		 * (posFriend >= 0) { member2.friends.get(posFriend).delete(); } else {
		 * throw new NotFoundException(
		 * "Member1 not found in Member2's friends"); }
		 */

		return true;
	}
}