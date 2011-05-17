package models;

import java.util.Date;
import java.util.List;

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
	}

	public static List<PendingInvitation> findByApplicant(Long memberId) {
		return PendingInvitation.find("byApplicant_id", memberId).fetch();
	}

	public static PendingInvitation findByMemberAndApplicant(Long memberId, Long applicantId) {
		return PendingInvitation.find("byMember_idAndApplicant_id", memberId, applicantId).first();
	}
}
