package servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import users.manager.UserManager;
import utils.ServletUtils;

import java.io.IOException;

import static connection.constants.Constants.GSON_INSTANCE;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = ALLIES_DATA_SERVLET, value = ALLIES_DATA)
public class AlliesDataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(userManager.getAllAlliesNames()));
    }
}
