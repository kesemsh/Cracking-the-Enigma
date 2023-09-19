package servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.automatic.decryption.data.agent.progress.AgentProgressData;
import object.automatic.decryption.data.task.details.DecryptionTaskDetails;
import object.user.type.UserType;
import users.Agent;
import users.Ally;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.List;

import static connection.constants.Constants.GSON_INSTANCE;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = AGENTS_PROGRESS_DATA_SERVLET, value = AGENTS_PROGRESS_DATA)
public class AgentsProgressDataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (SessionUtils.doesUserHaveSession(request, response)) {
                if (SessionUtils.isUserDesiredUserType(request, response, UserType.ALLY, true)) {
                    Ally ally = (Ally) ServletUtils.getUserManager(getServletContext())
                            .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

                    if (ally.isJoinedToBattlefield()) {
                        List<AgentProgressData> progressData = ally.getAgentsProgressData();

                        ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(progressData));
                    } else {
                        ServletUtils.setResponseErrorMessage(response, "User has not joined a battlefield!");
                    }
                }
            }
        } catch (Exception e) {
            ServletUtils.setResponseErrorMessage(response, e.getMessage());
        }
    }
}
