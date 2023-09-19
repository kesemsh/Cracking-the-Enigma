package servlets;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import machine.engine.MachineEngine;
import object.machine.state.MachineState;
import object.user.type.UserType;
import users.UBoat;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

import static connection.settings.ConnectionSettings.*;
import static connection.constants.Constants.*;

@WebServlet(name = MACHINE_DETAILS_SERVLET, value = MACHINE_DETAILS)
public class MachineDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            if (SessionUtils.isUserDesiredUserType(request, response, UserType.UBOAT, true)) {
                MachineEngine machineEngine = ((UBoat) ServletUtils.getUserManager(getServletContext())
                        .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)))).getMachineEngine();

                try {
                    MachineState machineState = machineEngine.getMachineState();

                    ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(machineState));
                } catch (Exception e) {
                    ServletUtils.setResponseErrorMessage(response, e.getMessage());
                }
            }
        }
    }
}
