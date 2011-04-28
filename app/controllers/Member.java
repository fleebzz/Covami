package controllers;

import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Member extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			models.Member user = models.Member.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user);
			renderArgs.put("security", Security.connected());
		}
	}
	
	/**
	 * Visualiser le profile de l'utilisateur
	 */
	public static void profile(models.Member m) {
		models.Member member = models.Member.find("byEmail", Security.connected()).first();
		if(m.id != null){
			member = m;
		}
		renderArgs.put("model", member);
		render(member);
	}
}