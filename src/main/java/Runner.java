import Servlets.*;
import db.H2Milestone;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Runner {
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(Runner.class);

    private static final int PORT = 9003;

    private final H2Milestone h2Milestone;

    private Runner() {
        h2Milestone = new H2Milestone();
    }

    private void start() throws Exception {
        Server server = new Server(PORT);

        ServletContextHandler handler = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        handler.setInitParameter("org.eclipse.jetty.servlet.Default." + "resourceBase", "src/main/resources/webapp");

        DefaultServlet ds = new DefaultServlet();
        handler.addServlet(new ServletHolder(ds), "/");

        handler.addServlet(new ServletHolder(new Register(h2Milestone)), "/Register");
        handler.addServlet(new ServletHolder(new UserProjectListServlet(h2Milestone)), "/Projects");
        handler.addServlet(new ServletHolder(new ProjectUrlServlet(h2Milestone)), "/Project/*");
        handler.addServlet(new ServletHolder(new LoginServlet(h2Milestone)), "/Login");
        handler.addServlet(new ServletHolder(new LogoutServlet()), "/Logout");
        handler.addServlet(new ServletHolder(new IndexServlet()), "/index");

        server.start();
        LOG.info("Server started, will run until terminated");
        server.join();

    }

    public static void main(String[] args) {
        try {
            LOG.info("server starting...");
            new Runner().start();
        } catch (Exception e) {
            LOG.error("Unexpected error running: " + e.getMessage());
        }
    }
}