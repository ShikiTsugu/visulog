package up.visulog.analyzer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

public class Freemarker {
    public  Configuration freemarkerConfig;

    public Freemarker() {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_30);
        this.freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates/");
    }

    public String useOfTemplates(HashMap hashMap, String templatePath) {
        Template template= null;
        String r= "";
        try {
            template= freemarkerConfig.getTemplate(templatePath);
            Writer out = new StringWriter();
            template.process(hashMap, out);
            r= out.toString();
        } catch (IOException | TemplateException exception) {
            exception.printStackTrace();
        }
        return r;
    }
}
