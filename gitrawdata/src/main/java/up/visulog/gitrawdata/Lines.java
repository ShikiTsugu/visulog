package up.visulog.gitrawdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Lines {
    // FIXME: (some of) these fields could have more specialized types than String
    public final String id
    public final int nbLines;
    public final int semaine;
    public final String author;

    public Lines(String id,int nbLines, String author, int semaine) {
    	this.id=id;
    	this.nbLines=nbLines;
        this.author = author;
        this.semaine = semaine;
    }

    // TODO: factor this out (similar code will have to be used for all git commands)
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

    public static int parseLog(BufferedReader reader) {
        var result = new ArrayList<Lines>();
        int i=0;
        Optional<Lines> lines = parseLines(reader);
        while (lines.isPresent()) {
            i++;
            lines = parseLines(reader);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Lines{" +
                "id='" + id + '\'' +
                ", nbLines='"+nbLines + '\''+
                ", author='" + author + '\'' +
                ", semaine='"+ semaine + '\''+
                '}';
    }
