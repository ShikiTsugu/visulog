package up.visulog.analyzer;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class CountChangesPerAuthorPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountChangesPerAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(HashMap changes) {
        var result = new Result();
        result.changesPerAuthor = changes;
        return result;
    }

    @Override
    public void run() {
        result = processLog(Commit.numberOfChangesPerAuthor(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private Map<LocalDate, int[]> changesPerAuthor = new HashMap<>();

        Map<LocalDate, int[]> getChangesPerWeek() {
            return changesPerAuthor;
        }

        @Override
        public String getResultAsString() {
            return changesPerAuthor.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Changes per Author: <ul>");
            for (var item : changesPerAuthor.entrySet()) {
                html.append("<li>").append(item.getKey()).append(": retirées ").append(item.getValue()[0]).append(" ajoutées ").append(item.getValue()[1]).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
}
