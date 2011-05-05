package controllers;

import java.util.List;

import models.Member;
import models.MemberFriends;
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
		// List<PendingInvitations> pendingInvitations =
		// PendingInvitations.find("byMember_id", member.id).fetch();
		List<Member> applicants = member.applicants;
		render(applicants);
	}

	/**
	 * Accepter une demande d'amitié
	 * 
	 * @param applicantId
	 */
	public static void accept(long applicantId) {
		Member member = Member.find("byEmail", Security.connected()).first();
		Member applicant = Member.findById(applicantId);

		member.applicants.remove(applicant);
		member.save();

		(new MemberFriends(member.id, applicant.id)).save();
		(new MemberFriends(applicant.id, member.id)).save();

		member.friends.add(applicant);
		applicant.friends.add(member);

		Members.listFriends(null);

	}

	/**
	 * Refuser une demande d'amitié
	 * 
	 * @param applicantId
	 */
	public static void deny(long applicantId) {
		Member member = Member.find("byEmail", Security.connected()).first();
		Member applicant = Member.findById(applicantId);

		member.applicants.remove(applicant);
		member.save();

		Members.listFriends(null);
	}
}