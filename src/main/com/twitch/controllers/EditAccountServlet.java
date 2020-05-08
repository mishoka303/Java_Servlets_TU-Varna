package twitch.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import twitch.models.JsonResult;
import twitch.models.Students;
import twitch.models.User;
import static twitch.controllers.AccountServlet.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class EditAccountServlet extends HttpServlet {
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //Moved to AuthFilter
        req.getRequestDispatcher("/editProfileNew.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String buttonEditName = req.getParameter("editName");

        //if (buttonEditName != null) {
            //int int_id = (int) req.getSession().getAttribute("logged_id");

            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = req.getReader().readLine()) != null) {
                sb.append(s);
            }

            //User newUser = (User) gson.fromJson(sb.toString(), User.class);

            JsonResult result = new JsonResult("Success");

            resp.setContentType("application/json;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            out.println(gson.toJson(result)); //Return result back to page
            out.flush();

            //req.setAttribute("ErrorMsg", "Your ID is: " + String.valueOf(int_id));

            //req.getRequestDispatcher("/editProfileNew.jsp").forward(req, resp);
        /*}
        else {
            resp.sendRedirect("editProfileNew");
        }*/
    }
}
