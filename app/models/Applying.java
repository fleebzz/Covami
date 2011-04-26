package models;

import java.util.*;
import javax.persistence.*;


import play.data.validation.Required;
import play.db.jpa.*;

@Entity
public class Applying extends Model {
	
	@Required
	public boolean accepted;
	
	@Required
	public float price;

	@Required
	@OneToOne
	public Member member;

	@Required
	@OneToOne
	public Announcement announcement;
	
}