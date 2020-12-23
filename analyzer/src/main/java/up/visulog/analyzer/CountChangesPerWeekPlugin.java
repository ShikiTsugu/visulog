package up.visulog.analyzer;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class CountChangesPerWeekPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountChangesPerWeekPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(HashMap changes) {
        var result = new Result();
        result.changesPerWeek = changes;
        return result;
    }

    @Override
    public void run() {
        result = processLog(Commit.numberOfChangesPerWeek(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private Map<LocalDate, int[]> changesPerWeek = new HashMap<>();

        Map<LocalDate, int[]> getChangesPerWeek() {
            return changesPerWeek;
        }

        @Override
        public String getResultAsString() {
            return changesPerWeek.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Changes per week: <ul>");
            for (var item : changesPerWeek.entrySet()) {
                html.append("<li>").append(item.getKey()).append(": retirées ").append(item.getValue()[0]).append(" ajoutées ").append(item.getValue()[1]).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
}
