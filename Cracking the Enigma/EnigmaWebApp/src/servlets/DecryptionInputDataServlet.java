package servlets;

import battlefield.manager.BattlefieldManager;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import machine.engine.MachineEngine;
import object.automatic.decryption.data.pre.decryption.PreDecryptionData;
import object.user.type.UserType;
import users.UBoat;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;

import static connection.settings.ConnectionSettings.*;
import static connection.constants.Constants.*;

@WebServlet(name = DECRYPTION_INPUT_DATA_SERVLET, value = DECRYPTION_INPUT_DATA)
public class DecryptionInputDataServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            if (SessionUtils.isUserDesiredUserType(request, response, UserType.UBOAT, true)) {
                UBoat uBoat = (UBoat) ServletUtils.getUserManager(getServletContext())
                        .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));
                MachineEngine machineEngine = uBoat.getMachineEngine();
                String message = request.getParameter(MESSAGE);

                if (message == null || message.isEmpty()) {
                    ServletUtils.setResponseErrorMessage(response, "No data type was given!");
                } else {
                    synchronized (getServletContext()) {
                        try {
                            PreDecryptionData preDecryptionData = machineEngine.getPreDecryptionData(message);
                            BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());

                            battlefieldManager.getBattlefield(uBoat.getJoinedGameTitle()).setPreDecryptionData(preDecryptionData);
                            ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(preDecryptionData));
                        } catch (Exception e) {
                            ServletUtils.setResponseErrorMessage(response, e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
