package Shared;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {

	public String message = "";
	public String timestamp = "";
	public String oid = "";
	
	public Message(){
		Date date  = new Date();
		DateFormat form = new SimpleDateFormat("SSSssmmhhddMMyyyy");
		timestamp = form.format(date);
	}
	
}
