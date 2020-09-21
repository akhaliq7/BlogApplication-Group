package Servlets;

import Model.Milestone;
import Model.Project;
import Model.User;
import Util.MustacheRenderer;
import Util.UrlGenerator;
import db.H2Milestone;
import lombok.Data;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import javax.servlet.http.HttpSession;

public class LoginServlet extends BaseServlet {

    H2Milestone h2Milestone;
    private final MustacheRenderer mustache;

    public LoginServlet(H2Milestone h2Milestone) {
        this.h2Milestone = h2Milestone;
        this.mustache = new MustacheRenderer();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Gets the session
        HttpSession session = request.getSession(false);

        // If the session exists
        if ( session != null ) {
            // Load the projects servlet
            response.sendRedirect("Projects");
        } else {
            // If session doesn't exist
            // Render the login page
            String html = mustache.render("loginPage.mustache", null);
            response.setContentType("text/html");
            response.setStatus(200);
            response.getOutputStream().write(html.getBytes(Charset.forName("utf-8")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Session declaration
        HttpSession session;
        // Ensure email and password is valid and exists together
        User auth = h2Milestone.authenticateUser( request.getParameter("email"), request.getParameter("password") );
        // If authentication is successful
        if ( auth != null ) {
            // Get requests session, and set the attribute user to the object returned from authenticateUser method
            session = request.getSession();
            session.setAttribute("user", auth);
            // Redirect to Projects page
            response.sendRedirect("/Projects");
        } else {
            // Redirect to index since authentication failed.
            response.sendRedirect("/index");
        }
    }
}
