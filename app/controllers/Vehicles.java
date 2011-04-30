package controllers;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import models.Member;
import models.Vehicle;
import models.VehicleModel;
import play.mvc.Before;
import play.mvc.Controller;

public class Vehicles extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			Member user = Member.find("byEmail", Security.connected()).first();
			renderArgs.put("user", user);
			renderArgs.put("security", Security.connected());
		}
	}

	public static void index() {
		Application.index();
	}
	
	public static void myVehicles(){
		Member member = Member.find("byEmail", Security.connected()).first();
		List<Vehicle> vehicles = member.vehicles;		
	}
}