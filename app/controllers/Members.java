package controllers;

import models.Member;
import play.data.validation.Required;
import play.libs.Crypto;
import play.mvc.Http;

public class Members extends CRUD {

	public static void signup() throws Throwable {
		Http.Cookie remember = request.cookies.get("rememberme");

		if (remember != null && remember.value.indexOf("-") > 0) {
			String sign = remember.value.substring(0,
					remember.value.indexOf("-"));
			String username = remember.value.substring(remember.value
					.indexOf("-") + 1);
			if (Crypto.sign(username).equals(sign)) {
				session.put("username", username);
			}
		}

		flash.keep("url");
		render();
	}

	// Une fois le formulaire soumis
	public static void dosignup(@Required Member m) throws Throwable {

		m.firstname = m.email;

		// Ajouter l'utilisateur à la bdd
		if (!m.validateAndCreate()) {
			flash.keep("url");
			flash.error("secure.error");
			params.flash();

			render("Members/signup.html", m);
			return;
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

		redirect("/");
	}
}