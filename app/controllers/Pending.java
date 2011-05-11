package controllers;

import java.util.List;

import models.Announcement;
import models.Member;
import models.MemberFriends;
import models.PendingAnnouncement;
import models.PendingInvitation;
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
		List<PendingInvitation> pendingInvitations = member.pendingInvitations;
		List<PendingAnnouncement> pendingAnnouncements = member.pendingAnnouncements;
		renderArgs.put("pendingInvitations", pendingInvitations);
		renderArgs.put("pendingAnnouncements", pendingAnnouncements);
		render();
	}

	/**
	 * Accepter une demande d'amitié
	 * 
	 * @param applicantId
	 */
	public static void acceptFriend(long applicantId) {
		Member member = Member.find("byEmail", Security.connected()).first();
		Member applicant = Member.findById(applicantId);
		
		PendingInvitation invitation = PendingInvitation.find("byMember_idAndApplicant_id", member.id, applicant.id).first();
		member.pendingInvitations.remove(invitation);
		member.save();
		invitation.delete();

		(new MemberFriends(member.id, applicant.id)).save();
		(new MemberFriends(applicant.id, member.id)).save();

		member.friends.add(applicant);
		applicant.friends.add(member);

		Pending.index();
	}

	/**
	 * Refuser une demande d'amitié
	 * 
	 * @param applicantId
	 */
	public static void denyFriend(long applicantId) {
		Member member = Member.find("byEmail", Security.connected()).first();
		Member applicant = Member.findById(applicantId);

		PendingInvitation invitation = PendingInvitation.find("byMember_idAndApplicant_id", member.id, applicant.id).first();
		member.pendingInvitations.remove(invitation);
		member.save();
		invitation.delete();

		Pending.index();
	}

	public static void acceptAnnouncement(long pendingAnnouncementId) {
		Member member = Member.find("byEmail", Security.connected()).first();
//		Member applicant = Member.findById(applicantId);

		PendingAnnouncement pendingAnnouncement = PendingAnnouncement.findById(pendingAnnouncementId);
		pendingAnnouncement.Announcement.passengers.add(pendingAnnouncement.applicant);
		pendingAnnouncement.Announcement.save();
//		member.pendingInvitations.remove(invitation);
//		member.save();
//		invitation.delete();
//
//		(new MemberFriends(member.id, applicant.id)).save();
//		(new MemberFriends(applicant.id, member.id)).save();

		member.pendingAnnouncements.remove(pendingAnnouncement);
		member.save();
		pendingAnnouncement.delete();
		Pending.index();
	}

	public static void denyAnnouncement(long pendingAnnouncementId) {
		Member member = Member.find("byEmail", Security.connected()).first();

		PendingAnnouncement pendingAnnouncement = PendingAnnouncement.findById(pendingAnnouncementId);
		member.pendingAnnouncements.remove(pendingAnnouncement);
		member.save();
		pendingAnnouncement.delete();

		Pending.index();
	}
}