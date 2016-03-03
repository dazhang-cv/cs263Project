package cs263w16.datastore;

import javax.xml.bind.annotation.XmlRootElement;

import java.util.*;

@XmlRootElement
// JAX-RS supports an automatic mapping from JAXB annotated class to XML and
// JSON
public class Message {
	private String usr_name;
	private String content;
	private int type;
	private Date date;
	private double longitude;
	private double latitude;
	private String options;

	public Message() {

	}

	public Message(String usr_name, String content, int type, Date date,
			double longitude, double latitude, String options) {
		this.usr_name = usr_name;
		this.content = content;
		this.type = type;
		this.date = date;
		this.longitude = longitude;
		this.latitude = latitude;
		this.options = options;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
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
