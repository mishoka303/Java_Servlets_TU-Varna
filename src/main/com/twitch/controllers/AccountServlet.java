package twitch.controllers;

import twitch.models.User;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AccountServlet extends HttpServlet{
    List<User> users = new ArrayList<User>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Click Login
        if (req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')).equals("/login")) {
                if (req.getSession(false) == null || req.getSession(false).getAttribute("logged_id") == null) { req.getRequestDispatcher("/login.jsp").forward(req, resp); }
                else resp.sendRedirect("dashboard");
        }
        // Click Register
        else if (req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')).equals("/register")) {
            if (req.getSession(false) == null || req.getSession(false).getAttribute("logged_id") == null) { req.getRequestDispatcher("/register.jsp").forward(req, resp); }
            else resp.sendRedirect("dashboard");
        }
        // Click/Go to Dashboard
        else if (req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')).equals("/dashboard")) {
            //Check if there is ?id=
            if (req.getParameterMap().containsKey("id")) {
                String query1 = req.getParameter("id");
                int int_id = Integer.parseInt(query1);

                if (int_id > users.size() - 1 || users.get(int_id) == null) {
                    resp.sendRedirect("index");
                } else {
                    req.setAttribute("user", users.get(int_id));
                    req.setAttribute("id", int_id);
                    if ((int) req.getSession().getAttribute("logged_id") == int_id) {
                        req.setAttribute("editOption", "<a href=\"${pageContext.request.contextPath}/editProfile\">Edit Profile</a>");
                        req.setAttribute("LogOutOption", "<a href=\"logout\">Log Out</a>");
                    }
                    req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
                    // String query1 = req.getQueryString(); //Query param
                }
            // Check if session logged in
            }
            else if (req.getSession().getAttribute("logged_id") != null) {
                int int_id = (int) req.getSession().getAttribute("logged_id");
                DashboardSessionOrganizer(req, resp, int_id);
                req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
            }
            // Redirect if not logged in or no ?id= query
            else resp.sendRedirect("index");
        }
        // Click on editProfile
        else if (req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')).equals("/editProfile")) {
            if (req.getSession(false) != null && req.getSession(false).getAttribute("logged_id") != null) { req.getRequestDispatcher("/editProfile.jsp").forward(req, resp); }
            else resp.sendRedirect("index");
        }
        // Click on LogOut
        else if (req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')).equals("/logout")) {
            req.getSession().invalidate();
            resp.sendRedirect("index");
        }
        // Click on LogOut
        else if (req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')).equals("/logout")) {
            req.getSession().invalidate();
            resp.sendRedirect("index");
        }
        // Click on hideGreetz
        else if (req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')).equals("/hideGreetz")) {
            HideGreetsForToday(req, resp);
            resp.sendRedirect("dashboard");
        }
        else resp.sendRedirect("index");
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String buttonRegister = req.getParameter("register");
        String buttonLogin = req.getParameter("login");
        String buttonEditProfile = req.getParameter("editProfile");
        String buttonLogOut = req.getParameter("logout");

        //Upon clicking Register(on Register)
        if (buttonRegister != null) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String cpassword = req.getParameter("cpassword");

            if (cpassword.equals(password)) {
                if (!checkDuplicates(username)) {
                    users.add(new User("", username, password));

                    req.setAttribute("ErrorMsg", "Successful registration!<br>");
                    req.getRequestDispatcher("/login.jsp").forward(req, resp);
                }
                else {
                    req.setAttribute("ErrorMsg", "Sorry, the username already exists!<br>");
                    req.getRequestDispatcher("/register.jsp").forward(req, resp);
                }
            }
            else {
                req.setAttribute("ErrorMsg", "Sorry, the passwords do not match!<br>");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
            }
        }

        //Upon clicking Login(on Index)
        else if (buttonLogin != null) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            int result = checkLogin(username,password);
            if (result != -1) {
                req.getSession().setAttribute("logged_id", result);
                CookieLoginInspector(req, resp);
                resp.sendRedirect("dashboard");
            }
            else {
                req.setAttribute("ErrorMsg", "Sorry, the username or password is incorrect!<br>");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            }
        }

        //Upon clicking editProfile
        else if (buttonEditProfile != null) {
            int id = (int) req.getSession().getAttribute("logged_id");
            String name = req.getParameter("name");
            users.get(id).setName(name);
            resp.sendRedirect("dashboard");
        }
    }

    // Used in register.jsp
    protected boolean checkDuplicates(String tmpUsername) {
        for (int i = 0; i < users.size(); i++) {
            User usr = users.get(i);
            if (usr.getUsername().equals(tmpUsername)) {
                return true;
            }
        }
        return false;
    }

    // Used in login.jsp
    protected int checkLogin(String tmpUsername, String tmpPassword) {
        for (int i = users.size() - 1; i >= 0; i--) {
            User usr = users.get(i);
            if (usr.getUsername().equals(tmpUsername) && usr.getPassword().equals(tmpPassword)) {
                return i;
            }
        }
        return -1;
    }

    // Used to check if in Login there is already a Greetz cookie
    protected void CookieLoginInspector(HttpServletRequest req, HttpServletResponse resp) {
        boolean flag = false;
        Cookie ck[]= req.getCookies();
        for(int i = 0; i < ck.length; i++){
            if (ck[i].getName().equals("Greetz")) {
                flag = true;
                break;
            }
        }
        if (flag == false) {
            Cookie ck1 = new Cookie("Greetz","1");
            ck1.setMaxAge(-1); //By default is -1, I KNOW!
            resp.addCookie(ck1);
        }
    }

    // Used to check for cookie Greetz if it's true or false + additions(Edit Profile, Log Out)
    private void DashboardSessionOrganizer(HttpServletRequest req, HttpServletResponse resp, int int_id) {
        req.setAttribute("user", users.get(int_id));
        req.setAttribute("id", int_id);
        req.setAttribute("editOption", "<a href=\"editProfile\">Edit Profile</a>");
        req.setAttribute("LogOutOption", "<a href=\"logout\">Log Out</a>");

        Cookie ck[]= req.getCookies();
        for(int i = 0; i < ck.length; i++){
            if (ck[i].getName().equals("Greetz")) {
                if (ck[i].getValue().equals("0") ) {
                    req.setAttribute("CookieGreetz", "");
                }
                else if (ck[i].getValue().equals("1")) {
                    req.setAttribute("CookieGreetz", "<div class=\"alert\">\n" +
                            "<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';location.href='hideGreetz'\">&times;</span>\n" +
                            "Glad to see you log in for the first time today! Wishing you a productive day!\n" +
                            "</div>");
                }
                else
                    req.setAttribute("CookieGreetz", "<div class=\"alert\">\n" +
                            "<span class=\"closebtn\" onclick=\"this.parentElement.style.display='none';location.href='dashboard'\">&times;</span>\n" +
                            "Invalid attribute set for cookie \"Greetz\"! Did you try to eat it?\n" +
                            "</div>");
                break;
            }
        }
    }

    // Used to hide the cookie and set time to expire at the end of the day
    private void HideGreetsForToday(HttpServletRequest req, HttpServletResponse resp) {
        Cookie ck1[]= req.getCookies();
        for(int i = 0; i < ck1.length; i++){
            if (ck1[i].getName().equals("Greetz")) {
                Cookie ck = new Cookie("Greetz", "0");
                Date today, tomorrow;

                //FUNCTION get Today and Tommorow + reset to midnight(Calendar to Date parse, then parse to int in milis/1000)
                Calendar calendar = Calendar.getInstance();
                today = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                tomorrow = calendar.getTime();

                int t = (int) (tomorrow.getTime() - today.getTime())/1000;
                Cookie ck2 = new Cookie("CookieTime", t + "");
                ck.setMaxAge(t);
                resp.addCookie(ck);
                resp.addCookie(ck2);
            }
        }
    }
}
