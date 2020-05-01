package twitch.controllers;

import twitch.models.Students;
import twitch.models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AccountServlet extends HttpServlet{
    static public ArrayList<User> users = new ArrayList<>(); //students in array
    static public Students students = new Students(); //POJO class

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Click/Go to Dashboard
        if (req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')).equals("/dashboard")) {
            if (req.getSession(false) == null) { resp.sendRedirect("index"); } //Empty session
            else {
                //Checks if there is ?id=
                if (req.getParameterMap().containsKey("id")) {
                    String query1 = req.getParameter("id");
                    int int_id = Integer.parseInt(query1);

                    if (int_id > students.getStudents().size() - 1 || int_id < 0 || students.getStudents().get(int_id) == null) {
                        resp.sendRedirect("index"); //invalid ID or out of bounds
                    } else {
                        req.setAttribute("user", students.getStudents().get(int_id));
                        req.setAttribute("id", int_id);
                        if ((int) req.getSession().getAttribute("logged_id") == int_id) {
                            req.setAttribute("editOption", "<a href=\"editProfile\">Edit Profile</a>   ");
                            req.setAttribute("LogOutOption", "   <a href=\"logout\">Log Out</a>");
                        }
                        req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
                        // String query1 = req.getQueryString(); //Query param
                    }
                }// Else check if session logged in
                else if (req.getSession().getAttribute("logged_id") != null) {
                    int int_id = (int) req.getSession().getAttribute("logged_id");
                    DashboardSessionOrganizer(req, resp, int_id);
                    req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);
                }
                // Redirect if not logged in or no ?id= query
                else resp.sendRedirect("index");
            }
        }

        // Click on LogOut
        if (req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')).equals("/logout")) {
            req.getSession().invalidate();
            resp.sendRedirect("index");
        }
        // Click on hideGreetz
        else if (req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/')).equals("/hideGreetz")) {
            HideGreetsForToday(req, resp);
            resp.sendRedirect("dashboard");
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String buttonRegister = req.getParameter("register");
        String buttonLogin = req.getParameter("login");
        String buttonEditProfile = req.getParameter("editProfile");

        //Upon clicking Register(on Register)
        if (buttonRegister != null) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            String cpassword = req.getParameter("cpassword");

            if (cpassword.equals(password)) {
                if (!checkDuplicates(username)) {
                    students.add(new User(username, password));

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
            students.getStudents().get(id).setName(name);
            resp.sendRedirect("dashboard");
        }
    }

    // On servlet startup
    public static void AccountServletOnStart(String xmlPath) {
        try { students = readFromXML(xmlPath); } catch (JAXBException ex) { ex.printStackTrace(); } catch (FileNotFoundException ex) { ex.printStackTrace(); }
    }

    // On servlet stop
    public static void AccountServletOnStop(String xmlPath) {
        try { writeToXML(xmlPath, students); } catch (JAXBException ex) { ex.printStackTrace(); }
    }

    // JAXB Spring Marshal (Write contents to XML file)
    public static void writeToXML(String xmlFile, Students students) throws JAXBException {

        // Създаване на JAXB контекст
        JAXBContext context = JAXBContext.newInstance(Students.class);
        // Създаване на marshaller инстанция
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // Записване в System.out
        m.marshal(students, System.out);
        // Записване във файл
        m.marshal(students, new File(xmlFile));
    }

    // JAXB Spring Unmarshal (Get contents from XML file)
    public static Students readFromXML(String xmlFile) throws JAXBException, FileNotFoundException {

        // Създаване на JAXB контекст
        JAXBContext context = JAXBContext.newInstance(Students.class);
        // Създаване на unmarshaller инстанция
        Unmarshaller um = context.createUnmarshaller();
        Students students = (Students) um.unmarshal(new FileReader(xmlFile));

        return students;
    }

    // Used in register.jsp
    protected boolean checkDuplicates(String tmpUsername) {
        for (int i = 0; i < students.getStudents().size(); i++) {
            User usr = students.getStudents().get(i);
            if (usr.getUsername().equals(tmpUsername)) {
                return true;
            }
        }
        return false;
    }

    // Used in login.jsp
    protected int checkLogin(String tmpUsername, String tmpPassword) {
        for (int i = students.getStudents().size() - 1; i >= 0; i--) {
            User usr = students.getStudents().get(i);
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
    protected void DashboardSessionOrganizer(HttpServletRequest req, HttpServletResponse resp, int int_id) {
        req.setAttribute("user", students.getStudents().get(int_id));
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
    protected void HideGreetsForToday(HttpServletRequest req, HttpServletResponse resp) {
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
