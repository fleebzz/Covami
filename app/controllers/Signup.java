package controllers;

import models.Member;
import play.data.validation.Required;
import play.mvc.Controller;


public class Signup extends Controller {

	/**
	 * Formulaire d'inscription
	 */
	public static void index() throws Throwable {
		render();
	}

	/**
	 * Une fois le formulaire soumis
	 */
	public static void dosignup(@Required Member m) throws Throwable {

		Member existMember = Member.findByEmail(controllers.Secure.Security.connected());
		
		if(existMember.count() > 1){
			flash.error("members.signup.alreadyExist");
			index();
		}
		
		// Ajouter l'utilisateur à la bdd
		if (!m.validateAndCreate()) {
			flash.keep("url");
			flash.error("secure.error");
			params.flash();
			
			index();
		}

		// Connecter l'utilisateur
		if (!Security.authentify(m.email, m.password)) {
			flash.error("members.errorAuthenticate");
			render("Members/signup.html", m);
			return;
		}

		// Tout est ok
		session.put("username", m.email);
		flash.success("members.signup.success");
		
		Application.index();
	}

}