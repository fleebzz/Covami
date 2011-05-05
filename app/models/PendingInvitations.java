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

	public PendingInvitations(Long member1Id, Long applicantId) {
		this.Member_id = member1Id;
		this.applicants_id = applicantId;
	}
}
