package controllers;

import models.Member;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Live extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			Member user = Member.findByEmail(controllers.Secure.Security.connected());
			renderArgs.put("user", user);
			renderArgs.put("security", Security.connected());
		}
	}
	
	public static void index() {
		render();
	}
}
