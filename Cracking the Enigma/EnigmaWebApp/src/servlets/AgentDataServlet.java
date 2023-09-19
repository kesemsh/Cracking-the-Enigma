package servlets;

import battlefield.Battlefield;
import battlefield.manager.BattlefieldManager;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.user.type.UserType;
import users.Agent;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

import static connection.constants.Constants.*;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = AGENT_DATA_SERVLET, value = AGENT_DATA)
public class AgentDataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            if (SessionUtils.isUserDesiredUserType(request, response, UserType.AGENT, true)) {
                Agent agent = (Agent) ServletUtils.getUserManager(getServletContext())
                        .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

                if (agent.isJoinedToBattlefield()) {
                    BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());
                    Battlefield battlefield = battlefieldManager.getBattlefield(agent.getJoinedGameTitle());
                    String dataType = request.getParameter(AGENT_DATA_TYPE);

                    if (dataType == null || dataType.isEmpty()) {
                        ServletUtils.setResponseErrorMessage(response, "Did not receive the \"" + AGENT_DATA_TYPE + "\" parameter!");
                    } else if (!dataType.equals(MACHINE_STRING) && !dataType.equals(MESSAGE_TO_DECRYPT) && !dataType.equals(BATTLEFIELD_NAME_PARAMETER)) {
                        ServletUtils.setResponseErrorMessage(response, "Data type must be \"" + MACHINE_STRING + "\" / \"" + MESSAGE_TO_DECRYPT + "\" / \"" + BATTLEFIELD_NAME_PARAMETER + "\"!");
                    } else {
                        switch (dataType) {
                            case MACHINE_STRING:
                                ServletUtils.setResponseSuccessMessage(response, battlefield.getFileAsString());
                                break;
                            /*case MESSAGE_TO_DECRYPT:
                                ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(battlefield.getMessageToDecrypt()));
                                break;*/
                            case BATTLEFIELD_NAME_PARAMETER:
                                ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(battlefield.getGameTitle()));
                                break;
                        }
                    }
                } else {
                    ServletUtils.setResponseErrorMessage(response, "User has not joined a battlefield!");
                }
            }
        }
    }
}
