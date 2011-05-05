package controllers;

import java.util.List;

import models.Member;
import models.Vehicle;
import models.VehicleModel;
import play.data.validation.Required;
import play.data.validation.Valid;
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
	}
	
	public static void add() {
		Member member = Member.find("byEmail", Security.connected()).first();
		renderArgs.put("vehicles", member.vehicles);
		render();
	}
	
	public static void addPost(@Valid Vehicle vehicle) {
		System.out.println(vehicle.model.make);
	}
}