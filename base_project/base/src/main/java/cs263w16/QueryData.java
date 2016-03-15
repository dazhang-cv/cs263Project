package cs263w16;

import com.google.appengine.api.datastore.GeoPt;

public class QueryData{
	
	public String title;
	public String city;
	public GeoPt location;
	public String question;
	
	public String toString(){
		return city + "," + title + "," + location + "," + question;
	}
	
}