package Servlets;

import db.H2Milestone;
import Util.MustacheRenderer;
import Model.User;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class Register extends BaseServlet {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(Register.class);
    private static final long serialVersionUID = -7461821901454655091L;

    private final H2Milestone h2Milestone;
    private final MustacheRenderer mustache;

    public Register(H2Milestone h2Milestone) {
        mustache = new MustacheRenderer();
        this.h2Milestone = h2Milestone;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get current session
        HttpSession session = request.getSession(false);
        // if session exists
        if ( session != null ) {
            // redirect to projects page, user already logged in
            response.sendRedirect("Projects");
        } else {
            // load the registration  page
            String html = mustache.render("registerPage.mustache", null);
            response.setContentType("text/html");
            response.setStatus(200);
            response.getOutputStream().write(html.getBytes(Charset.forName("utf-8")));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        h2Milestone.addUser(email, name, password);
        response.sendRedirect("/Login");
    }

}
