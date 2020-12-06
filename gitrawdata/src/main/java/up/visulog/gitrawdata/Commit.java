package up.visulog.gitrawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
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


public class Commit {
    // FIXME: (some of) these fields could have more specialized types than String
    public final String id;
    public final LocalDateTime date;
    public final String author;
    public final String description;
    public final String mergedFrom;

    public Commit(String id, String author, LocalDateTime date, String description, String mergedFrom) {
        this.id = id;
        this.author = author;
        this.date = date;
        this.description = description;
        this.mergedFrom = mergedFrom;
    }

    // TODO: factor this out (similar code will have to be used for all git commands)
    //renvoie la liste des commits donnés par la commande "git log" exécutéé depuis gitPath
    public static List<Commit> parseLogFromCommand(Path gitPath) {
    	//builder: commande "git log" qui va être exécutée depuis le dossier gitPath
        ProcessBuilder builder =
                new ProcessBuilder("git", "log").directory(gitPath.toFile());
        Process process;
        try {
        	//exécution de builder
            process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException("Error running \"git log\".", e);
        }
        InputStream is = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return parseLog(reader);
    }
    
    public static List<Commit> parseLog(BufferedReader reader) {
        var result = new ArrayList<Commit>();
        Optional<Commit> commit = parseCommit(reader);
        while (commit.isPresent()) {
            result.add(commit.get());
            commit = parseCommit(reader);
        }
        return result;
    }

    /**
     * Parses a log item and outputs a commit object. Exceptions will be thrown in case the input does not have the proper format.
     * Returns an empty optional if there is nothing to parse anymore.
     */
    //crée des objet de la class commit à partir de la sortie de la commande git log
    public static Optional<Commit> parseCommit(BufferedReader input) {
        try {

            var line = input.readLine();
            if (line == null) return Optional.empty(); // if no line can be read, we are done reading the buffer
            var idChunks = line.split(" ");
            if (!idChunks[0].equals("commit")) parseError();
            var builder = new CommitBuilder(idChunks[1]);

            line = input.readLine();
            while (!line.isEmpty()) {
                var colonPos = line.indexOf(":");
                var fieldName = line.substring(0, colonPos);
                var fieldContent = line.substring(colonPos + 1).trim();
                switch (fieldName) {
                    case "Author":
                        builder.setAuthor(fieldContent);
                        break;
                    case "Merge":
                        builder.setMergedFrom(fieldContent);
                        break;
                    case "Date":
                        builder.setDate(fieldContent);
                        break;
                    default: 
                    	System.out.println("Le champ " + fieldName + " a été ignoré");
                }
                line = input.readLine(); //prepare next iteration
                if (line == null) parseError(); // end of stream is not supposed to happen now (commit data incomplete)
            }

            // now read the commit message per se
            var description = input
                    .lines() // get a stream of lines to work with
                    .takeWhile(currentLine -> !currentLine.isEmpty()) // take all lines until the first empty one (commits are separated by empty lines). Remark: commit messages are indented with spaces, so any blank line in the message contains at least a couple of spaces.
                    .map(String::trim) // remove indentation
                    .reduce("", (accumulator, currentLine) -> accumulator + currentLine); // concatenate everything
            builder.setDescription(description);
            return Optional.of(builder.createCommit());
        } catch (IOException e) {
            parseError();
        }
        return Optional.empty(); // this is supposed to be unreachable, as parseError should never return
    }

    // Helper function for generating parsing exceptions. This function *always* quits on an exception. It *never* returns.
    private static void parseError() {
        throw new RuntimeException("Wrong commit format.");
    }
    
    // Renvoie une HashMap dont les clés sont les jours et les valeurs, le nombre de commits effectués
    public static HashMap numberOfCommitsPerDay(Path gitPath) {
    	List<Commit> commits = parseLogFromCommand(gitPath);
    	HashMap<LocalDate, Integer> commitsPerDay = new HashMap<LocalDate, Integer>();
    	for(Commit c : commits) {
    		if(commitsPerDay.get(c.date.toLocalDate())==null) {
    			int valeur = 0;
        		commitsPerDay.put(c.date.toLocalDate(),1 + valeur);
    		}else {
	    		int valeur = (int) commitsPerDay.get(c.date.toLocalDate());
	    		commitsPerDay.put(c.date.toLocalDate(),1 + valeur);
    		}
    	}
    	return commitsPerDay;
    }
    
    // Renvoie une HashMap dont les clés sont les premiers jours de chaque semaine et les valeurs, le nombre de commits effectués
    public static HashMap numberOfCommitsPerWeek(Path gitPath) {
    	List<Commit> commits = parseLogFromCommand(gitPath);
    	HashMap<LocalDate, Integer> commitsPerWeek = new HashMap<LocalDate, Integer>();
    	for(Commit c : commits) {
    		if(commitsPerWeek.get(firstDayOfTheWeek(c.date.toLocalDate()))==null) {
    			int valeur = 0;
        		commitsPerWeek.put(firstDayOfTheWeek(c.date.toLocalDate()),1 + valeur);
    		}else {
	    		int valeur = (int) commitsPerWeek.get(firstDayOfTheWeek(c.date.toLocalDate()));
	    		commitsPerWeek.put(firstDayOfTheWeek(c.date.toLocalDate()),1 + valeur);
    		}
    	}
    	return commitsPerWeek;
    }
    
