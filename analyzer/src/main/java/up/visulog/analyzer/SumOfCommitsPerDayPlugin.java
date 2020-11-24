package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.util.HashMap;
import java.util.Map;


    public class SumOfCommitsPerDayPlugin implements AnalyzerPlugin {
        private final Configuration configuration;
        private Result result;

        public SumOfCommitsPerDayPlugin(Configuration generalConfiguration) {
            this.configuration = generalConfiguration;
        }

        static Result processLog(HashMap Commits) {
            var result = new Result();
            result.commitsPerDaySum = Commits;
            return result;
        }

        @Override
        public void run() {
            result = processLog(Commit.sumOfCommitsPerDay(configuration.getGitPath()));
        }

        @Override
        public Result getResult() {
            if (result == null) run();
            return result;
        }

        static class Result implements AnalyzerPlugin.Result {
            private Map<String, Integer> commitsPerDaySum = new HashMap<>();

            Map<String, Integer> getCommitsPerDaySum() {
                return commitsPerDaySum;
            }

            @Override
            public String getResultAsString() {
                return commitsPerDaySum.toString();
            }

            @Override
            public String getResultAsHtmlDiv() {
                StringBuilder html = new StringBuilder("<div>The sum of commits per day: <ul>");
                for (var item : commitsPerDaySum.entrySet()) {
                    html.append("<li>").append(item.getKey()).append(": ").append(item.getValue()).append("</li>");
                }
                html.append("</ul></div>");
                return html.toString();
            }
        }
    }


