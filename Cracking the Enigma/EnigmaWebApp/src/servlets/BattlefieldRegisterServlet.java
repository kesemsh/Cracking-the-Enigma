package servlets;

import battlefield.Battlefield;
import battlefield.manager.BattlefieldManager;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.user.type.UserType;
import users.Ally;
import users.User;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

import static connection.constants.Constants.*;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = BATTLEFIELD_REGISTER_SERVLET, value = BATTLEFIELD_REGISTER)
public class BattlefieldRegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            if (SessionUtils.isUserDesiredUserType(request, response, UserType.AGENT, true)) {
                User user = ServletUtils.getUserManager(getServletContext())
                        .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));
                boolean userJoinedGameTitle = user.getJoinedGameTitle() != null;

                ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(userJoinedGameTitle));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            if (SessionUtils.isUserDesiredUserType(request, response, UserType.ALLY, true)) {
                Ally ally = (Ally) ServletUtils.getUserManager(getServletContext())
                        .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

                if (!ally.isJoinedToBattlefield()) {
                    String battlefieldName = request.getParameter(BATTLEFIELD_NAME_PARAMETER);

                    if (battlefieldName == null || battlefieldName.isEmpty()) {
                        ServletUtils.setResponseErrorMessage(response, "You must provide an existing battlefield name!");
                    } else {
                        BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());
                        Battlefield battlefield = battlefieldManager.getBattlefield(battlefieldName);

                        if (battlefield.getCurrentAlliesCount() < battlefield.getAlliesCount()) {
                            battlefield.joinBattlefield(ally);
                            ServletUtils.setResponseSuccessMessage(response, "Successfully joined the battlefield!");
                        } else {
                            ServletUtils.setResponseErrorMessage(response, "Battlefield is already full!");
                        }
                    }
                } else {
                    ServletUtils.setResponseErrorMessage(response, "User has already joined a battlefield!");
                }
            }
        }
    }
}
