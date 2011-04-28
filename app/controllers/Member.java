package controllers;

import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Member extends Controller {

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