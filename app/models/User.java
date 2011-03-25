package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class User extends Model {

    public String login;
    public String email;
    public String password;
    
    public User(String login, String email, String password, String fullname) {
    	this.login = login;
        this.email = email;
        this.password = password;
    }
 
}