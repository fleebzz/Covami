package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class User extends Model {

    public String login;
    public String email;
    public String password;
    public String firstname;
    public String lastname;
    
    public User(String login, String email, String password, String firstname, String lastname) {
    	this.login = login;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }
    
    public static User connect(String email, String password) {
        return find("byLoginAndPassword", email, password).first();
    }
    
    static boolean authentify(String username, String password) {
        return User.connect(username, password) != null;
    }
    
    public String toString() {
        return firstname + " " + lastname;
    }
}