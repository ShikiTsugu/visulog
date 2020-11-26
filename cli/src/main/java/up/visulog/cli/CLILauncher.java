package up.visulog.cli;

import up.visulog.analyzer.Analyzer;
import up.visulog.config.Configuration;
import up.visulog.config.PluginConfig;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Optional;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.*;

import java.io.*;
import org.apache.commons.cli.*;
import org.json.*;

public class CLILauncher {

    public static void main(String[] args) {
        try {
            var cli = new DefaultParser().parse(cliOptions(), args);
            var config = configFromCli(cli);
            var analyzer = new Analyzer(config);
            var results = analyzer.computeResults();
            System.out.println(results.toHTML());
        } catch (ParseException e) {
            //TODO nice errors
            System.out.println(e.getMessage());
            var fmt= new HelpFormatter();
            fmt.printHelp("Visulog",help());
        }

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

  /* public static void commandOptionsToJsonFile(String parameter) throws IOException {
        Runtime objetExecution = Runtime.getRuntime();
        var myCommand= objetExecution.exec("man" + parameter);
        InputStream i = myCommand.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(i));
        String line;
        while((line=reader.readLine()) != null){
        } destiné à l'utilisation de la commande justSaveConfigFile.
    }*/

    /* Liste des plugins :
    "countCommits"
    "countCommitsPerWeek"
    "countCommitsPerDay"
    "sumOfCommitsPerDay"
    "countCommitsPerHour"
    */

    public static Options help(){
        var option= new Options();
        option.addOption(Option.builder().longOpt( "help" )
                .desc( "display all the available options" )
                .hasArg(false)
                .build() );
        System.out.println("Use the command --help to display the options manual with the correct syntaxes.");
        return option;
    }

    public static void displayHelp(){
        var h = new HelpFormatter();
        h.printHelp("Visulog", cliOptionsWithoutHelp());
        System.exit(0);
    }

    public static Options cliOptionsWithoutHelp(){
        var option= new Options();
        option.addOption(Option.builder().longOpt( "addPlugin" )
                .desc( "add a plugin to config" )
                .hasArg()
                .argName("PLUGIN")
                .build() );
        option.addOption(Option.builder().longOpt( "loadConfigFile" )
                .desc( "load options from a file" )
                .hasArg()
                .argName("FILE")
                .build() );
        option.addOption(Option.builder().longOpt( "justSaveConfigFile" )
                .desc( "save command line options to a file instead of running the analysis" )
                .hasArg()
                .argName("OPTION")
                .build() );
        return option;
    }

    public static Options cliOptions(){
        var option= new Options();
        option.addOption(Option.builder().longOpt( "help" )
                .desc( "display all the available options" )
                .hasArg(false)
                .build() );
        option.addOption(Option.builder().longOpt( "addPlugin" )
                .desc( "add a plugin to config" )
                .hasArg()
                .argName("PLUGIN")
                .build() );
        option.addOption(Option.builder().longOpt( "loadConfigFile" )
                .desc( "load options from a file" )
                .hasArg()
                .argName("FILE")
                .build() );
        option.addOption(Option.builder().longOpt( "justSaveConfigFile" )
                .desc( "save command line options to a file instead of running the analysis" )
                .hasArg()
                .argName("OPTION")
                .build() );
        return option;
    }

    public static Configuration configFromCli(CommandLine cli){
        if(cli.hasOption("help")){displayHelp();}

        var p= cli.getOptionValues("addPlugin");
        var plugins = new HashMap<String, PluginConfig>();
        for(var plugin : p){
            plugins.put(plugin,new PluginConfig());
        }
        /*var lcf= cli.getOptionValues("loadConfigFile");
        for(var difflcf : lcf) {
            configsFromJsonFile(difflcf);
        }
        var jscf= cli.getOptionValues("justSaveConfigFile");
        for(var diffjscf : jscf){
            commandOptionsToJsonFile(diffjscf);
        }*/
        // pour le moment je n'ai pas eu le temps de test loadConfigFile mais je fais une commit de ça pour l'instant.
        // ne pas oublier de rajouter le fonctionnement de la commande justSaveConfigFile.

        var gitPath = FileSystems.getDefault().getPath(".");
        if(cli.getArgs()[0] != null) {
            gitPath = FileSystems.getDefault().getPath(cli.getArgs()[0]);
        }
        return new Configuration(gitPath, plugins);
    }
}
