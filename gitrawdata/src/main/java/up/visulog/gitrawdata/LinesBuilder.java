package up.visulog.gitrawdata;

public class LinesBuilder{
    private int nbLines;
    private final String id;
    private String author;
    private int semaine;

    public LinesBuilder(String id) {
        this.id = id;
    }
    
    public LinesBuilder(int nbLines){
    	this.nbLines=nbLines;
    	return this;
    }

    public LinesBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public LinesBuilder setSemaine(int semaine) {
        this.semaine = semaine;
        return this;
    }

    public Lines createLines() {
        return new Lines(id, nbLines, author, semaine);
    }
}
