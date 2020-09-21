package Servlets;

import Model.User;
import Util.MustacheRenderer;
import db.H2Milestone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.Charset;

public class LogoutServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the current session
        HttpSession session = request.getSession();
        // Invalidate the session
        session.invalidate();
        // Redirect the user to the login page
        response.sendRedirect("Login");
    }

}
