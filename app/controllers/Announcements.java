package controllers;

import java.awt.Color;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import models.Announcement;
import models.City;
import models.Member;
import models.Passenger;
import models.PendingAnnouncement;
import models.PendingReadOnly;
import models.Trip;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Announcements extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			Member user = Member.findByEmail(controllers.Secure.Security
					.connected());
			renderArgs.put("user", user);
			renderArgs.put("security", Security.connected());
		}
	}

	/**
	 * Lister les annonces
	 */
	public static void index() {
		Announcements.list();
	}

	/**
	 * Ajouter une annonce
	 * 
	 * @param announcement
	 * @param startDate
	 */
	public static void add(Announcement announcement, String startDate) {
		Member member = Member.findByEmail(controllers.Secure.Security
				.connected());

		List<City> cities = City.findAllOrderByName();

		renderArgs.put("vehicles", member.vehicles);
		renderArgs.put("cities", cities);
		renderArgs.put("startDate", startDate);

		render();
	}

	/**
	 * Ajouter une annonce (soumission des données)
	 * 
	 * @param announcement
	 * @param startDate
	 * @throws ParseException
	 */
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
			// new Announcement()
			Announcements.add(announcement, null);
		}

		if (announcement.freePlaces > (announcement.vehicle.model.nbPlaces - 1)) {
			flash.error("announcements.add.error.nbPlaces");
			Announcements.add(null, null);
		}

		if (startDate.isEmpty()) {
			flash.error("announcements.add.error.dateEmpty");
			Announcements.add(null, null);
		}

		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT, Locale.FRENCH);
		announcement.startDate = df.parse(startDate);

		// = trip.from.distanceBetween(trip.to);
		announcement.publicationDate = new Date();
		announcement.member = Member.findByEmail(controllers.Secure.Security
				.connected());

		// Recherche du chemin via aStar qui retourne la distance totale en KM
		announcement.kilometers = (int) (trip.generatePath());

		announcement.costByPassenger = (int) ((announcement.kilometers / 5.3) / announcement.freePlaces);

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

			announcement.color = String.valueOf(new Color(valR, valG, valB)
					.getRGB());
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
		Member member = Member.findByEmail(controllers.Secure.Security
				.connected());

		List<Announcement> previousAnnouncements = Announcement
				.findByMemberAndStartDateLessThanOrderByStartDate(member.id,
						new Date());
		List<Announcement> nextAnnouncements = Announcement
				.findByMemberAndStartDateGreaterThanOrderByStartDate(member.id,
						new Date());

		renderArgs.put("previousAnnouncements", previousAnnouncements);
		renderArgs.put("nextAnnouncements", nextAnnouncements);
		render();
	}

	public static void see(long id) {
		Member member = Member.findByEmail(controllers.Secure.Security
				.connected());
		Announcement announcement = Announcement.findById(id);
		if (announcement != null
				&& (member.friends.contains(announcement.member) || announcement.member == member)) {
			List<Passenger> passengersAnnouncement = Passenger
					.findByAnnouncement(announcement.id);
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
		Member member = Member.findByEmail(controllers.Secure.Security
				.connected());
		List<Announcement> announcements = Announcement.findByMember(id);

		List<Announcement> participateAnnouncements = new ArrayList<Announcement>();

		for (Announcement announcement : announcements) {
			if (Passenger.findByAnnouncementAndMember(announcement.id,
					member.id) != null) {
				participateAnnouncements.add(announcement);
			}
		}

		renderArgs.put("announcements", announcements);
		renderArgs.put("participateAnnouncements", participateAnnouncements);
		render();
	}

	public static void search(String searchFrom, String searchTo,
			String startDate) {
		Member member = Member.findByEmail(controllers.Secure.Security
				.connected());

		List<City> cities = City.findAllOrderByName();

		List<Announcement> announcements = new ArrayList<Announcement>();
		List<Announcement> allAnnouncements = new ArrayList<Announcement>();

		if (searchFrom == null) {
			allAnnouncements = Announcement.findAllOrderByStartDate();
		} else if (searchFrom.equalsIgnoreCase(searchTo)) {
			flash.error("announcements.search.error.sameCitys");
		} else if (startDate == null) {
			flash.error("announcements.search.error.noDate");
		} else {
			String startDateMinString = startDate + " 00:00:00";
			String startDateMaxString = startDate + " 23:59:59";

			SimpleDateFormat formatterDate = new SimpleDateFormat(
					"dd/MM/yyyy HH:mm:ss");
			Date startDateMin = formatterDate.parse(startDateMinString,
					new ParsePosition(0));
			Date startDateMax = formatterDate.parse(startDateMaxString,
					new ParsePosition(0));
			allAnnouncements = Announcement
					.findByStartDateGreaterThanAndStartDateLessThanOrderByStartDate(
							startDateMin, startDateMax);

		}

		for (Announcement announcement : allAnnouncements) {
			if (member.friends.contains(announcement.member)
					&& announcement.startDate.after(new Date())) {
				announcements.add(announcement);
			}
		}

		List<Announcement> participateAnnouncements = new ArrayList<Announcement>();

		for (Announcement announcement : announcements) {
			if (Passenger.findByAnnouncementAndMember(announcement.id,
					member.id) != null) {
				participateAnnouncements.add(announcement);
			}
		}

		if (announcements.size() == 0) {
			flash.error("announcements.search.notFound");
		}

		renderArgs.put("announcements", announcements);
		renderArgs.put("participateAnnouncements", participateAnnouncements);
		renderArgs.put("cities", cities);
		renderArgs.put("searchFrom", searchFrom);
		renderArgs.put("searchTo", searchTo);
		renderArgs.put("startDate", startDate);

		render();
	}

	public static void apply(long announcementId, long fromId, long toId,
			int wantedPlaces, int price) {
		Member member = Member.findByEmail(controllers.Secure.Security
				.connected());

		Announcement announcement = Announcement.findById(announcementId);

		if (announcement.member == member) {
			flash.error("announcements.apply.selfError");
			Announcements.see(announcementId);
		}
		if (announcement.startDate.before(new Date())) {
			flash.error("announcements.apply.error.past");
			Announcements.see(announcementId);
		}

		PendingAnnouncement existPending = PendingAnnouncement
				.findByAnnouncementAndApplicant(announcement.id, member.id);
		if (existPending == null) {
			City from = City.findById(fromId);
			City to = City.findById(toId);
			PendingAnnouncement pending = new PendingAnnouncement(announcement,
					member, from, to, wantedPlaces);
			pending.save();

			announcement.member.pendingAnnouncements.add(pending);
			announcement.member.save();
		}

		flash.success("announcements.apply.success");

		Announcements.see(announcementId);
	}

	public static void applyCustom(long announcementId, long fromId, long toId,
			int wantedPlaces) {
		Announcement announcement = Announcement.findById(announcementId);
		City from = City.findById(fromId);
		City to = null;

		if (toId != 0 && wantedPlaces != 0) {
			to = City.findById(toId);
			renderArgs.put("to", to);
			renderArgs.put("wantedPlaces", wantedPlaces);
		} else {
			to = announcement.trip.to;
			renderArgs.put("to", null);
			renderArgs.put("wantedPlaces", 0);
		}

		Trip trip = new Trip(from, to);
		double distance = trip.generatePath();

		int price = (int) (distance / 5.3 / announcement.vehicle.model.nbPlaces * wantedPlaces);

		List<City> cities = trip.cities;
		cities.remove(from);

		renderArgs.put("announcement", announcement);
		renderArgs.put("from", from);
		renderArgs.put("cities", cities);
		renderArgs.put("price", price);

		render();
	}

	public static void desist(long announcementId) {
		Member member = Member.findByEmail(controllers.Secure.Security
				.connected());
		Announcement announcement = Announcement.findById(announcementId);
		Passenger passenger = Passenger.findByAnnouncementAndMember(
				announcement.id, member.id);

		announcement.freePlaces += passenger.nbPlaces;
		announcement.passengers.remove(passenger);
		announcement.save();
		passenger.delete();

		PendingReadOnly pending = PendingReadOnly.findByAnnouncementAndMember(
				announcement.id, member.id);
		if (pending != null) {
			member.pendings.remove(pending);
			member.save();
			pending.delete();
		}

		PendingReadOnly pendingRO = new PendingReadOnly(announcement.member);
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
		Member member = Member.findByEmail(controllers.Secure.Security
				.connected());
		Announcement announcement = Announcement.findById(announcementId);

		SimpleDateFormat formatterDate = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");

		List<Passenger> passengersAnnouncement = Passenger
				.findAllByAnnouncement(announcement);
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
			pendingDelete.description = formatterDate.format(
					announcement.startDate).toString()
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