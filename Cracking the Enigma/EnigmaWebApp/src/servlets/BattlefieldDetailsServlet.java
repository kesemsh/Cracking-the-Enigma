package servlets;

import battlefield.Battlefield;
import battlefield.manager.BattlefieldManager;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.user.type.UserType;
import users.User;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

import static connection.constants.Constants.*;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = BATTLEFIELD_DETAILS_SERVLET, value = BATTLEFIELD_DETAILS)
public class BattlefieldDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            if (!SessionUtils.isUserDesiredUserType(request, response, UserType.AGENT, false)) {
                if (request.getParameter(BATTLEFIELD_DETAILS_TYPE_PARAMETER).equals(ALLIES_COUNT)) {
                    User user = ServletUtils.getUserManager(getServletContext())
                            .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

                    if (user.isJoinedToBattlefield()) {
                        BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());
                        Battlefield battlefield = battlefieldManager.getBattlefield(user.getJoinedGameTitle());
                        int alliesCount = battlefield.getAlliesCount();

                        ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(alliesCount));
                    } else {
                        ServletUtils.setResponseErrorMessage(response, "User has not joined a battlefield!");
                    }
                } else {
                    ServletUtils.setResponseErrorMessage(response, "Details type requested is not supported!");
                }
            } else {
                ServletUtils.setResponseErrorMessage(response, "A client of type \"" + UserType.AGENT + "\" cannot access this information!");
            }
        }
    }
}
