package models;

import java.util.*;
import javax.persistence.*;

import play.data.validation.Required;
import play.db.jpa.*;

@Entity
public class Announcement extends Model {

	@Required
	@OneToOne
	public Member member;

	@Required
	@OneToOne
	public Vehicle vehicle;

	@Required
	@OneToOne
	public Trip trip;
	
	@Required
	public Date publicationDate;
	
	@Required
	public Date startDate;
	
	@Required
	public int kilometers;
	
	@Required
	public int freePlaces;
	
	@Required
	public float totalCost;
	
}