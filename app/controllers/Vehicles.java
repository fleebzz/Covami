package controllers;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import models.Member;
import models.Vehicle;
import models.VehicleModel;
import play.data.validation.Required;
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
		render(vehicles);
	}
	
	public static void add() {
		Vehicle model = new Vehicle();
		List<VehicleModel> vehicleModels = VehicleModel.findAll();
		renderArgs.put("model", model);
		render(vehicleModels);
	}
	
	public static void addVehicle(@Required Vehicle v) throws Throwable {
		Vehicle existVehicle = Vehicle.find("byRegistration", v.registration).first();
		
		if(existVehicle.count() > 1){
			flash.error("vehicles.add.alreadyExist");
			index();
		}
		System.out.println(v.model);
//		validation.valid(v);
//		if (validation.hasErrors()) {
//			params.flash();
//			validation.keep();
//		} else {
//			v.save();
//			flash.success("member.profile.success");
//			myVehicles();
//		}
//		if (!v.validateAndCreate()) {
//			flash.keep("url");
//			flash.error("secure.error");
//			params.flash();
//			
//			index();
//		}
//
//		// Tout est ok
//		session.put("username", m.email);
//		flash.success("members.signup.success");
//		
//		Application.index();
	}
	
	public static void seeVehicle(long id) {
		
	}
}