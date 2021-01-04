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
            Freemarker tmp = new Freemarker();

            var root = new HashMap<>();
            root.put("result", changesPerAuthor);

            return tmp.useOfTemplates(root, "count_changes_by_author.ftlh");
        }
    }
}
