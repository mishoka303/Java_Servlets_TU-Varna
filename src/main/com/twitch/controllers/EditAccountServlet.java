package twitch.controllers;

import com.google.gson.Gson;
import twitch.models.JsonResult;
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
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String URI = req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/'));

        if (URI.equals("/editProfileUsername")) { ProfileChangeUsername(URI, req, resp); }
        if (URI.equals("/editProfileNameBio")) { ProfileChangeNameBio(URI, req, resp); }
    }

    protected void ProfileChangeUsername(String URI, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int int_id = (int) req.getSession().getAttribute("logged_id");
        User newUser = UserFromJson(req, resp);

        if (!checkDuplicates(newUser.getUsername())) {
            students.getStudents().get(int_id).setUsername(newUser.getUsername());
            SendJsonResult(new JsonResult("Successfully updated the username!"), resp);
        }
        else
            // Sends result back to JS's fetch -> .then
            SendJsonResult(new JsonResult("Username is already taken!"), resp);


    }

    protected void ProfileChangeNameBio(String URI, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int int_id = (int) req.getSession().getAttribute("logged_id");
        User newUser = UserFromJson(req, resp);

            students.getStudents().get(int_id).setName(newUser.getName());
            students.getStudents().get(int_id).setBio(newUser.getBio());

        // Sends result back to JS's fetch -> .then
        SendJsonResult(new JsonResult("Successfully updated!"), resp);
    }

    protected User UserFromJson(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = req.getReader().readLine()) != null) {
            sb.append(s);
        }

        User newUser = (User) gson.fromJson(sb.toString(), User.class);

        return newUser;
    }

    protected void SendJsonResult(JsonResult result, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println(gson.toJson(result)); //Return result back to page
        out.flush();
    }
}