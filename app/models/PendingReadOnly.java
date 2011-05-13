package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class PendingReadOnly extends Model {

	@Required
	@OneToOne
	public Member member;

	@Required
	public String type;

	@OneToOne
	public Announcement announcement;

	@OneToOne
	public Member applicant;
	
	public String description;

	public PendingReadOnly(Member _member) {
		this.member = _member;
	}
}
