package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class PendingInvitation extends Model {

	@Required
	@OneToOne
	public Member Member;

	@Required
	@OneToOne
	public Member applicant;

	public Date publicationDate;

	public PendingInvitation(Member member, Member applicant) {
		this.Member = member;
		this.applicant = applicant;
		System.out.println(new Date());
	}
}
