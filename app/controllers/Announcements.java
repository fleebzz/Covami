package controllers;

import java.awt.Color;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import models.Announcement;
import models.City;
import models.JSONAnnouncement;
import models.Member;
import models.Passenger;
import models.PendingAnnouncement;
import models.PendingReadOnly;
import models.Trip;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

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

	public static void add(Announcement announcement, String startDate) {
		Member member = Member.find("byEmail", Security.connected()).first();

		List<City> cities = City.find("order by name").fetch();
		
		renderArgs.put("vehicles", member.vehicles);
		renderArgs.put("cities", cities);
		renderArgs.put("startDate", startDate);

		render();
	}

	public static void addPost(Announcement announcement, String startDate)
			throws ParseException {

		if (announcement == null || announcement.trip == null) {
			Announcements.add(null, null);
		}

		if (Validation.hasErrors()) {
			Announcements.add(announcement, startDate);

		}

		Trip trip = announcement.trip;

		// Préciser les City dans Trip
		// (elles ne sont pas automatiquement remplies)
		trip.from = City.findById(trip.getFrom().id);
		trip.to = City.findById(trip.to.id);

		if (trip.from.equals(trip.to)) {
			flash.error("announcements.sameCity");

			// BUG: Si on met Announcements.add(announcement); play bug !
			// TODO: Raporter le bug
			Announcements.add(new Announcement(), null);
		}
		
		if(announcement.freePlaces > (announcement.vehicle.model.nbPlaces-1)){
			flash.error("announcements.add.error.nbPlaces");
			Announcements.add(null, null);
		}
		
		if(startDate.isEmpty()){
			flash.error("announcements.add.error.dateEmpty");
			Announcements.add(null, null);
		}

		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT, Locale.FRENCH);
		announcement.startDate = df.parse(startDate);

		// = trip.from.distanceBetween(trip.to);
		announcement.publicationDate = new Date();
		announcement.member = Member.find("byEmail", Security.connected())
				.first();

		// Recherche du chemin via aStar qui retourne la distance totale en KM
		announcement.kilometers = (int)(trip.generatePath());

		announcement.costByPassenger = (int)((announcement.kilometers / 5.3) / announcement.freePlaces);
		
		System.out.println(announcement.costByPassenger);

		if (announcement.trip.validateAndSave()
				&& announcement.validateAndSave()) {
			flash.success("announcements.successWhileSaving");
			
			Random randR = new Random();
			Random randG = new Random();
			Random randB = new Random();
			int minRand = 0;
			int maxRand = 255;

			int valR = minRand + randR.nextInt(maxRand - minRand);
			int valG = minRand + randG.nextInt(maxRand - minRand);
			int valB = minRand + randB.nextInt(maxRand - minRand);
			
			announcement.color = String.valueOf(new Color(valR, valG, valB).getRGB());
			announcement.save();
			
			Announcements.list();

		} else {
			for (play.data.validation.Error e : Validation.errors()) {
				System.out.println(e);
			}
			flash.error("announcements.errorWhileSaving");
			Announcements.add(new Announcement(), null);
		}

	}

	public static void list() {
		Member member = Member.find("byEmail", Security.connected()).first();

		List<Announcement> previousAnnouncements = Announcement.find("member_id = ? and startDate < ? order by startDate",
				member.id, new Date()).fetch();
		List<Announcement> nextAnnouncements = Announcement.find("member_id = ? and startDate > ? order by startDate",
				member.id, new Date()).fetch();

		renderArgs.put("previousAnnouncements", previousAnnouncements);
		renderArgs.put("nextAnnouncements", nextAnnouncements);
		render();
	}

	public static void see(long id) {
		Member member = Member.find("byEmail", Security.connected()).first();
		Announcement announcement = Announcement.findById(id);
		if (announcement != null
				&& (member.friends.contains(announcement.member) || announcement.member == member)) {
			List<Passenger> passengersAnnouncement = Passenger.find("byAnnouncement_id", announcement.id).fetch();
			List<Member> passengers = new ArrayList<Member>();
			for (Passenger passenger : passengersAnnouncement) {
				passengers.add(passenger.member);
			}
			renderArgs.put("announcement", announcement);
			renderArgs.put("passengers", passengers);
			render();
		}
		if (announcement == null) {
			flash.error("announcements.error.notFound");
		} else {
			flash.error("announcements.error.forbidden");
		}
		Announcements.search(null, null, null);
	}

	public static void byMember(long id) {
		Member member = Member.find("byEmail", Security.connected()).first();
		List<Announcement> announcements = Announcement.find("byMember_id", id)
				.fetch();
		
		List<Announcement> participateAnnouncements = new ArrayList<Announcement>();
		
		for (Announcement announcement : announcements) {
			if(Passenger.find("byAnnouncement_idAndMember_id", announcement.id, member.id) != null){
				participateAnnouncements.add(announcement);
			}
		}

		renderArgs.put("announcements", announcements);
		renderArgs.put("participateAnnouncements", participateAnnouncements);
		render();
	}

	public static void search(String searchFrom, String searchTo, String startDate) {
		Member member = Member.find("byEmail", Security.connected()).first();
		
		List<City> cities = City.find("order by name").fetch();
		
		List<Announcement> announcements = new ArrayList<Announcement>();
		List<Announcement> allAnnouncements = new ArrayList<Announcement>();
		
		if(searchFrom == null){
			allAnnouncements = Announcement.find("order by startDate").fetch();
		}
		else if(searchFrom.equalsIgnoreCase(searchTo)){
			flash.error("announcements.search.error.sameCitys");
		}
		else if(startDate == null){
			flash.error("announcements.search.error.noDate");
		}
		else{
			String startDateMinString = startDate + " 00:00:00";
			String startDateMaxString = startDate + " 23:59:59";
			SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date startDateMin = formatterDate.parse(startDateMinString, new ParsePosition(0));
			Date startDateMax = formatterDate.parse(startDateMaxString, new ParsePosition(0));
			allAnnouncements = Announcement.find("startDate > ? and startDate < ? order by startDate", startDateMin, startDateMax).fetch();
		}
		for (Announcement announcement : allAnnouncements) {
			if (member.friends.contains(announcement.member) && announcement.startDate.after(new Date())) {
				announcements.add(announcement);
			}
		}

		List<Announcement> participateAnnouncements = new ArrayList<Announcement>();
		
		for (Announcement announcement : announcements) {
			if(Passenger.find("byAnnouncement_idAndMember_id", announcement.id, member.id) != null){
				participateAnnouncements.add(announcement);
			}
		}

		renderArgs.put("announcements", announcements);
		renderArgs.put("participateAnnouncements", participateAnnouncements);
		renderArgs.put("cities", cities);
		renderArgs.put("searchFrom", searchFrom);
		renderArgs.put("searchTo", searchTo);
		renderArgs.put("startDate", startDate);
		render();
	}

	public static void apply(long announcementId) {
		Member member = Member.find("byEmail", Security.connected()).first();
		Announcement announcement = Announcement.findById(announcementId);

		if (announcement.member == member) {
			flash.error("announcements.apply.selfError");
			Announcements.see(announcementId);
		}
		if(announcement.startDate.before(new Date())){
			flash.error("announcements.apply.error.past");
			Announcements.see(announcementId);
		}

		PendingAnnouncement existPending = PendingAnnouncement.find(
				"byAnnouncement_idAndApplicant_id", announcement.id, member.id)
				.first();
		if (existPending == null) {
			PendingAnnouncement pending = new PendingAnnouncement(announcement,
					member);
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

		PendingReadOnly pending = PendingReadOnly.find(
				"byAnnouncement_idAndMember_id", announcement.id, member.id)
				.first();
		if (pending != null) {
			member.pendings.remove(pending);
			member.save();
			pending.delete();
		}
		
		PendingReadOnly pendingRO = new PendingReadOnly(member);
		pendingRO.type = "desistParticipation";
		pendingRO.announcement = announcement;
		pendingRO.applicant = member;
		pendingRO.save();
		
		announcement.member.pendings.add(pendingRO);
		announcement.member.save();

		flash.success("announcements.desist.success");

		Announcements.see(announcementId);
	}

	public static void delete(long announcementId) {
		Member member = Member.find("byEmail", Security.connected()).first();
		Announcement announcement = Announcement.findById(announcementId);
		
		SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		List<Passenger> passengersAnnouncement = Passenger.find("byAnnouncement", announcement).fetch();
		List<Member> passengers = new ArrayList<Member>();
		
		for (Passenger passengerAnnouncement : passengersAnnouncement) {
			passengers.add(passengerAnnouncement.member);
			announcement.passengers.remove(passengerAnnouncement);
			announcement.save();
			passengerAnnouncement.delete();
		}
		
		
		for (Member passenger : passengers) {
			PendingReadOnly pendingDelete = new PendingReadOnly(passenger);
			pendingDelete.type = "deleteAnnouncement";
			pendingDelete.applicant = member;
			pendingDelete.description = formatterDate.format(announcement.startDate).toString()
				+ " | "
				+ announcement.trip.from.name
				+ " => "
				+ announcement.trip.to.name;
			pendingDelete.save();
			passenger.pendings.add(pendingDelete);
			passenger.save();
		}
		announcement.delete();

		flash.success("announcements.delete.success");

		Announcements.list();
	}

	public static void seeMap(long id) {
		
		renderArgs.put("announcementId", id);
		
		render();
	}
}