package controllers;

import java.util.List;

import javassist.NotFoundException;
import models.Member;
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
			Member user = Member.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user);
			renderArgs.put("security", Security.connected());
		}
	}

	/**
	 * Visualiser le profile de l'utilisateur
	 */
	public static void editProfile(Member m) {
		Member member = Member.find("byEmail", Security.connected()).first();
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
			profile();

		}
	}

	/**
	 * Visualiser le profile de l'utilisateur
	 */
	public static void profile() {
		models.Member model = models.Member.find("byEmail",
				Security.connected()).first();

		renderArgs.put("model", model);

		render();
	}

	public static void listFriends(Member m) {
		Member member = Member.find("byEmail", Security.connected()).first();
		if (m.id != null) {
			member = m;
		}
		List<Member> friends = member.friends;

		renderArgs.put("friends", friends);

		render();
	}

	public static void deleteFriend(long friendId) throws NotFoundException {

		// Récupérer l'user actuel
		Member member = Member.find("byEmail", Security.connected()).first();
		Member friend = Member.find("byId", friendId).first();

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
		Member member1 = Member.find("byEmail", Security.connected()).first();
		Member member2 = Member.find("byId", friendId).first();

		if (member1 != null && member2 != null && member1.id != member2.id) {
			member2.applicants.add(member1);
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
	 * @param s
	 */
	public static void findFriends(String s) {
		Member member = Member.find("byEmail", Security.connected()).first();
		List<Member> members = null;

		if (s == null || s.isEmpty() || s.length() < 3) {

			if (s != null && s.length() <= 4) {
				flash.error("recherche trop petite");
				s = "";
			}

			members = Member.all().fetch(maxMembersToFind);
		} else {
			// ByFirstnameLikeOrLastnameLike ne fonctionnait pas
			members = Member.find("firstname like ? or lastname like ?",
					"%" + s + "%", "%" + s + "%").fetch(maxMembersToFind);
		}

		renderArgs.put("s", s);
		renderArgs.put("members", members);
		renderArgs.put("applicants", member.applicants);

		render();
	}

	/**
	 * Affiche le profile d'un utilisateur
	 * 
	 * @param id
	 */
	public static void seeProfile(long id) {
		Member member = Member.find("byEmail", Security.connected()).first();
		boolean isApplicant = false;
		Member model = null;

		if (id != 0) {
			model = Member.find("byId", id).first();
		} else {
			model = member;
		}

		if (member.applicants.contains(model)) {
			isApplicant = true;
		}

		renderArgs.put("me", model.email.equals(Security.connected()));
		renderArgs.put("model", model);
		renderArgs.put("isApplicant", isApplicant);

		render();
	}
}