package models;

import java.util.List;

import javassist.NotFoundException;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;
import tags.form.HiddenField;
import tags.form.IgnoreField;

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

	@IgnoreField
	@ManyToMany
	@JoinTable(name = "MemberFriends")
	public List<Member> friends;

  @IgnoreField
  @ManyToMany
  @JoinTable(name = "MemberReadOnlyPending")
  public List<PendingReadOnly> pendings;

	@IgnoreField
	@OneToMany
	public List<PendingInvitation> pendingInvitations;

	@IgnoreField
	@OneToMany
	public List<PendingAnnouncement> pendingAnnouncements;

	@IgnoreField
	@OneToMany
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

		MemberFriends.delete("Member_id = ? and friends_id = ?", member1.id,
				member2.id);

		MemberFriends.delete("Member_id = ? and friends_id = ?", member2.id,
				member1.id);

		member1.refresh();
		member2.refresh();

		return true;
	}
}