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

public class IndexServlet extends BaseServlet {

    public IndexServlet() {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirects to Login page
        response.sendRedirect("Login");
    }
}
