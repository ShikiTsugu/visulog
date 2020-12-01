package up.visulog.gitrawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Lines {
    // FIXME: (some of) these fields could have more specialized types than String
    public final String id;
    public final String author;
    public final LocalDateTime date;
    public final int nbLines;
    
    public Lines(String id,String author,LocalDateTime date,int nbLines) {
    	this.id=id;
    	this.author =author;
    	this.date=date;
    	this.nbLines=nbLines;
    }

    public static List<Lines> parseLogFromCommand(Path gitPath) {
        ProcessBuilder builder = 
        new ProcessBuilder("git", "log").directory(gitPath.toFile());
        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException("Error running \"git log\".", e);
        }
        InputStream is = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return parseLog(reader);
    }

    public static List<Lines> parseLog(BufferedReader reader) {
        var result = new ArrayList<Lines>();
        Optional<Lines> lines = parseLines(reader);
        while (lines.isPresent()) {
            result.add(lines.get());
            lines = parseLines(reader);
        }
        return result;
    }
    
    public static Optional<Lines> parseLines(BufferedReader input) {
        try {
            var line = input.readLine();
            if (line == null) return Optional.empty(); // if no line can be read, we are done reading the buffer
            var idChunks = line.split(" ");
            if (!idChunks[0].equals("lines")) parseError();
            var builder = new LinesBuilder(idChunks[1]);
            line = input.readLine();
            while (!line.isEmpty()) {
                var colonPos = line.indexOf(":");
                var fieldName = line.substring(0, colonPos);
                var fieldContent = line.substring(colonPos + 1).trim();
                switch (fieldName) {
                    case "Author":
                        builder.setAuthor(fieldContent);
                           break;
                    case "Date":
                        builder.setDate(fieldContent);
                        break;
                    default: 
                    	System.out.println("Le champ " + fieldName + " a été ignoré");
                }
                line=input.readLine();
                if (line == null) parseError(); // end of stream is not supposed to happen now (line data incomplete)
            }
            return Optional.of(builder.createLines());
	} catch (IOException e) {
            parseError();
        }
        return Optional.empty(); // this is supposed to be unreachable, as parseError should never return
    }
    
    private static void parseError() {
        throw new RuntimeException("Wrong format.");
    }
           
    public static HashMap numberOfLinesPerWeek(Path gitPath) {
    	List<Lines> lines = parseLogFromCommand(gitPath);
    	HashMap<LocalDate, Integer> linesPerWeek = new HashMap<LocalDate, Integer>();
    	for(Lines c : lines) {
    		if(linesPerWeek.get(firstDayOfTheWeek(c.date.toLocalDate()))==null) {
    			int valeur = 0;
        		linesPerWeek.put(firstDayOfTheWeek(c.date.toLocalDate()),1 + valeur);
    		}else {
	    		int valeur = (int) linesPerWeek.get(firstDayOfTheWeek(c.date.toLocalDate()));
	    		linesPerWeek.put(firstDayOfTheWeek(c.date.toLocalDate()),1 + valeur);
    		}
    	}
    	return linesPerWeek;
    }
    
    public static LocalDate firstDayOfTheWeek(LocalDate date) {
    	TemporalField fieldFR = WeekFields.of(Locale.FRANCE).dayOfWeek();
    	return date.with(fieldFR, 1);
    }
    
    public static String dateParser(LocalDateTime date){
        DateTimeFormatter Pattern = DateTimeFormatter.ofPattern("EEEE/MM/yyyy");
        String DateParsed = date.format(Pattern);
        int index = DateParsed.indexOf("/");
        return DateParsed.substring(0, index);

    }

    @Override
    public String toString() {
        return "Lines{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", date='"+ date + '\''+
                ", nbLines='"+nbLines + '\''+
  		'}';
  	}
}
