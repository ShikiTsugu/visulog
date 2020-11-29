package up.visulog.cli;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestCLILauncher {
    /*
    TODO: one can also add integration tests here:
    - run the whole program with some valid options and look whether the output has a valid format
    - run the whole program with bad command and see whether something that looks like help is printed
     */
    @Test
    public void testArgumentParser() {
        try{
            var cli= new DefaultParser().parse(CLILauncher.cliOptions(), new String[]{".","countCommits"});
            var argument= cli.getOptionValue("addPlugin");
            assertTrue(!argument.isEmpty() ? true : false);

            var cli2= new DefaultParser().parse(CLILauncher.cliOptions(), new String[]{});
            var argument2= cli2.getOptionValue("nonExistingOption");
            assertFalse(argument2.isEmpty() ? true : false);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}
