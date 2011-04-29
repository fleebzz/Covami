package controllers;

import models.Member;
import play.data.validation.Valid;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Members extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			models.Member user = models.Member.find("byEmail",
					Security.connected()).first();
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
			// FIXME: Ajouter ce message dans les traductions
			flash.success("Membre enregistré avec succès");
			Application.index();
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
		// Member florian = Member.find("byFirstname", "Florian").first();
		// member.friends.add(florian);
		// member.save();
		render(member.friends);
	}
}