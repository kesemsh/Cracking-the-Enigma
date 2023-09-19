package servlets;

import battlefield.Battlefield;
import battlefield.manager.BattlefieldManager;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.user.type.UserType;
import users.Agent;
import users.Ally;
import users.User;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

import static connection.settings.ConnectionSettings.*;
import static connection.constants.Constants.*;

@WebServlet(name = RESET_CONTEST_SERVLET, value = RESET_CONTEST)
public class ResetContestServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (SessionUtils.doesUserHaveSession(request, response)) {
                if (SessionUtils.isUserDesiredUserType(request, response, UserType.AGENT, true)) {
                    Agent agent = (Agent) ServletUtils.getUserManager(getServletContext())
                            .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

                    ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(agent.getAlly().isReset()));
                }
            }
        } catch (Exception e) {
            ServletUtils.setResponseErrorMessage(response, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (SessionUtils.doesUserHaveSession(request, response)) {
                if (!SessionUtils.isUserDesiredUserType(request, response, UserType.AGENT, false)) {
                    User user = ServletUtils.getUserManager(getServletContext())
                            .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

                    if (user.getUserType().equals(UserType.UBOAT)) {
                        if (user.isJoinedToBattlefield()) {
                            BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());
                            Battlefield battlefield = battlefieldManager.getBattlefield(user.getJoinedGameTitle());

                            battlefield.resetBattlefield();
                            ServletUtils.setResponseSuccessMessage(response, "Contest reset successfully!");
                        } else {
                            ServletUtils.setResponseErrorMessage(response, "User has not joined a battlefield!");
                        }
                    } else {
                        ((Ally) user).onReset();
                        ServletUtils.setResponseSuccessMessage(response, "Ally reset successfully!");
                    }
                } else {
                    ServletUtils.setResponseErrorMessage(response, "User of type \"" + UserType.AGENT + "\" cannot access this information!");
                }
            }
        } catch (Exception e) {
            ServletUtils.setResponseErrorMessage(response, e.getMessage());
        }
    }
}