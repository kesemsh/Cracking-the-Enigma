package servlets;

import battlefield.Battlefield;
import battlefield.manager.BattlefieldManager;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.automatic.decryption.active.teams.details.ActiveTeamDetails;
import object.user.type.UserType;
import users.User;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.List;

import static connection.constants.Constants.GSON_INSTANCE;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = ACTIVE_TEAMS_DETAILS_SERVLET, value = ACTIVE_TEAMS_DETAILS)
public class ActiveTeamsDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            if (!SessionUtils.isUserDesiredUserType(request, response, UserType.AGENT, false)) {
                User user = ServletUtils.getUserManager(getServletContext())
                        .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

                if (user.isJoinedToBattlefield()) {
                    BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());
                    Battlefield battlefield = battlefieldManager.getBattlefield(user.getJoinedGameTitle());
                    List<ActiveTeamDetails> activeTeamDetailsList = battlefield.getActiveTeamsDetailsList();

                    ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(activeTeamDetailsList));
                } else {
                    ServletUtils.setResponseErrorMessage(response, "User has not joined a battlefield!");
                }
            } else {
                ServletUtils.setResponseErrorMessage(response, "A client of type \"" + UserType.AGENT + "\" cannot access this information!");
            }
        }
    }
}
