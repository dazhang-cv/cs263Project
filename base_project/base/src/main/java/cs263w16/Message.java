package cs263w16;

public class Message{
	String type;
	String username;
	String content;
	String time;
	
	public Message(String type, String username, String content, String time){
		this.type = type;
		this.username = username;
		this.content = content;
		this.time = time;
	}
}