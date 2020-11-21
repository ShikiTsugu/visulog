package up.visulog.cli;

import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;
import java.util.ArrayList;

import java.io.File;

public class CLILauncher {

    public static void main(String[] args) {
        var config = makeConfigFromCommandLineArgs(args);
        if (config.isPresent()) {
            var analyzer = new Analyzer(config.get());
            var results = analyzer.computeResults();
            System.out.println(results.toHTML());
        } else displayHelpAndExit();
    }
    
    //String s correspond au fichier Json
    public static Configuration configsFromJsonFile(String json){
    	Scanner sc = null;
		try {	
			String s = new File(json).getAbsolutePath();
			sc = new Scanner(new File(s));
		}
		catch(Exception e) {
			System.out.println("Error when opening file.");
			e.printStackTrace();
			System.exit(1);
		}
		var gitPath = FileSystems.getDefault().getPath(sc.nextLine());
    	var plugins = new HashMap<String, PluginConfig>();
    	return new Configuration(gitPath, plugins);
    }

    static Optional<Configuration> makeConfigFromCommandLineArgs(String[] args) {
        var gitPath = FileSystems.getDefault().getPath(".");
        var plugins = new HashMap<String, PluginConfig>();
        for (var arg : args) {
            if (arg.startsWith("--")) {
                String[] parts = arg.split("=");
                if (parts.length != 2) return Optional.empty();
                else {
                    String pName = parts[0];
                    String pValue = parts[1];
                    switch (pName) {
                        case "--addPlugin":
                            // TODO: parse argument and make an instance of PluginConfig

                            // Let's just trivially do this, before the TODO is fixed:

                            if (pValue.equals("countCommits")) plugins.put("countCommits", new PluginConfig() {
                            });
                            if (pValue.equals("countCommitsPerWeek")) plugins.put("countCommitsPerWeek", new PluginConfig() {
                            });
                            if (pValue.equals("countCommitsPerDay")) plugins.put("countCommitsPerDay", new PluginConfig() {
                            });
                            if (pValue.equals("sumOfCommitsPerDay")) plugins.put("sumOfCommitsPerDay", new PluginConfig() {
                            });


                            break;
                        case "--loadConfigFile":
                            // TODO (load options from a file)
                            break;
                        case "--justSaveConfigFile":
                            // TODO (save command line options to a file instead of running the analysis)
                            break;
                        default:
                            return Optional.empty();
                    }
                }
            } else {
                gitPath = FileSystems.getDefault().getPath(arg);
            }
        }
        return Optional.of(new Configuration(gitPath, plugins));
    }

    private static void displayHelpAndExit() {
        System.out.println("Wrong command...");
        //TODO: print the list of options and their syntax
        System.exit(0);
    }
}
