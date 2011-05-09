package controllers;

import java.util.Date;

import models.Announcement;
import models.City;
import models.Member;
import models.Trip;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

// TODO: Empêcher de poster une annonce si l'utilisateur n'a pas enregistré de véhicule
@With(Secure.class)
public class Announcements extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			Member user = Member.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user);
			renderArgs.put("security", Security.connected());
		}
	}

	public static void index() {
	}

	public static void add(Announcement announcement) {
		Member member = Member.find("byEmail", Security.connected()).first();

		// if (announcement == null) { announcement = new Announcement(); }
		// TODO: Prendre en compte announcement dans le form, s'il existe

		renderArgs.put("vehicles", member.vehicles);
		renderArgs.put("cities", City.findAll());
		// renderArgs.put("annoucement", announcement);

		render();
	}

	public static void addPost(Announcement announcement) {

		if (announcement == null || announcement.trip == null) {
			Announcements.add(null);
		}

		Trip trip = announcement.trip;

		// Préciser les City dans Trip
		// (elles ne sont pas automatiquement remplies)
		trip.from = City.findById(trip.from.id);
		trip.to = City.findById(trip.to.id);

		if (trip.from.equals(trip.to)) {
			flash.error("announcements.sameCity");

			// BUG: Si on met Announcements.add(announcement); play bug !
			// TODO: Reporter le bug
			Announcements.add(new Announcement());
		}

		announcement.kilometers = trip.from.distanceBetween(trip.to);
		announcement.publicationDate = new Date();
		announcement.member = Member.find("byEmail", Security.connected())
				.first();

		// FIXME: Changer la valeur de calcul du prix
		announcement.totalCost = announcement.kilometers * 1.5;

		// Recherche du chemin via aStar
		trip.generatePath();

		if (announcement.trip.validateAndSave()
				&& announcement.validateAndSave()) {
			flash.success("announcements.successWhileSaving");
			Announcements.list();

		} else {
			for (play.data.validation.Error e : Validation.errors()) {
				System.out.println(e);
			}
			flash.error("announcements.errorWhileSaving");
			Announcements.add(new Announcement());
		}

	}

	public static void list() {
		renderArgs.put("annoucements", Announcement
				.find("byMember_id",
						((Member) Member.find("byEmail", Security.connected())
								.first()).id));
		render();
	}
}