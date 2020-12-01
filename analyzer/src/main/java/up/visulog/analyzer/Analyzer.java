package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Analyzer {
    private final Configuration config;

    private AnalyzerResult result;

    public Analyzer(Configuration config) {
        this.config = config;
    }

    public AnalyzerResult computeResults() {
        List<AnalyzerPlugin> plugins = new ArrayList<>();
        for (var pluginConfigEntry: config.getPluginConfigs().entrySet()) {
            var pluginName = pluginConfigEntry.getKey();
            var pluginConfig = pluginConfigEntry.getValue();
            var plugin = makePlugin(pluginName, pluginConfig);
            plugin.ifPresent(plugins::add);
        }
        // run all the plugins
        // TODO: try running them in parallel
        for (var plugin: plugins) plugin.run();

        // store the results together in an AnalyzerResult instance and return it
        return new AnalyzerResult(plugins.stream().map(AnalyzerPlugin::getResult).collect(Collectors.toList()));
    }

    // TODO: find a way so that the list of plugins is not hardcoded in this factory
    private Optional<AnalyzerPlugin> makePlugin(String pluginName, PluginConfig pluginConfig) {
        switch (pluginName) {
            case "countCommits" : return Optional.of(new CountCommitsPerAuthorPlugin(config));
            case "countLines" : return Optional.of(new CountLinesPerAuthorPlugin(config));
            case "countCommitsPerWeek" : return Optional.of(new CountCommitsPerWeekPlugin(config));
            case "countCommitsPerDay" : return Optional.of(new CountCommitsPerDayPlugin(config));
            case "sumOfCommitsPerDay" : return Optional.of(new SumOfCommitsPerDayPlugin(config));
            case "countCommitsPerHour" : return Optional.of(new CountCommitsPerHourPlugin(config));
            case "countLinesPerAuthor" : return Optional.of(new CountLinesPerAuthorPlugin(config));
            default : return Optional.empty();
        }
    }

}
