package controllers;

import models.Member;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Members extends Controller {

	/**
	 * Visualiser le profile de l'utilisateur
	 */
	public static void profile() {
		Member member = Member.find("byEmail", Security.connected()).first();
		render(member);
	}
	
	public static void editProfile(@Required Member member) throws Throwable {

		Member m = Member.findById(member.id);
		
		m.email = member.email;
		m.firstname = member.firstname;
		m.lastname = member.lastname;
		
		m.save();
		
		profile();
	}
}