package servlets;

import battlefield.manager.BattlefieldManager;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.automatic.decryption.data.task.details.DecryptionTaskDetails;
import object.automatic.decryption.results.DecryptionTaskResults;
import object.user.type.UserType;
import users.Agent;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.List;

import static connection.constants.Constants.*;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = DECRYPTION_TASKS_SERVLET, value = DECRYPTION_TASKS)
public class DecryptionTasksServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (SessionUtils.doesUserHaveSession(request, response)) {
                if (SessionUtils.isUserDesiredUserType(request, response, UserType.AGENT, true)) {
                    Agent agent = (Agent) ServletUtils.getUserManager(getServletContext())
                            .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

                    if (agent.isJoinedToBattlefield()) {
                        List<DecryptionTaskDetails> tasksList = agent.getAlly().getTasks(agent.getPulledTasksAmount());

                        agent.updatePulledTasks(tasksList.size());
                        ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(tasksList));
                    } else {
                        ServletUtils.setResponseErrorMessage(response, "User has not joined a battlefield!");
                    }
                }
            }
        } catch (Exception e) {
            if (e.getMessage().equals("null")) {
                ServletUtils.setResponseErrorMessage(response, "Error in Decryption Tasks Servlet");
            } else {
                ServletUtils.setResponseErrorMessage(response, e.getMessage());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (SessionUtils.doesUserHaveSession(request, response)) {
                if (SessionUtils.isUserDesiredUserType(request, response, UserType.AGENT, true)) {
                    Agent agent = (Agent) ServletUtils.getUserManager(getServletContext())
                            .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));
                    BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());
                    DecryptionTaskResults decryptionTaskResults = GSON_INSTANCE.fromJson(request.getReader(), DecryptionTaskResults.class);

                    battlefieldManager.getBattlefield(agent.getJoinedGameTitle()).onDecryptionResultsReceived(decryptionTaskResults);
                    agent.updateTasksFinished(decryptionTaskResults);
                    agent.getAlly().onTaskCompleted();
                    ServletUtils.setResponseSuccessMessage(response, "Successfully received the results!");
                }
            }
        } catch (Exception e) {
            ServletUtils.setResponseErrorMessage(response, e.getMessage());
        }
    }
}