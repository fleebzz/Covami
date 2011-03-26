package controllers;
 
import models.*;
 
public class Security extends Secure.Security {
 
    static boolean authentify(String login, String password) {
    	return User.connect(login, password) != null;
    }
    
    //Fonction permettant de revenir à la page d'accueil après s'être deconnecté
    static void onDisconnected() {
        Application.index();
    }
}