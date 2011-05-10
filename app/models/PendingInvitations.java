package models;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class PendingInvitations extends Model {

	@Required
	public Long Member_id;

	@Required
	public Long applicants_id;

	public PendingInvitations(Long memberId, Long applicantId) {
		this.Member_id = memberId;
		this.applicants_id = applicantId;
	}
}
