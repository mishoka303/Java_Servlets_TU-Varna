package twitch.controllers;
import twitch.models.User;
import javax.servlet.*;
import javax.xml.bind.JAXBException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static twitch.controllers.AccountServlet.*;

public class MyServletContextListener implements ServletContextListener {
    protected String xmlPath = "C:\\Users\\ROG-Laptop\\IdeaProjects\\Zad\\src\\main\\com\\twitch\\Storage\\accounts.xml";

    public void contextInitialized(ServletContextEvent e) {
        AccountServletOnStart(xmlPath);
    }

    public void contextDestroyed(ServletContextEvent e) {
        //students.delete("u5");
        AccountServletOnStop(xmlPath);
    }
}
