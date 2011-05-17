package controllers;

import java.util.Date;
import java.util.List;

import models.Announcement;
import models.Member;
import models.Vehicle;
import models.VehicleModel;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Vehicles extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			Member user = Member.findByEmail(controllers.Secure.Security.connected());
			renderArgs.put("user", user);
			renderArgs.put("security", Security.connected());
		}
	}

	public static void index() {
		Application.index();
	}

	/**
	 * Lister les voitures du membre
	 */
	public static void list() {
		Member member = Member.findByEmail(controllers.Secure.Security.connected());
		List<Vehicle> vehicles = member.vehicles;

		render(vehicles);
	}

	/**
	 * Ajouter une voiture
	 */
	public static void add() {
		Vehicle model = new Vehicle();
		List<VehicleModel> vehicleModels = VehicleModel.findAll();

		renderArgs.put("model", model);

		render(vehicleModels);
	}

	/**
	 * [POST] Ajouter une voiture
	 * 
	 * @param vehicle
	 * @param vehicleModel
	 * @throws Throwable
	 */
	public static void addPost(@Required Vehicle vehicle,
			@Required VehicleModel vehicleModel) throws Throwable {
		
		Vehicle existVehicle = Vehicle.findByRegistration(vehicle.registration);

		Member member = Member.findByEmail(controllers.Secure.Security.connected());

		if (existVehicle != null) {
			flash.error("vehicles.add.alreadyExist");
			Vehicles.add();
		}

		vehicle.model = vehicleModel;
		vehicle.save();
		member.vehicles.add(vehicle);
		member.save();
		flash.success("vehicles.add.success");

		Vehicles.list();
	}

	/**
	 * Editer une voiture
	 * 
	 * @param id
	 */
	public static void edit(long id) {
		Vehicle vehicle = Vehicle.findById(id);
		List<VehicleModel> vehicleModels = VehicleModel.findAll();
		
		System.out.println(vehicle.registration);

		renderArgs.put("model", vehicle);
		renderArgs.put("vehicle", vehicle);
		renderArgs.put("vehicleModels", vehicleModels);

		render();
	}

	/**
	 * [POST] Editer une voiture
	 * 
	 * @param vehicle
	 * @param vehicleModel
	 * @throws Throwable
	 */
	public static void editPost(@Valid Vehicle vehicle,
			@Required VehicleModel vehicleModel) throws Throwable {
		
		Vehicle existVehicle = Vehicle.findByRegistrationAndIdNotEqual(vehicle.registration, vehicle.id);

		if (existVehicle != null) {
			flash.error("vehicles.add.alreadyExist");
			Vehicles.edit(vehicle.id);
		}

		vehicle.model = vehicleModel;
		vehicle.save();

		flash.success("vehicles.edit.success");

		Vehicles.list();
	}

	/**
	 * Supprimer une voiture
	 * 
	 * @param vehicleId
	 */
	public static void delete(long vehicleId) {
		Member member = Member.findByEmail(controllers.Secure.Security.connected());
		Vehicle vehicle = Vehicle.findById(vehicleId);
		
		Announcement announcement = Announcement.findByStartDateGreaterThanAndVehicle(new Date(), vehicle.id);

		if (announcement == null) {
			member.vehicles.remove(vehicle);
			member.save();
			vehicle.delete();
			flash.success("vehicles.delete.success");
		} else {
			flash.error("vehicles.delete.error.hasAnnouncement");
		}

		Vehicles.list();
	}
}