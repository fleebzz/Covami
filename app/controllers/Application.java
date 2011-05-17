package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Announcement;
import models.City;
import models.Member;
import models.PendingAnnouncement;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends Controller {

	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			Member user = Member.findByEmail(controllers.Secure.Security.connected());
			renderArgs.put("user", user);
			renderArgs.put("security", Security.connected());
		}
	}

	public static void index() {
		render();
	}
	
	public static void citiesForTests(){
		
		List<City> cities = City.findAll();
		
		renderXml(cities);
		
//		List<String> neighborhood = new ArrayList<String>();
//		System.out.println(cities.size());
//		for (City city : cities) {
//			System.out.println(city.name);
////			for (City city2 : city.neighborhood) {
////				if(city2.insee > city.insee){
////					neighborhood.add("autoroutes.add(new Troncon(getVille(" + city.insee + "), getVille(" + city2.insee + ")));");
////				}
////			}
//		}
		
//		for (String string : neighborhood) {
//			System.out.println(string);
//		}
	}
}