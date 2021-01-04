package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MostCommitsPlugin implements AnalyzerPlugin {
    private final Configuration configuration;
    private Result result;

    public MostCommitsPlugin(Configuration generalConfiguration) {
        this.configuration = generalConfiguration;
    }

    static Result processLog(List<Commit> gitLog) {
        var result = new Result();
        for (var commit : gitLog) {
            var nb = result.commitsPerAuthor.getOrDefault(commit.author, 0);
            result.commitsPerAuthor.put(commit.author, nb + 1);
        }
        int max = 0;
        for (Map.Entry<String,Integer> entry: result.commitsPerAuthor.entrySet()){
            if(max < entry.getValue()){
                max = entry.getValue();
                result.Mostcommits.clear();
                result.Mostcommits.put(entry.getKey(),entry.getValue());
            }
        }
        return result;
    }

    @Override
    public void run() {
        result = processLog(Commit.parseLogFromCommand(configuration.getGitPath()));
    }

    @Override
    public Result getResult() {
        if (result == null) run();
        return result;
    }

    static class Result implements AnalyzerPlugin.Result {
        private final Map<String, Integer> commitsPerAuthor = new HashMap<>();
        private final Map<String, Integer> Mostcommits = new HashMap<>();

        Map<String, Integer> getMostCommits() {
            return Mostcommits;
        }

        @Override
        public String getResultAsString() {
            return Mostcommits.toString();
        }

        @Override
        public String getResultAsHtmlDiv() {
            Freemarker tmp = new Freemarker();

            var root = new HashMap<>();
            root.put("result", Mostcommits);

            return tmp.useOfTemplates(root, "most_commits_by_author.ftlh");
        }
    }
}
