package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.data.validation.Email;
import play.data.validation.MaxSize;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Member extends Model {
	@Email
	@MaxSize(200)
	@Required(message = "member.bademail")
	public String email;

	@Password
	@Required(message = "member.badpassword")
	@MaxSize(15)
	public String password;

	@MaxSize(100)
	public String firstname;

	@Required
	@MaxSize(100)
	public String lastname;

	@ManyToMany
	public List<Member> friends;

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
}