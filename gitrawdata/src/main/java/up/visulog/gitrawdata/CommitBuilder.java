package up.visulog.gitrawdata;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommitBuilder {
    private final String id;
    private String author;
    private LocalDateTime date;
    private String description;
    private String mergedFrom;

    public CommitBuilder(String id) {
        this.id = id;
    }

    public CommitBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public CommitBuilder setDate(String date) {
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

    public CommitBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public CommitBuilder setMergedFrom(String mergedFrom) {
        this.mergedFrom = mergedFrom;
        return this;
    }

    public Commit createCommit() {
        return new Commit(id, author, date, description, mergedFrom);
    }
}
