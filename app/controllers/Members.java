package controllers;

import models.Member;
import play.data.validation.Valid;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Members extends Controller {

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
}