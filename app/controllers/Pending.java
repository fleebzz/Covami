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
		List<Passenger> memberPassengerAnnouncements = Passenger.find("byMember_id", member.id).fetch();
		List<Announcement> pendingsReadOk = new ArrayList<Announcement>();
		List<Announcement> comingAnnouncements = new ArrayList<Announcement>();
		for (Passenger memberPassengerAnnouncement : memberPassengerAnnouncements) {
			Announcement announcement = Announcement.findById(memberPassengerAnnouncement.announcement.id);
			if(announcement.startDate.after(new Date())) {
				comingAnnouncements.add(announcement);
			}
			for (PendingReadOnly pendingReadOnly : member.pendings) {
				if(pendingReadOnly.announcement != null && pendingReadOnly.announcement.id == announcement.id) {
					pendingsReadOk.add(announcement);
				}
			}
		}
		
		List<PendingReadOnly> pendingsReadOnly = PendingReadOnly.find("byMember_id", member.id).fetch();
		
		int existDeleteOrDesist = 0;
		
		for (PendingReadOnly pendingReadOnly : pendingsReadOnly) {
			System.out.println(pendingReadOnly.type);
			if(pendingReadOnly.type.equalsIgnoreCase("deleteAnnouncement") || pendingReadOnly.type.equalsIgnoreCase("desistParticipation")){
				existDeleteOrDesist = 1;
			}
		}
		
		renderArgs.put("pendingInvitations", pendingInvitations);
		renderArgs.put("pendingAnnouncements", pendingAnnouncements);
		renderArgs.put("pendingsReadOk", pendingsReadOk);
		renderArgs.put("pendingsReadOnly", pendingsReadOnly);
		renderArgs.put("comingAnnouncements", comingAnnouncements);
		renderArgs.put("existDeleteOrDesist", existDeleteOrDesist);
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
		
		if(pendingAnnouncement.nbPassengers > pendingAnnouncement.Announcement.freePlaces){
			flash.error("pendings.announcements.full");
			Pending.index();
		}
		
		Passenger passenger = new Passenger(pendingAnnouncement.Announcement, pendingAnnouncement.applicant, pendingAnnouncement.nbPassengers);
		passenger.save();
		pendingAnnouncement.Announcement.passengers.add(passenger);
		pendingAnnouncement.Announcement.save();

		member.pendingAnnouncements.remove(pendingAnnouncement);
		member.save();
		pendingAnnouncement.delete();
		
		pendingAnnouncement.Announcement.freePlaces -= pendingAnnouncement.nbPassengers;
		pendingAnnouncement.Announcement.save();
		
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
		member.pendings.remove(pending);
		member.save();
		pending.delete();
		
		Pending.index();
	}

	public static void pendingReadOnlyOkDelete(long pendingReadOnlyId) {
		Member member = Member.find("byEmail", Security.connected()).first();
		
		PendingReadOnly pending = PendingReadOnly.findById(pendingReadOnlyId);
		
		member.pendings.remove(pending);
		member.save();
		
		pending.delete();
		
		Pending.index();
	}
}