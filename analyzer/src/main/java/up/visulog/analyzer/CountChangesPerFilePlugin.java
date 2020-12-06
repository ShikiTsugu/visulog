package up.visulog.analyzer;

import java.util.HashMap;
import java.util.Map;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

public class CountChangesPerFilePlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountChangesPerFilePlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(HashMap changes) {
        var result = new Result();
        result.changesPerFile = changes;
        return result;
    }

    @Override
    public void run() {
        result = processLog(Commit.numberOfChangesPerFile(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private Map<String, int[]> changesPerFile = new HashMap<>();

        Map<String, int[]> getChangesPerFile() {
            return changesPerFile;
        }

        @Override
        public String getResultAsString() {
            return changesPerFile.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Changes per file: <ul>");
            for (var item : changesPerFile.entrySet()) {
                html.append("<li>").append(item.getKey()).append(": retirées ").append(item.getValue()[0]).append(" ajoutées ").append(item.getValue()[1]).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
}
