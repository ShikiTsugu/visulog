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
                Freemarker tmp = new Freemarker();

                var root = new HashMap<>();
                root.put("result", commitsPerDaySum);

                return tmp.useOfTemplates(root, "count_commits_by_day.ftlh");
            }
        }
    }


