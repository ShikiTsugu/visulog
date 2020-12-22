import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

public class TestFreemarker {
    public static void main(String[] args) throws IOException, TemplateException {
        var config = new Freemarker();
        var template = config.freemarkerConfig.getTemplate("count_commits_by_author.ftlh");
        var root = new HashMap<>();
        root.put("result", new int[]{1, 2, 3, 4});
        Writer out = new StringWriter();
        template.process(root, out);
        out.toString();
    }
}
