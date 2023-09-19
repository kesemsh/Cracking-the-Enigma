package servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.automatic.decryption.data.input.agent.AgentInputData;
import object.user.type.UserType;
import users.Agent;
import users.Ally;
import users.manager.UserManager;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

import static connection.constants.Constants.*;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = LOGIN_SERVLET, value = LOGIN)
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        if (usernameFromSession == null) {
            String usernameFromParameter = request.getParameter(USERNAME_PARAMETER);

            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                ServletUtils.setResponseErrorMessage(response, "No name was given!");
            } else {
                usernameFromParameter = usernameFromParameter.trim();
                String userTypeStringFromParameter = request.getParameter(USER_TYPE_PARAMETER);
                UserType userTypeFromParameter = UserType.fromString(userTypeStringFromParameter);

                if (userTypeFromParameter == null) {
                    ServletUtils.setResponseErrorMessage(response, "No user type was given!");
                } else {
                    synchronized (this) {
                        if (userManager.doesUserExist(usernameFromParameter)) {
                            String errorMessage = "Username \"" + usernameFromParameter + "\" of type " + userTypeFromParameter + " already exists! Please enter a different username!";

                            ServletUtils.setResponseErrorMessage(response, errorMessage);
                        } else {
                            if (userTypeFromParameter.equals(UserType.UBOAT) || userTypeFromParameter.equals(UserType.ALLY)) {
                                switch (userTypeFromParameter) {
                                    case UBOAT:
                                        userManager.addUBoat(usernameFromParameter);
                                        break;
                                    case ALLY:
                                        userManager.addAllies(usernameFromParameter);
                                        break;
                                }

                                request.getSession(true).setAttribute(USER_TYPE_PARAMETER, userTypeFromParameter);
                                request.getSession(true).setAttribute(USERNAME_PARAMETER, usernameFromParameter);
                                ServletUtils.setResponseSuccessMessage(response, "Logged in successfully!");
                            } else {
                                ServletUtils.setResponseErrorMessage(response, "Invalid user type! To login as Agent, please use a Post request!");
                            }
                        }
                    }
                }
            }
        } else {
            ServletUtils.setResponseSuccessMessage(response, "Logged in successfully!");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        if (usernameFromSession == null) {
            String usernameFromParameter = request.getParameter(USERNAME_PARAMETER);

            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                ServletUtils.setResponseErrorMessage(response, "No name was given!");
            } else {
                usernameFromParameter = usernameFromParameter.trim();
                String userTypeStringFromParameter = request.getParameter(USER_TYPE_PARAMETER);
                UserType userTypeFromParameter = UserType.fromString(userTypeStringFromParameter);

                if (userTypeFromParameter == null) {
                    ServletUtils.setResponseErrorMessage(response, "No user type was given!");
                } else {
                    synchronized (this) {
                        if (userManager.doesUserExist(usernameFromParameter)) {
                            String errorMessage = "Username \"" + usernameFromParameter + "\" of type " + userTypeFromParameter + " already exists! Please enter a different username!";

                            ServletUtils.setResponseErrorMessage(response, errorMessage);
                        } else {
                            if (userTypeFromParameter.equals(UserType.AGENT)) {
                                AgentInputData agentInputData;

                                try {
                                    agentInputData = GSON_INSTANCE.fromJson(request.getReader(), AgentInputData.class);
                                } catch (Exception e) {
                                    ServletUtils.setResponseErrorMessage(response, "Data received is not in the format of agent input data!");
                                    return;
                                }

                                if (!userManager.doesUserExist(agentInputData.getAllyName()) || !userManager.isUserOfType(agentInputData.getAllyName(), UserType.ALLY)) {
                                    String errorMessage = "Username \"" + agentInputData.getAllyName() + "\" of type " + UserType.ALLY + " does not exist!";

                                    ServletUtils.setResponseErrorMessage(response, errorMessage);
                                } else {
                                    Ally ally = (Ally) ServletUtils.getUserManager(getServletContext()).getUser(agentInputData.getAllyName(), UserType.ALLY);

                                    userManager.addAgent(usernameFromParameter, ally, agentInputData.getThreadsAmount(), agentInputData.getPulledTasksAmount());
                                    Agent agent = (Agent) userManager.getUser(usernameFromParameter, UserType.AGENT);

                                    ally.addAgent(agent);
                                    request.getSession(true).setAttribute(USER_TYPE_PARAMETER, userTypeFromParameter);
                                    request.getSession(true).setAttribute(USERNAME_PARAMETER, usernameFromParameter);
                                    ServletUtils.setResponseSuccessMessage(response, "Logged in successfully!");
                                }
                            }
                        }
                    }
                }
            }
        } else {
            ServletUtils.setResponseSuccessMessage(response, "Logged in successfully!");
        }
    }
}
