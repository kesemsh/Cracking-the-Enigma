package servlets;

import battlefield.Battlefield;
import battlefield.manager.BattlefieldManager;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.user.type.UserType;
import users.Agent;
import users.Ally;
import users.User;
import utils.ServletUtils;
import utils.SessionUtils;
import static connection.settings.ConnectionSettings.*;

import java.io.IOException;

@WebServlet(name = LOGOUT_SERVLET, value = LOGOUT)
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            User user = ServletUtils.getUserManager(getServletContext())
                    .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

            if (user.isJoinedToBattlefield()) {
                BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());

                if (user.getUserType().equals(UserType.UBOAT)) {
                    battlefieldManager.removeBattlefield(user.getJoinedGameTitle());
                } else if (user.getUserType().equals(UserType.ALLY)) {
                    Battlefield battlefield = battlefieldManager.getBattlefield(user.getJoinedGameTitle());

                    battlefield.leaveBattlefield((Ally) user);
                } else {
                    ((Agent) user).getAlly().removeAgentFromBattlefield(((Agent) user));
                }
            }

            ServletUtils.getUserManager(getServletContext()).removeUser(UserType.fromString(SessionUtils.getUserType(request)), SessionUtils.getUsername(request));
            SessionUtils.clearSession(request);
        }
    }
}
