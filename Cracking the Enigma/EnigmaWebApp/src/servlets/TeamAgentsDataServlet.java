package servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.automatic.decryption.data.team.agents.TeamAgentsData;
import object.user.type.UserType;
import users.Agent;
import users.Ally;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static connection.constants.Constants.GSON_INSTANCE;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = TEAM_AGENTS_DATA_SERVLET, value = TEAM_AGENTS_DATA)
public class TeamAgentsDataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            if (SessionUtils.isUserDesiredUserType(request, response, UserType.ALLY, true)) {
                Ally ally = (Ally) ServletUtils.getUserManager(getServletContext())
                        .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));
                List<Agent> agentAlliesList = ally.getAgentList();
                List<TeamAgentsData> teamAgentsDataList = new ArrayList<>();

                agentAlliesList.forEach(x -> {
                    teamAgentsDataList.add(new TeamAgentsData(x.getUsername(), x.getThreadsAmount(), x.getPulledTasksAmount()));
                });

                ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(teamAgentsDataList));
            }
        }
    }
}
