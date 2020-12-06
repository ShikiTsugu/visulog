package up.visulog.gitrawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;

public class CommitDetails {
    private HashMap<String, LinkedList<String>> changes;
    private String id;

    public CommitDetails(String id){
        this.id = id;
        changes = new HashMap<String, LinkedList<String>>();
    }

    public CommitDetails(Path gitPath, String id){
        this.id = id;
        changes = parseShowFromCommand(gitPath,id).changes;
    }

    //renvoie l'objet CommitDetails donné par la commande "git show <id>" exécutéé depuis gitPath
    public static CommitDetails parseShowFromCommand(Path gitPath, String id) {
        ProcessBuilder builder =
                new ProcessBuilder("git", "show", id).directory(gitPath.toFile());
        Process process;
        try {
            //exécution de builder
            process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException("Error running \"git show\".", e);
        }
        InputStream is = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return parseDetails(reader, id);
    }

    //crée un objet de la class CommitDetails à partir de la sortie de la commande git show
    public static CommitDetails parseDetails(BufferedReader input, String id){
        try {
            String line = input.readLine();
            if(!line.startsWith("commit")){
                parseError();
            }
            CommitDetails detail = new CommitDetails(id);
            String s = "";

            while(line!=null) {
                if (line.startsWith("+++")) {
                    int index = line.lastIndexOf("/");
                    s = line.substring(index+1);
                    detail.changes.put(s, new LinkedList<String>());
                    line = input.readLine();
                }
                if (!s.equals("") && !line.startsWith("---")&& (line.startsWith("+") || line.startsWith("-"))) {
                    detail.changes.get(s).add(line);
                }
                line = input.readLine();
            }
            return detail;
        }catch (IOException e) {
            parseError();
            }
        return null;
    }

    private static void parseError() {
        throw new RuntimeException("Wrong commit format.");
    }
    
    //renvoie une map dont les clefs sont les noms des fichiers et les valeurs un tableau d'entier comptant le nombre de lignes retirées (indice 0) et ajoutées (indice 1)
    public HashMap numberOfChanges() {
    	HashMap numberOfChanges = new HashMap<String, int[]>();
    	for(String key : changes.keySet()){
    		numberOfChanges.put(key, new int[2]);
    		for(String value : changes.get(key) ) {
    			if(value.startsWith("-")) {
    				int t[] = (int[]) numberOfChanges.get(key);
    				t[0]++;
    			}
    			else {
    				int t[] = (int[]) numberOfChanges.get(key);
    				t[1]++;
    			}
    		}
    	}
    	return numberOfChanges;
    }
    
    public void Afficher(){
         for(String m : changes.keySet()){
        	 System.out.println(m);
             LinkedList<String> l = changes.get(m);
             for(String s : l){
                 System.out.println("  " + s);
             }
         }
    }
}
