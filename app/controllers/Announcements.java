package controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.Announcement;
import models.City;
import models.Member;
import models.PendingAnnouncement;
import models.PendingReadOnly;
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
		Announcements.list();
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

	public static void addPost(Announcement announcement, String startDate)
			throws ParseException {

		if (announcement == null || announcement.trip == null) {
			Announcements.add(null);
		}

		Trip trip = announcement.trip;

		// Préciser les City dans Trip
		// (elles ne sont pas automatiquement remplies)
		trip.from = City.findById(trip.getFrom().id);
		trip.to = City.findById(trip.to.id);

		if (trip.from.equals(trip.to)) {
			flash.error("announcements.sameCity");

			// BUG: Si on met Announcements.add(announcement); play bug !
			// TODO: Reporter le bug
			Announcements.add(new Announcement());
		}

		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT, Locale.FRENCH);
		announcement.startDate = df.parse(startDate);

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
		Member member = Member.find("byEmail", Security.connected()).first();

		List<Announcement> announcements = Announcement.find("byMember_id",
				member.id).fetch();

		renderArgs.put("announcements", announcements);
		render();
	}

	public static void see(long id) {
		Member member = Member.find("byEmail", Security.connected()).first();
		Announcement announcement = Announcement.findById(id);
		if(announcement != null && member.friends.contains(announcement.member)){
			renderArgs.put("announcement", Announcement.findById(id));
			render();
		}
		if(announcement == null){
			flash.error("Cette annonce n'existe pas !");
		}
		else{
			flash.error("Vous ne pouvez pas visualiser cette annonce !");
		}
		Announcements.search();
	}

	public static void byMember(long id) {
		List<Announcement> announcements = Announcement.find("byMember_id", id).fetch();
		renderArgs.put("announcements", announcements);
		render();
	}

	public static void search() {
		Member member = Member.find("byEmail", Security.connected()).first();
		List<Announcement> announcements = new ArrayList<Announcement>();
		List<Announcement> allAnnouncements = Announcement.findAll();
		for (Announcement announcement : allAnnouncements) {
			if(member.friends.contains(announcement.member)){
				announcements.add(announcement);
			}
		}
		renderArgs.put("announcements", announcements);
		render();
	}

	
	public static void api() {
		List<Announcement> announcements = Announcement.find("startDate >= ?",
				new Date()).fetch();

		renderXml(announcements);
	}

	public static void apply(long announcementId) {
		Member member = Member.find("byEmail", Security.connected()).first();
		Announcement announcement = Announcement.findById(announcementId);
		
		if(announcement.member == member) {
			flash.error("announcements.apply.selfError");
			Announcements.see(announcementId);
		}
		
		PendingAnnouncement existPending = PendingAnnouncement.find("byAnnouncement_idAndApplicant_id", announcement.id, member.id).first();
		if(existPending == null) {
			PendingAnnouncement pending = new PendingAnnouncement(announcement, member);
			pending.save();
	
			announcement.member.pendingAnnouncements.add(pending);
			announcement.member.save();
		}
		
		flash.success("announcements.apply.success");
		
		Announcements.see(announcementId);
	}

	public static void desist(long announcementId) {
		Member member = Member.find("byEmail", Security.connected()).first();
		Announcement announcement = Announcement.findById(announcementId);
		
		announcement.freePlaces += 1;
		announcement.passengers.remove(member);
		announcement.save();
		
		PendingReadOnly pending = PendingReadOnly.find("byAnnouncement_idAndMember_id", announcement.id, member.id).first();
		if(pending != null) {
			member.pendings.remove(pending);
			member.save();
			pending.delete();
		}
		
		flash.success("announcements.desist.success");
		
		Announcements.see(announcementId);
	}
}