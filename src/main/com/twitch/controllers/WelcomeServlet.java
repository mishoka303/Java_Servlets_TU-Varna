package twitch.controllers;
import twitch.models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WelcomeServlet extends HttpServlet {
    String quotes[] = {
            "<h1 class=\"welcome-title\">Why do programmers wear glasses? Because they can't <span style=\"color:blue;\">C#</span></h1>",
            "<h1 class=\"welcome-title\">The real inventor, is <span style=\"color:blue;\">you!</span></h1>",
            "<h1 class=\"welcome-title\">Talk is cheap. Show me the <span style=\"color:blue;\">code</span>.</h1>",
            "<h1 class=\"welcome-title\">Programming is usually taught by <span style=\"color:blue;\">examples</span>.</h1>"
    };


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("RandomQuote", quotes[new Random().nextInt(quotes.length)]);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
