package servlets;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import machine.engine.MachineEngine;
import object.machine.configuration.MachineConfiguration;
import object.user.type.UserType;
import users.UBoat;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.HashMap;

import static connection.constants.Constants.*;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = SET_CONFIGURATION_SERVLET, value = SET_CONFIGURATION)
public class SetConfigurationServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            if (SessionUtils.isUserDesiredUserType(request, response, UserType.UBOAT, true)) {
                MachineEngine machineEngine = ((UBoat) ServletUtils.getUserManager(getServletContext())
                        .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)))).getMachineEngine();
                try {
                    machineEngine.resetConfiguration();
                    ServletUtils.setResponseSuccessMessage(response, "Configuration has been reset successfully!");
                } catch (Exception e) {
                    ServletUtils.setResponseErrorMessage(response, e.getMessage());
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            if (SessionUtils.isUserDesiredUserType(request, response, UserType.UBOAT, true)) {
                MachineEngine machineEngine = ((UBoat) ServletUtils.getUserManager(getServletContext())
                        .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)))).getMachineEngine();
                try {
                    validateSetConfigurationRequest(request);
                    if (request.getParameter(CONFIGURATION_MODE_PARAMETER).equalsIgnoreCase(MANUAL)) {
                        MachineConfiguration receivedConfiguration;

                        try {
                            receivedConfiguration = GSON_INSTANCE.fromJson(request.getReader(), MachineConfiguration.class);
                            if (receivedConfiguration.getPlugsToUse() == null) {
                                receivedConfiguration.setPlugsToUse(new HashMap<>());
                            }
                        } catch (Exception e) {
                            throw new JsonIOException("The data received is not in the format of the machine configuration!");
                        }

                        machineEngine.setConfiguration(receivedConfiguration);
                    } else {
                        machineEngine.setRandomConfiguration();
                    }

                    ServletUtils.setResponseSuccessMessage(response, "Configuration has been set successfully!" + System.lineSeparator() + new Gson().toJson(machineEngine.getCurrentMachineConfiguration()));
                } catch (Exception e) {
                    ServletUtils.setResponseErrorMessage(response, e.getMessage());
                }
            }
        }
    }

    private void validateSetConfigurationRequest(HttpServletRequest request) throws ServletException {
        String specifiedConfigurationMode = request.getParameter(CONFIGURATION_MODE_PARAMETER);

        if (specifiedConfigurationMode == null) {
            throw new ServletException("You must specify the configuration mode using the parameter \"" + CONFIGURATION_MODE_PARAMETER +"\"!");
        } else if (!specifiedConfigurationMode.equalsIgnoreCase(RANDOM) && !specifiedConfigurationMode.equalsIgnoreCase(MANUAL)) {
            throw new ServletException("Configuration mode specified must be RANDOM or MANUAL!");
        }
    }
}
