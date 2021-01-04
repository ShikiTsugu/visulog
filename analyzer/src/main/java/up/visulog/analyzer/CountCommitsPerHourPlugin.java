package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            StringBuilder html = new StringBuilder("<div>Commits per Hour: <ul>");
            for(int i=0; i<24; i=i+2) {
                html.append("<li>").append(i + "-" + ((i+2)%24)+"h").append(": ").append(commitsPerHour[i]+commitsPerHour[i+1]).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
}
