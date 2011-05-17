package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javassist.NotFoundException;
import models.Announcement;
import models.Member;
import models.PendingInvitation;
import play.data.validation.Valid;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Members extends Controller {
	// Récupérer 50 membres au maximum
	private static int maxMembersToFind = 50;

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			Member user = Member.findByEmail(controllers.Secure.Security.connected());
			renderArgs.put("user", user);
			renderArgs.put("security", Security.connected());
		}
	}

	/**
	 * Visualiser le profile de l'utilisateur
	 */
	public static void editProfile(Member m) {
		Member member = Member.findByEmail(controllers.Secure.Security.connected());
		if (m.id != null) {
			member = m;
		}

		renderArgs.put("model", member);

		render();
	}

	public static void editProfilePost(@Valid Member member) throws Throwable {
		validation.valid(member);
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			Members.editProfile(member);

		} else {
			member.save();
			flash.success("member.profile.success");
			editProfile();
		}
	}

	/**
	 * Visualiser le profile de l'utilisateur
	 */
	public static void editProfile() {
		Member member = Member.findByEmail(controllers.Secure.Security.connected());

		renderArgs.put("model", member);

		render();
	}

	public static void listFriends(Member m) {
		Member member = Member.findByEmail(controllers.Secure.Security.connected());
		if (m.id != null) {
			member = m;
		}
		List<Member> friends = member.friends;

		renderArgs.put("friends", friends);

		render();
	}

	public static void deleteFriend(long friendId) throws NotFoundException {

		// Récupérer l'user actuel
		Member member = Member.findByEmail(controllers.Secure.Security.connected());
		Member friend = Member.findById(friendId);

		if (Member.removeFriend(member, friend)) {
			flash.success("members.deleteFriendSuccess");
		} else {
			flash.error("members.deleteFriendFailed");
		}

		Members.listFriends(null);
	}

	/**
	 * Envoi une demande d'invitation à un ami
	 */
	public static void inviteFriend(long friendId) {
		Member member1 = Member.findByEmail(controllers.Secure.Security.connected());
		Member member2 = Member.findById(friendId);

		if (member1 != null && member2 != null && member1.id != member2.id) {
			PendingInvitation invitation = new PendingInvitation(member2, member1);
			invitation.save();
			member2.pendingInvitations.add(invitation);
			member2.save();

			flash.success("members.inviteFriendSuccess");
		} else {
			flash.error("members.inviteFriendError");
			Members.findFriends(null);
		}

		Members.listFriends(null);
	}

	/**
	 * Trouver des amis
	 * 
	 * - Ne pas afficher l'utilisateur connecté dans la liste.
	 * 
	 * - Si le membre à fait une demande d'amitié, afficher les boutons
	 * 
	 * - Si l'utilisateur à fait une demandé d'amitié pour le membre et qu'il
	 * n'a pas encore répondu, afficher "demandé d'amitié envoyée"
	 * 
	 * - Si le membre est déjà un amis, ne rien afficher comme bouton
	 * 
	 * @param s
	 */
	public static void findFriends(String s) {
		Member member = Member.findByEmail(controllers.Secure.Security.connected());
		List<Member> members = null;

		if (s == null || s.isEmpty() || s.length() < 3) {

			if (s != null && s.length() <= 4) {
				flash.error("recherche trop petite");
				s = "";
			}

			members = Member.all().fetch(maxMembersToFind);
		} else {
			// ByFirstnameLikeOrLastnameLike ne fonctionnait pas
			members = Member.findByFirstnameOrLastnameMax(s, s, maxMembersToFind);
		}
		
		List<PendingInvitation> pendingInvitations = PendingInvitation.findByApplicant(member.id);
		List<Member> pendings = new ArrayList<Member>();
		for (PendingInvitation pi : pendingInvitations) {
			if (members.contains(pi.Member)) {
				pendings.add(pi.Member);
			}
		}
		
		List<PendingInvitation> pendingApplicants = member.pendingInvitations;
		List<Member> applicants = new ArrayList<Member>();
		for (PendingInvitation pa : pendingApplicants) {
			applicants.add(pa.applicant);
		}
		
		renderArgs.put("pendings", pendings);
		renderArgs.put("applicants", applicants);

		renderArgs.put("s", s);
		renderArgs.put("members", members);
		renderArgs.put("me", member);

		render();
	}

	/**
	 * Affiche le profile d'un utilisateur
	 * 
	 * @param id
	 */
	public static void seeProfile(long id) {
		Member member = Member.findByEmail(controllers.Secure.Security.connected());
		Member memberToSee = null;

		if (id != 0) {
			memberToSee = Member.findById(id);
		} else {
			memberToSee = member;
		}

		List<Member> applicants = new ArrayList<Member>();
		List<Member> pendings = new ArrayList<Member>();

		PendingInvitation applicantsTemp = PendingInvitation.findByMemberAndApplicant(member.id, memberToSee.id);
		PendingInvitation pendingsTemp = PendingInvitation.findByMemberAndApplicant(memberToSee.id, member.id);

		if(applicantsTemp != null){
			applicants.add(memberToSee);
		}

		if(pendingsTemp != null){
			pendings.add(memberToSee);
		}
		
		List<Announcement> nextAnnouncements = Announcement.findByMemberAndStartDateGreaterThanOrderByStartDate(memberToSee.id, new Date());
		
		System.out.println(nextAnnouncements.size());

		renderArgs.put("pendings", pendings);
		renderArgs.put("applicants", applicants);
		renderArgs.put("me", member);
		renderArgs.put("member", memberToSee);
		renderArgs.put("nextAnnouncements", nextAnnouncements);

		render();
	}
}