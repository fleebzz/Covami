package controllers;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import models.Member;
import models.Vehicle;
import models.VehicleModel;
import play.data.validation.Required;
import play.data.validation.Valid;
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
	
	public static void addVehicle(@Required Vehicle vehicle, @Required VehicleModel vehicleModel) throws Throwable {
		Vehicle existVehicle = Vehicle.find("byRegistration", vehicle.registration).first();
		models.Member member = models.Member.find("byEmail",Security.connected()).first();
		
		if(existVehicle.count() > 1){
			flash.error("vehicles.add.alreadyExist");
			add();
		}
		vehicle.model = vehicleModel;
		vehicle.save();
		member.vehicles.add(vehicle);
		member.save();
		flash.success("vehicles.add.success");
		myVehicles();
	}
	
	public static void myVehicle(long id) {
		Vehicle vehicle = Vehicle.findById(id);
		List<VehicleModel> vehicleModels = VehicleModel.findAll();
		renderArgs.put("model", vehicle);
		render(vehicle, vehicleModels);
	}

	public static void editVehicle(@Valid Vehicle vehicle, @Required VehicleModel vehicleModel) throws Throwable {
		Vehicle existVehicle = Vehicle.find("byRegistrationAndIdNotEqual", vehicle.registration, vehicle.id).first();
		
		if(existVehicle != null){
			flash.error("vehicles.add.alreadyExist");
			myVehicle(vehicle.id);
		}
		vehicle.model = vehicleModel;
		vehicle.save();
		flash.success("vehicles.edit.success");
		myVehicles();
	}
	
	public static void deleteVehicle(long vehicleId) {
		Member member = Member.find("byEmail", Security.connected()).first();
		Vehicle vehicle = Vehicle.findById(vehicleId);
		
		member.vehicles.remove(vehicle);
		member.save();

		vehicle.delete();
		
		myVehicles();
	}
}