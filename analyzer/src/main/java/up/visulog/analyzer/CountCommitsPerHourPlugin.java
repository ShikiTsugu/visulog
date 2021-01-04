package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.time.LocalDate;
import java.util.*;

public class CountCommitsPerHourPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountCommitsPerHourPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(int [] Commits) {
        var result = new Result();
        result.commitsPerHour = Commits;
        return result;
    }

    @Override
    public void run() {
        result = processLog(Commit.numberOfCommitsPerHour(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private int [] commitsPerHour;

       int [] getCommitsPerHour() {
            return commitsPerHour;
        }

        @Override
        public String getResultAsString() {
            return commitsPerHour.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            Map<String, Integer> perHour= new LinkedHashMap<>();

            for(int i=0; i<24; i=i+2) {
                perHour.put(i + "-" + (i+2)%24,commitsPerHour[i]+commitsPerHour[i+1]);
            }
            Freemarker tmp = new Freemarker();

            var root = new HashMap<>();
            root.put("result", perHour);

            return tmp.useOfTemplates(root, "count_commits_by_hour.ftlh");
        }
    }
}
