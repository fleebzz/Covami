package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Announcement;
import models.JSONAnnouncement;
import models.Member;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

public class Api extends Controller {
	public static void index() {
		List<Announcement> announcements = Announcement.find("startDate >= ?",
				new Date()).fetch();
		
		List<JSONAnnouncement> jsonAnnouncements = new ArrayList<JSONAnnouncement>();
		for (Announcement announcement : announcements) {
			jsonAnnouncements.add(new JSONAnnouncement(announcement.trip.from.insee, announcement.trip.to.insee, announcement.color));
		}
		
		renderJSON(jsonAnnouncements);
	}
	
	public static void byAnnouncement(long id){
		Announcement announcement = Announcement.findById(id);
		if(announcement != null){
			renderJSON(new JSONAnnouncement(announcement.trip.from.insee, announcement.trip.to.insee, announcement.color));
		}
	}
}
