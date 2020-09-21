package Servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.Enumeration;
import javax.servlet.http.HttpSession;

import Model.Milestone;
import Model.Project;
import Model.User;
import Util.UrlGenerator;
import db.H2Milestone;
import lombok.Data;


public class ProjectUrlServlet extends BaseServlet {

    private static final String MILESTONE_TEMPLATE = "milestonesListItem.mustache";
    private static final String MILESTONEADMIN_TEMPLATE = "milestonesAdmin.mustache";
    private static final String MILESTONEADD_TEMPLATE = "milestonesAdd.mustache";
    private final H2Milestone h2Milestone;
    private int projID;

    public ProjectUrlServlet(H2Milestone h2Milestone) {
        this.h2Milestone = h2Milestone;
    }

    //helper method to create a Milestone object, which provides the data shown on the topic page

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // if URL path is null then
        if (request.getPathInfo() == null) {
            // show error
            showView(response, "error.mustache", new Error("Invalid URL"));
        } else {
            // if the length of the url is long enough to exist
            if (request.getPathInfo().length() > 10) {
                // get the url
                String urlParam = request.getPathInfo().substring(1, 11);
                // get project with url
                Project currentP = h2Milestone.getProject(urlParam);
                // if current project exists
                if (currentP != null) {
                    // get session
                    HttpSession session = request.getSession(false);;
                    // if session exists
                    if (session != null) {
                        // get user
                        User currentU = (User) request.getSession().getAttribute("user");
                        // if current user is the owner of the project url specified
                        if ( currentP.getOwner() == currentU.getID() ) {
                            // show the admin view
                            showView(response, "milestoneTitleAdmin.mustache", new MilestoneInfo(currentP, urlParam));
                            showView(response, MILESTONEADD_TEMPLATE, new MilestoneInfo(urlParam));
                            for (Milestone m : currentP.getMilestones()) {
                                showView(response, MILESTONEADMIN_TEMPLATE, new MilestoneInfo(m, urlParam));
                            }
                        } else {
                            // show the non admin view
                            showView(response, "milestoneTitle.mustache", currentP);
                            for (Milestone m : currentP.getMilestones()) {
                                showView(response, MILESTONE_TEMPLATE, m);
                            }
                        }
                    } else {
                        // show non admin view
                        showView(response, "milestoneTitle.mustache", currentP);
                        for (Milestone m : currentP.getMilestones()) {
                            showView(response, MILESTONE_TEMPLATE, m);
                        }
                    }
                } else {
                    // show error
                    showView(response, "error.mustache", new Error("Invalid URL"));
                }
            } else {
                // show error
                showView(response, "error.mustache", new Error("Invalid URL"));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get current session
        HttpSession session = request.getSession(false);
        // If session exists
        if ( session != null ) {
            // Get the current user from session attributes
            User current = (User) request.getSession().getAttribute("user");
            // Iterate through parameter names of request
            for (Enumeration p = request.getParameterNames(); p.hasMoreElements();) {
                // Get the next parameter
                String key = (String) p.nextElement();
                // get the owner of the project by URL and compare to current user session, store in boolean
                boolean valid = h2Milestone.getProject(request.getPathInfo().substring(1, 11)).getOwner() == current.getID();
                // If invalid user then return
                if (!valid){
                    // user being bad
                    return;
                }
                // if a parameter name addmilestone exists
                if (key.equals("addMilestone")) {
                    // add a milestone with sufficient parameters
                    h2Milestone.addMilestone(request.getParameter("description"), Date.valueOf(request.getParameter("intendedDueDate")), h2Milestone.getProject(request.getPathInfo().substring(1, 11)).getId());
                    // Refresh the page
                    response.sendRedirect("/Project/" + request.getPathInfo().substring(1, 11));
                } else if (key.startsWith("delete_")) { // if the button delete was selected
                    // get the id of the deletion and parse to an integer
                    int id = Integer.parseInt(key.substring(7));
                    // delete the milestone with the id from the current project URL
                    h2Milestone.deleteMilestone(id, h2Milestone.getProject(request.getPathInfo().substring(1, 11)).getId());
                    // Refresh the page
                    response.sendRedirect("/Project/" + request.getPathInfo().substring(1, 11));
                } else if (key.startsWith("saveChanges_")) {
                    // get the id into a string
                    String s_id = key.substring(12);
                    // parse to integer
                    int id = Integer.parseInt(s_id);
                    // set completion date to null by default
                    Date acd = null;
                    // if the data is completed then parse the parameter to the acd date object
                    if (!request.getParameter("actualCompletionDate_" + s_id).equals("")) {
                        acd = Date.valueOf(request.getParameter("actualCompletionDate_" + s_id));
                    }
                    // modify the milestone in the database
                    h2Milestone.modifyMilestone(request.getParameter("description_" + s_id), Date.valueOf(request.getParameter("intendedDueDate_" + s_id)), acd, id, h2Milestone.getProject(request.getPathInfo().substring(1, 11)).getId());
                    // Refresh the page
                    response.sendRedirect("/Project/" + request.getPathInfo().substring(1, 11));
                }  else if (key.equals("modifyProjectName")) {
                    // Modify the project name with the title parameter
                    h2Milestone.modifyProject(h2Milestone.getProject(request.getPathInfo().substring(1, 11)).getId(), request.getParameter("title"));
                    // refresh the page
                    response.sendRedirect("/Project/" + request.getPathInfo().substring(1, 11));
                }

            }
        } else {
            // Redirect to login page
            response.sendRedirect("/Login");
        }
    }
}

// For mustache visualisation
@Data
class Error {
    private String errorMessage;

    Error(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}


// For mustache visualisation
@Data
class MilestoneInfo {
    private Milestone ms;
    private String url;
    private Project cP;

    MilestoneInfo (Milestone ms, String url) {
        this.ms = ms;
        this.url = url;
    }

    MilestoneInfo (String url) {
        this.url = url;
    }

    MilestoneInfo (Project cP, String url) {
        this.cP = cP;
        this.url = url;
    }
}

