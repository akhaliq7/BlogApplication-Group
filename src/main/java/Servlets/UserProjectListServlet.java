package Servlets;

import Model.Milestone;
import Model.Project;
import Model.User;
import Util.MustacheRenderer;
import Util.UrlGenerator;
import db.H2Milestone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Arrays;

public class UserProjectListServlet extends BaseServlet {

    private final H2Milestone h2Milestone;
    //good practice to declare the template that is populated as a constant, why?
    //declare your template here
    private static final String PROJECT_TEMPLATE = "projectListItem.mustache";
    private static final String PROJECTADD_TEMPLATE = "projectAdd.mustache";
    //servlet can be serialized
    private static final long serialVersionUID = 687117339002032958L;

    public UserProjectListServlet(H2Milestone h2Milestone)  {
        this.h2Milestone = h2Milestone;
    }

    //right now, setting the data for the page by hand, later that comes from a data store
    //helper method to create a MessageBoard object, which provides the data shown on the message board page

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get current session
        HttpSession session = request.getSession(false);
        // if the session exists
        if ( session != null ) {
            // get the current user for validation
            User current = (User) request.getSession().getAttribute("user");
            // get a list of projects for the user
            List<Project> projects = h2Milestone.findProjects(current.getID());
            // show the project add element
            showView(response, PROJECTADD_TEMPLATE, current);
            // loop through and show every project
            for (Project p : projects) {
                showView(response, PROJECT_TEMPLATE, p);
            }
        } else {
            // redirect to index page, session doesn't exist
            response.sendRedirect("/index");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get current session
        HttpSession session = request.getSession(false);
        // if the session exists
        if ( session != null ) {
            // get current user for validation
            User current = (User) request.getSession().getAttribute("user");
            // loop through requests parameters
            for (Enumeration p = request.getParameterNames(); p.hasMoreElements();) {
                // get the parameter name into string
                String key = (String) p.nextElement();
                // if the user selected to add a project
                if (key.equals("addProject")) {
                    // get the title parameter into a string
                    String title = request.getParameter("title");
                    // add the project to the database, generate a unique url for it too
                    h2Milestone.addProject(title, new UrlGenerator(h2Milestone).rndUrl(10), current.getID());
                    // refresh page
                    response.sendRedirect("/Projects");
                } else if (key.startsWith("delete_")) { // if the user selected to delete
                    // set the deletion id into a variable
                    int id = Integer.parseInt(key.substring(7));
                    // delete the project id, pass the current user for validation
                    h2Milestone.deleteProject(id, current);
                    // refresh page
                    response.sendRedirect("/Projects");
                }
            }
        } else {
            // load the login page, session doesn't exist
            response.sendRedirect("/Login");
        }
    }
}
