package cs263w16.datastore;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.*;

@XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and JSON
public class Message {
  private String usr_name;
  private String content;
  private Date date;
  private double longitude;
  private double latitude;

public Message(){
	
}

public Message(String usr_name, String content, Date date, double longitude,
		double latitude) {
	this.usr_name = usr_name;
	this.content = content;
	this.date = date;
	this.longitude = longitude;
	this.latitude = latitude;
}

public String getUsr_name() {
	return usr_name;
}

public void setUsr_name(String usr_name) {
	this.usr_name = usr_name;
}

public String getContent() {
	return content;
}

public void setContent(String content) {
	this.content = content;
}

public Date getDate() {
	return date;
}

public void setDate(Date date) {
	this.date = date;
}

public double getLongitude() {
	return longitude;
}

public void setLongitude(double longitude) {
	this.longitude = longitude;
}

public double getLatitude() {
	return latitude;
}

public void setLatitude(double latitude) {
	this.latitude = latitude;
}
} 
