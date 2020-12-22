package up.visulog.analyzer;

import freemarker.template.Configuration;

import java.io.File;
import java.io.IOException;

public class Freemarker {
    public  Configuration freemarkerConfig;
    public Freemarker() throws IOException {
        this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_30);
        this.freemarkerConfig.setDirectoryForTemplateLoading(new File("/home/netbook/visulog/templates"));
    }


}
