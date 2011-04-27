package controllers;

import models.Member;

public class Security extends Secure.Security {

	static boolean authentify(String email, String password) {
		return Member.authentify(email, password);
	}

	// Fonction permettant de revenir à la page d'accueil après s'être
	// deconnecté
	static void onDisconnected() {
		Application.index();
	}
}