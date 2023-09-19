package servlets;

import battlefield.Battlefield;
import battlefield.manager.BattlefieldManager;
import jakarta.servlet.ServletException;
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

@WebServlet(name = CONTEST_STATUS_SERVLET, value = CONTEST_STATUS)
public class ContestStatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            User user = ServletUtils.getUserManager(getServletContext())
                    .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

            if (user.isJoinedToBattlefield()) {
                BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());
                Battlefield battlefield = battlefieldManager.getBattlefield(user.getJoinedGameTitle());

                ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(battlefield.getContestStatus()));
            } else {
                ServletUtils.setResponseErrorMessage(response, "User has not joined a battlefield!");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            User user = ServletUtils.getUserManager(getServletContext())
                    .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

            if (user.isJoinedToBattlefield()) {
                if (user.getClass().equals(Ally.class)) {
                    Ally ally = (Ally) user;

                    if (ally.getAgentsCount() == 0) {
                        ServletUtils.setResponseErrorMessage(response, "Ally does not have any connected agents!");
                        return;
                    }

                    try {
                        validateStatusRequest(request);
                        String taskSizeParameter = request.getParameter(TASK_SIZE_PARAMETER);
                        int taskSize = Integer.parseInt(taskSizeParameter);
                        ally.setTaskSize(taskSize);

                    } catch (Exception e) {
                        ServletUtils.setResponseErrorMessage(response, e.getMessage());
                        return;
                    }
                }
                BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());
                Battlefield battlefield = battlefieldManager.getBattlefield(user.getJoinedGameTitle());

                if (!user.isReady()) {
                    user.setReady(true);
                    battlefield.updateGameStatus();

                    ServletUtils.setResponseSuccessMessage(response, "User is now ready!");
                } else {
                    ServletUtils.setResponseErrorMessage(response, "User is already ready!");
                }
            } else {
                ServletUtils.setResponseErrorMessage(response, "User has not joined a battlefield!");
            }
        }
    }

    private void validateStatusRequest(HttpServletRequest request) throws ServletException {
        String taskSizeParameter = request.getParameter(TASK_SIZE_PARAMETER);

        if (taskSizeParameter == null) {
            throw new ServletException("Missing the \"" + TASK_SIZE_PARAMETER + "\" parameter!");
        } else if (Integer.parseInt(taskSizeParameter) < 1) {
            throw new ServletException("Task size is below 1!");
        }
    }
}
