package servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.automatic.decryption.data.dm.progress.DMProgressData;
import object.user.type.UserType;
import users.Ally;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

import static connection.constants.Constants.GSON_INSTANCE;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = DM_PROGRESS_DATA_SERVLET, value = DM_PROGRESS_DATA)
public class DMProgressDataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if (SessionUtils.doesUserHaveSession(request, response)) {
                if (SessionUtils.isUserDesiredUserType(request, response, UserType.ALLY, true)) {
                    Ally ally = (Ally) ServletUtils.getUserManager(getServletContext())
                            .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

                    if (ally.isJoinedToBattlefield()) {
                        DMProgressData dmProgressData = ally.getDMProgressData();

                        ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(dmProgressData));
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
