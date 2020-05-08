package twitch.Filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static twitch.controllers.AccountServlet.*;

public class AuthFilter implements Filter {
//    private ServletContext context;

    public void init(FilterConfig fConfig) throws ServletException {
//        this.context = fConfig.getServletContext();
//        this.context.log("AuthFilter initialized - success!");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String type = req.getMethod();
        String URI = req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/'));

        //If session is FALSE and NOT LOGGED
        if (req.getSession(false) == null || req.getSession(false).getAttribute("logged_id") == null && type.equals("GET")) {
            if (URI.equals("/login")) { req.getRequestDispatcher("/login.jsp").forward(req, resp); }
            else if (URI.equals("/register")) { req.getRequestDispatcher("/register.jsp").forward(req, resp); }
            else if (URI.equals("/editProfile")) { resp.sendRedirect("login"); }
            else if (URI.equals("/editProfileNew")) { resp.sendRedirect("login"); }
            else if (URI.equals("/studentList")) { resp.sendRedirect("login"); }
            else chain.doFilter(req, resp);
        }
        //If session is TRUE and LOGGED IN
        else if (req.getSession(false) != null && req.getSession(false).getAttribute("logged_id") != null && type.equals("GET")) {
            if (URI.equals("/editProfile")) { req.getRequestDispatcher("/editProfile.jsp").forward(req, resp); }
            else if (URI.equals("/editProfileNew")) { req.getRequestDispatcher("/editProfileNew.jsp").forward(req, resp); }
            else if (URI.equals("/studentList")) { req.setAttribute("rows", students.getStudents()); req.getRequestDispatcher("/studentList.jsp").forward(req, resp); }
            else if (URI.equals("/login")) { resp.sendRedirect("dashboard"); }
            else if (URI.equals("/register")) { resp.sendRedirect("dashboard"); }
            else chain.doFilter(req, resp);
        }
        else chain.doFilter(req, resp);
    }

    public void destroy() {
        //close any resources here
    }
}
