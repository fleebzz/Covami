package models;

import java.util.*;
import javax.persistence.*;

import play.data.validation.Required;
import play.db.jpa.*;

@Entity
public class City extends Model {
	
	@Required
	public String name;
	
	@Column(name="longitude", nullable=true)
	public double longitude;
	
	@Column(nullable=true)
	public double latitude;
	
	@Column(nullable=true)
	public int insee;
	
	@Column(nullable=true)
	public int postalCode;
	
	@OneToOne
	public Country country;
	
	@ManyToMany
	public List<City> neighborhood;
	
	// Setters
	public void setName(String value){
		if(value == null || value.length() == 0){
			throw new IllegalArgumentException("Bad value");
		}
		
		this.name = value;
	}
}
