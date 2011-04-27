package controllers;

import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Members extends Controller {

	/**
	 * Visualiser le profile de l'utilisateur
	 */
	public static void profile() {

	}
}