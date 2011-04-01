package controllers;
 
import models.*;
 
public class Security extends Secure.Security {
 
    static boolean authentify(String email, String password) {
    	return Member.connect(email, password) != null;
    }
    
    //Fonction permettant de revenir à la page d'accueil après s'être deconnecté
    static void onDisconnected() {
        Application.index();
    }
}