package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Lines;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountLinesPerAuthorPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public CountLinesPerAuthorPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Lines> gitLog) {
        var result = new Result();
        for (var lines : gitLog) {
            var nb = result.linesPerAuthor.getOrDefault(lines.author, 0);
            result.linesPerAuthor.put(lines.author, nb + 1);
        }
        return result;
    }

    @Override
    public void run() {
        result = processLog(Lines.parseLogFromCommand(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private final Map<String, Integer> linesPerAuthor = new HashMap<>();

        Map<String, Integer> getLinesPerAuthor() {
            return linesPerAuthor;
        }

        @Override
        public String getResultAsString() {
            return linesPerAuthor.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            StringBuilder html = new StringBuilder("<div>Lines per author: <ul>");
            for (var item : linesPerAuthor.entrySet()) {
                html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
            }
            html.append("</ul></div>");
            return html.toString();
        }
    }
}
