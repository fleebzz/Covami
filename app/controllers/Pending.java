package controllers;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import models.Member;
import models.MemberFriends;
import models.PendingInvitations;
import models.Vehicle;
import models.VehicleModel;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.mvc.Before;
import play.mvc.Controller;

public class Pending extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			Member user = Member.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user);
			renderArgs.put("security", Security.connected());
		}
	}

	public static void index() {
		Member member = Member.find("byEmail", Security.connected()).first();
//		List<PendingInvitations> pendingInvitations = PendingInvitations.find("byMember_id", member.id).fetch();
		List<Member> applicants = member.applicants;
		render(applicants);
	}
	
	public static void acceptInvitation(long applicantId){
		Member member = Member.find("byEmail", Security.connected()).first();
		Member applicant = Member.findById(applicantId);

		member.applicants.remove(applicant);
		member.save();
		
		(new MemberFriends(member.id, applicant.id)).save();
		(new MemberFriends(applicant.id, member.id)).save();

		member.friends.add(applicant);
		applicant.friends.add(member);
		
		Members.friends(null);
		
	}
	
	public static void denyInvitation(long applicantId){
		Member member = Member.find("byEmail", Security.connected()).first();
		Member applicant = Member.findById(applicantId);

		member.applicants.remove(applicant);
		member.save();

		Members.friends(null);
	}
}