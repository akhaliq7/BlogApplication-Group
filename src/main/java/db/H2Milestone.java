package db;

import Model.Milestone;
import Model.Project;
import Util.UrlGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Model.User;

public class H2Milestone  implements AutoCloseable {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(H2Milestone.class);

    //public static final String MEMORY = "jdbc:h2:mem:test";
    public static final String FILE = "jdbc:h2:~/test";

    private Connection connection;

    static Connection getConnection(String db) throws SQLException, ClassNotFoundException {
        Class.forName("org.h2.Driver");  // ensure the driver class is loaded when the DriverManager looks for an installed class. Idiom.
        return DriverManager.getConnection(db, "sa", "");  // default password, ok for embedded.
    }

    public H2Milestone() {
        this(FILE);
    }

    public H2Milestone(String db) {
        try {
            connection = getConnection(db);
            loadResource("/user.sql");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // used to validate a users login details
    public User authenticateUser(String userEmail, String userPassword) {
        // setup the query
        final String AUTHUSER_QUERY = "SELECT id, email, name, password FROM user";
        // prepare a statement
        try (PreparedStatement ps = connection.prepareStatement(AUTHUSER_QUERY)) {
            // execute query and get results
            ResultSet rs = ps.executeQuery();
            // loop through results
            while (rs.next()) {
                // check if a corresponding details exist in the database
                if ( userEmail.equals( rs.getString(2) ) && userPassword.equals( rs.getString(4) ) ) {
                    // return a new user object
                    return new User(rs.getInt(1), rs.getString(2), rs.getString(3));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // return null, invalid details
        return null;
    }


    // check if a URL is taken
    public boolean checkUrl(String uurl) {
        // prepare query
        final String LIST_USERS_QUERY = "SELECT url FROM project";
        // setup prepared statement
        try (PreparedStatement ps = connection.prepareStatement(LIST_USERS_QUERY)) {
            // execute statement and get result
            ResultSet rs = ps.executeQuery();
            // loop through results
            while (rs.next()) {
                // if the url exists then return true
                if (uurl.equals(rs.getString(1))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // return false, url doesn't exist
        return false;
    }

    public void addUser(String email, String name, String password) {
        final String ADD_USER_QUERY = "INSERT INTO user (email, name, password) VALUES (?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(ADD_USER_QUERY)) {
            ps.setString(1, email);
            ps.setString(2, name);
            ps.setString(3, password);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Used to add a project to the database
    public void addProject(String title, String url, int owner) {
        // setup the statement with sufficient parameter replacement
        final String ADD_PROJECT_QUERY = "INSERT INTO project (title, url, owner) VALUES (?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(ADD_PROJECT_QUERY)) {
            // replace each ? with required parameter to prevent SQL injection
            ps.setString(1, title);
            ps.setString(2, url);
            ps.setInt(3, owner);
            // execute statement
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Used to modify details of a milestone
    public void modifyMilestone(String description, Date intendedDueDate, Date actualCompletionDate, int id, int projID) {
        // setup statement
        final String MODIFY_STATEMENT = "UPDATE milestone SET description = ?, intendedDueDate = ?, actualCompletionDate = ? WHERE id = ? AND project = ?";
        try (PreparedStatement ps = connection.prepareStatement(MODIFY_STATEMENT)) {
            // replace statement ? with parameters
            ps.setString(1, description);
            ps.setDate(2, intendedDueDate);
            ps.setDate(3, actualCompletionDate);
            ps.setInt(4, id);
            ps.setInt(5, projID);
            // execute statement
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Used to modify a project
    public void modifyProject(int id, String title) {
        // prepare statement
        final String MODIFY_STATEMENT = "UPDATE project SET title = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(MODIFY_STATEMENT)) {
            // replace ? with parameters
            ps.setString(1, title);
            ps.setInt(2, id);
            // execute statement
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Used to add a milestone to the database
    public void addMilestone(String description, Date intendedDueDate, int projID) {
        // prepare statement with parameter replacement
        final String ADD_MILESTONE_QUERY = "INSERT INTO milestone (description, intendedDueDate, actualCompletionDate, project) VALUES (?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(ADD_MILESTONE_QUERY)) {
            // replace ? with parameters
            ps.setString(1, description);
            ps.setDate(2, intendedDueDate);
            ps.setDate(3, null);
            ps.setInt(4, projID);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // get a project object from a URL
    public Project getProject(String URL) {
        // prepare statement
        final String LIST_PROJECTS_QUERY = "SELECT id, title, url, owner FROM project WHERE url = ?";
        try (PreparedStatement ps = connection.prepareStatement(LIST_PROJECTS_QUERY)) {
            // set the URL via ? replacement to prevent SQL injection
            ps.setString(1, URL);
            // execute query
            ResultSet rs = ps.executeQuery();
            // loop through results
            while (rs.next()) {
                // return project object
                return new Project(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), findMilestones(rs.getInt(1)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // return null, project with URL doesn't exist
        return null;
    }


    // delete a project with id, user object to validate
    public void deleteProject(int id, User user) {
        // prepare statement
        final String DELETE_QUERY = "DELETE FROM project WHERE id = (?) AND owner = (?)";
        try (PreparedStatement ps = connection.prepareStatement(DELETE_QUERY)) {
            // replace ? with parameters
            ps.setInt(1, id);
            ps.setInt(2, user.getID());
            // execute the statement
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // delete a milstone with id and project id
    public void deleteMilestone(int id, int projID) {
        // prepare statement
        final String DELETE_QUERY = "DELETE FROM milestone WHERE id = (?) AND project = (?)";
        try (PreparedStatement ps = connection.prepareStatement(DELETE_QUERY)) {
            // set ? to parameters
            ps.setInt(1, id);
            ps.setInt(2, projID);
            // execute statement
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // returns a list of projects for a user ID
    public List<Project> findProjects(int userID) {
        final String LIST_PROJECTS_QUERY = "SELECT id, title, url, owner FROM project WHERE owner = ?";
        // instantiate empty list of projects
        List<Project> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(LIST_PROJECTS_QUERY)) {
            // set ? to parameter
            ps.setInt(1, userID);
            // get results by executing query
            ResultSet rs = ps.executeQuery();
            // loop through results
            while (rs.next()) {
                // add a new project to the list for each result
                out.add(new Project(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), findMilestones(rs.getInt(1))));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // return the list of projects
        return out;
    }


    // returns a list of milestones for a project
    public List<Milestone> findMilestones(int projectID) {
        // prepare statement
        final String LIST_MILESTONES_QUERY = "SELECT id, description, intendedDueDate, actualCompletionDate FROM milestone WHERE project = ?";
        // instantiate empty list of milestones
        List<Milestone> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(LIST_MILESTONES_QUERY)) {
            // set the ? to parameter
            ps.setInt(1, projectID);
            // execute the query and store in rs
            ResultSet rs = ps.executeQuery();
            // loop through results
            while (rs.next()) {
                // add a new milestone to the list
                out.add(new Milestone(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getDate(4)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // return the list of milestones
        return out;
    }

    private void loadResource(String name) {
        try {
            String cmd = new Scanner(getClass().getResource(name).openStream()).useDelimiter("\\Z").next();
            PreparedStatement ps = connection.prepareStatement(cmd);
            ps.execute();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
