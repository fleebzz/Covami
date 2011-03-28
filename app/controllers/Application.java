package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;


public class Application extends Controller {
	
    @Before
    static void setConnectedUser(){
         if(Security.isConnected()){
             User user = User.find("byLogin",Security.connected()).first();
             renderArgs.put("user", user);
             renderArgs.put("security",Security.connected());
         }else {
             renderArgs.put("user", new User("","","", "John", "Doe"));
         }
     }
    
    public static void index() {
        render();
    }
}