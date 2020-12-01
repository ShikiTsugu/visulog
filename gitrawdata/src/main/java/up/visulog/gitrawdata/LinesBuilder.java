package up.visulog.gitrawdata;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LinesBuilder{
    private final String id;
    private String author;
    private LocalDateTime date;
    private int nbLines; 
    
    

    public LinesBuilder(String id) {
        this.id = id;
    } 
    
    public LinesBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }
    
    public LinesBuilder setDate(String date) {
    	//Modifie date pour qu'elle soit dans le bon format
    	date = date.trim().replace("\n","");
    	date = date.substring(0,date.length()-6); 
	if(date.length()==24){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE LLL dd HH:mm:ss yyyy", java.util.Locale.ENGLISH);
		LocalTime time = LocalTime.parse(date.substring(11,19));
    		this.date = LocalDate.parse(date, formatter).atTime(time);
        	return this;
	}
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE LLL d HH:mm:ss yyyy", java.util.Locale.ENGLISH);
	LocalTime time = LocalTime.parse(date.substring(10,18));
    	this.date = LocalDate.parse(date, formatter).atTime(time);
        return this;
    }

    public LinesBuilder setNbLines(int nbLines){
    	this.nbLines=nbLines;
    	return this;
    }
       
    public Lines createLines() {
        return new Lines(id, author, date, nbLines);
    }
}
