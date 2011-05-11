package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Announcement;
import models.Member;
import models.MemberFriends;
import models.Passenger;
import models.PendingAnnouncement;
import models.PendingInvitation;
import models.PendingReadOnly;
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
		List<Passenger> memberPassengerAnnouncements = Passenger.find("byPassengers_id", member.id).fetch();
		List<Announcement> comingAnnouncementsReadOk = new ArrayList<Announcement>();
		List<Announcement> comingAnnouncements = new ArrayList<Announcement>();
		System.out.println(memberPassengerAnnouncements.size());
		for (Passenger memberPassengerAnnouncement : memberPassengerAnnouncements) {
			Announcement announcement = Announcement.findById(memberPassengerAnnouncement.Announcement_id);
			if(announcement.startDate.after(new Date())) {
				comingAnnouncements.add(announcement);
			}
			for (PendingReadOnly pendingReadOnly : member.pendings) {
				if(pendingReadOnly.announcement == announcement) {
					comingAnnouncementsReadOk.add(announcement);
				}
			}
		}

		System.out.println(comingAnnouncements.size());

		renderArgs.put("pendingInvitations", pendingInvitations);
		renderArgs.put("pendingAnnouncements", pendingAnnouncements);
		renderArgs.put("comingAnnouncementsReadOk", comingAnnouncementsReadOk);
		renderArgs.put("comingAnnouncements", comingAnnouncements);
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

		PendingAnnouncement pendingAnnouncement = PendingAnnouncement.findById(pendingAnnouncementId);
		pendingAnnouncement.Announcement.passengers.add(pendingAnnouncement.applicant);
		pendingAnnouncement.Announcement.save();

		member.pendingAnnouncements.remove(pendingAnnouncement);
		member.save();
		pendingAnnouncement.delete();
		
		PendingReadOnly pending = new PendingReadOnly(pendingAnnouncement.applicant);
		pending.type = "announcementParticipation";
		pending.announcement = pendingAnnouncement.Announcement;
		pending.save();
		
		pendingAnnouncement.applicant.pendings.add(pending);
		pendingAnnouncement.applicant.save();
		
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

	public static void pendingReadOnlyOk(long announcementId) {
		Member member = Member.find("byEmail", Security.connected()).first();
		
		PendingReadOnly pending = PendingReadOnly.find("byAnnouncement_idAndMember_id", announcementId, member.id).first();
//		System.out.println("-------------------Type : " + pending.type);
		member.pendings.remove(pending);
		member.save();
		pending.delete();
		
		Pending.index();
	}
}