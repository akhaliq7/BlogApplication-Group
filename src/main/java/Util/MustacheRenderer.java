package Util;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

public class MustacheRenderer {

    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(MustacheRenderer.class);

    //sets the domain object
    private static final String TEMPLATE_ROOT = "templates";

    private MustacheFactory mustacheFactory;

    public MustacheRenderer() {
        this(TEMPLATE_ROOT);
    }

    public MustacheRenderer(String templateRoot) {

        mustacheFactory = new DefaultMustacheFactory(templateRoot);
    }

    //method to compile a mustache template with a data object
    //parameters are the template name and the data object
    //the method returns a string of the template with domain data
    public String render(String templateName, Object model) {
        //LOG.error("in render 1: template root: " + TEMPLATE_ROOT + ", template name: " + templateName);
        Mustache mustache = mustacheFactory.compile(templateName);
        //LOG.error("in render 2: template root: " + TEMPLATE_ROOT + ", template name: " + templateName);
        try (StringWriter stringWriter = new StringWriter()) {
            mustache.execute(stringWriter, model).close();
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
