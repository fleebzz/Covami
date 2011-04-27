package controllers;

import models.Member;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			Member user = Member.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user);
			renderArgs.put("security", Security.connected());
		} else {
			renderArgs.put("user", new Member("", "", "John", "Doe"));
		}
	}

	public static void index() {
		render();
	}
}