    //Renvoie le nombre de commits des n derniers jours
    public static int numberOfCommits (int n, Path gitPath) {
    	LocalDate day = LocalDate.now().minusDays(n);
    	int number = 0;
    	HashMap<LocalDate, Integer> commits = numberOfCommitsPerDay(gitPath);
    	for(HashMap.Entry<LocalDate, Integer> m : commits.entrySet()) {
    		if(m.getKey().isAfter(day)) {
    			number += m.getValue();
    		}
    	}
    	return number;
    }
    
    //Renvoie le nombre de commit fait en moyenne les n derniers jours
    public static double averageNumberOfCommits(int n, Path gitPath) {
    	return (double) numberOfCommits(n,gitPath)/(double) n;
    }
    
    //Renvoie l'écart type du nombre de commit sur les n derniers jours
    public static double ecartType(int n, Path gitPath) {
    	double average = averageNumberOfCommits(n,gitPath);
    	LocalDate day = LocalDate.now().minusDays(n);
    	int sum = 0;
    	HashMap<LocalDate, Integer> commits = numberOfCommitsPerDay(gitPath);
    	for(HashMap.Entry<LocalDate, Integer> m : commits.entrySet()) {
    		if(m.getKey().isAfter(day)) {
    			sum += (m.getValue() - average)*(m.getValue() - average);
    		}
    	}
    	return (double) sum/(double) numberOfCommits (n,gitPath);
    }
    
    //Renvoie le premier jour de la semaine de l'argument date
    public static LocalDate firstDayOfTheWeek(LocalDate date) {
    	TemporalField fieldFR = WeekFields.of(Locale.FRANCE).dayOfWeek();
    	return date.with(fieldFR, 1);
    }
    
    //renvoie le nombre de commits effectués depuis le 15 Septembre. 
    public static int totalNumberOfCommits(Path gitPath) {
    	return numberOfCommits((int) LocalDate.parse("2020-09-15").until(LocalDate.now(), ChronoUnit.DAYS), gitPath);
    }
    
    //Renvoie le nombre de commit de la semaine courante
    public static int numberOfCommitsInTheCurrentWeek(Path gitPath) {
    	return numberOfCommits((int) firstDayOfTheWeek(LocalDate.now()).until(LocalDate.now(), ChronoUnit.DAYS), gitPath);
    }

    //Renvoie une HashMap dont les clés sont les jours (seulement ceux où au moins un commit a été effectué) , et les valeurs sont les sommes des commits pour chaque jour.
    public static HashMap sumOfCommitsPerDay(Path gitPath){
        List<Commit> commits = parseLogFromCommand(gitPath);
        HashMap<String, Integer> commitsPerDay = new HashMap<String, Integer>();
        for(Commit c : commits) {
            if(commitsPerDay.containsKey(dateParser(c.date))){
                int valeur = commitsPerDay.get(dateParser(c.date));
                commitsPerDay.put(dateParser(c.date), valeur +1);
            }else {
                int valeur = 0;
                commitsPerDay.put(dateParser(c.date), valeur +1);
            }
        }
        return commitsPerDay;

    }

    //Renvoie le jour correspondant à la date sous forme d'une chaîne de caractères
    public static String dateParser(LocalDateTime date){
        DateTimeFormatter Pattern = DateTimeFormatter.ofPattern("EEEE/MM/yyyy");
        String DateParsed = date.format(Pattern);
        int index = DateParsed.indexOf("/");
        return DateParsed.substring(0, index);

    }
    //Renoie un tableau d'entier dont les indices correspondent aux heures d'une journée, et les valeurs aux nombres de commits effectués durant l'heure
    public static int[] numberOfCommitsPerHour(Path gitPath) {
    	List<Commit> commits = parseLogFromCommand(gitPath);
    	int [] commitsPerHour = new int[24];
    	for(Commit c : commits) {
    		 commitsPerHour[c.date.toLocalTime().getHour()]++;
    		}
    	return commitsPerHour;
    }
    
  //renvoie une map dont les clefs sont les noms des fichiers et les valeurs un tableau d'entier comptant le nombre de lignes retirées (indice 0) et ajoutées (indice 1) du commit
    public HashMap numberOfChanges(Path gitPath) {
    	return (new CommitDetails(gitPath, id)).numberOfChanges();
    }
    
  //renvoie une map dont les clefs sont les noms des fichiers et les valeurs un tableau d'entier comptant le nombre de lignes retirées (indice 0) et ajoutées (indice 1) de tous les commits
    public static HashMap numberOfChangesPerFile(Path gitPath) {
    	 List<Commit> commits = parseLogFromCommand(gitPath);
    	 HashMap<String, int[]> numberOfChanges = new HashMap<String, int[]>();
    	 for(Commit c : commits) {
    		 HashMap<String, int[]> changes = c.numberOfChanges(gitPath);
    		 for(String key : changes.keySet()) {
    			 if(numberOfChanges.containsKey(key)){
    				 numberOfChanges.get(key)[0] = numberOfChanges.get(key)[0] + changes.get(key)[0];
    				 numberOfChanges.get(key)[1] = numberOfChanges.get(key)[1] + changes.get(key)[1];
    			 }
    			 else {
    				 int [] t = {changes.get(key)[0], changes.get(key)[1]};
    				 numberOfChanges.put(key, t);
    			 }
    		 }
    	 }
    	 return numberOfChanges;
    }


    @Override
    public String toString() {
        return "Commit{" +
                "id='" + id + '\'' +
                (mergedFrom != null ? ("mergedFrom...='" + mergedFrom + '\'') : "") + //TODO: find out if this is the only optional field
                ", date='" + date.format(DateTimeFormatter.ofPattern("EEE LLL d HH:mm:ss yyyy", java.util.Locale.ENGLISH)) + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
