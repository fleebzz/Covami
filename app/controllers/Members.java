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
	public static void profile(Member m) {
		Member member = Member.find("byEmail", Security.connected()).first();
		if (m.id != null) {
			member = m;
		}

		renderArgs.put("model", member);
		render();
	}

	public static void editProfile(@Valid Member member) throws Throwable {
		validation.valid(member);
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			profile(member);
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

	public static void friends(Member m) {
		Member member = Member.find("byEmail", Security.connected()).first();
		if (m.id != null) {
			member = m;
		}
		List<Member> friends = member.friends;
		// Member florian = Member.find("byFirstname", "Florian").first();
		// member.friends.add(florian);
		// member.save();
		// florian.friends.add(member);
		// florian.save();
		render(friends);
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

		friends(null);
	}

	/**
	 * Envoi une demande d'invitation à un ami
	 */
	public static void inviteFriend(int friendId) {
		// stub
		flash.success("members.inviteFriendSuccess");
		redirect("/");
	}

	public static void findFriends(String s) {
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
		render(members);
	}

	public static void seeProfile(long id) {
		models.Member model = null;

		if (id != 0) {
			model = models.Member.find("byId", id).first();
		} else {
			model = models.Member.find("byEmail", Security.connected()).first();
		}

		renderArgs.put("me", model.email.equals(Security.connected()));
		renderArgs.put("model", model);
		render();
	}
}