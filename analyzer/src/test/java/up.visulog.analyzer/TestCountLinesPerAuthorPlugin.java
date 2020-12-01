package up.visulog.analyzer;

import org.junit.Test;
import up.visulog.gitrawdata.Lines;
import up.visulog.gitrawdata.LinesBuilder;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class TestCountLinesPerAuthorPlugin {
    @Test
    public void checkLinesSum() {
        var log = new ArrayList<Lines>();
        String[] authors = {"foo", "bar", "baz"};
        var entries = 20;
        for (int i = 0; i < entries; i++) {
            log.add(new LinesBuilder("").setAuthor(authors[i % 3]).createLines());
        }
        var res = CountLinesPerAuthorPlugin.processLog(log);
        assertEquals(authors.length, res.getLinesPerAuthor().size());
        var sum = res.getLinesPerAuthor().values()
                .stream().reduce(0, Integer::sum);
        assertEquals(entries, sum.longValue());
    }
}
