package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountCommitsPerWeekPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountCommitsPerWeekPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(HashMap Commits) {
        var result = new Result();
        result.commitsPerWeek = Commits;
        return result;
    }

    @Override
    public void run() {
        result = processLog(Commit.numberOfCommitsPerWeek(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private Map<LocalDate, Integer> commitsPerWeek = new HashMap<>();

        Map<LocalDate, Integer> getCommitsPerWeek() {
            return commitsPerWeek;
        }

        @Override
        public String getResultAsString() {
            return commitsPerWeek.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            Freemarker tmp = new Freemarker();

            var root = new HashMap<>();
            root.put("result", commitsPerWeek);

            return tmp.useOfTemplates(root, "count_commits_by_week.ftlh");
        }
    }
}
