package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountCommitsPerDayPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountCommitsPerDayPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(HashMap Commits) {
        var result = new Result();
        result.commitsPerDay = Commits;
        return result;
    }

    @Override
    public void run() {
        result = processLog(Commit.numberOfCommitsPerDay(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private Map<LocalDate, Integer> commitsPerDay = new HashMap<>();

        Map<LocalDate, Integer> getCommitsPerday() {
            return commitsPerDay;
        }

        @Override
        public String getResultAsString() {
            return commitsPerDay.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            Freemarker tmp = new Freemarker();

            var root = new HashMap<>();
            root.put("result", commitsPerDay);

            return tmp.useOfTemplates(root, "count_commits_by_day.ftlh");
        }
    }
}
