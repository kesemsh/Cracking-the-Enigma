package servlets;

import battlefield.Battlefield;
import battlefield.manager.BattlefieldManager;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import machine.engine.MachineEngine;
import object.user.type.UserType;
import users.UBoat;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.ServerException;
import java.util.Scanner;

import static connection.constants.Constants.*;
import static connection.settings.ConnectionSettings.*;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
@WebServlet(name = FILE_MANAGER_SERVLET, value = FILE_MANAGER)
public class FileManagerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
            if (SessionUtils.isUserDesiredUserType(request, response, UserType.UBOAT, true)) {
                UBoat uBoat = (UBoat) ServletUtils.getUserManager(getServletContext())
                        .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));
                MachineEngine machineEngine = uBoat.getMachineEngine();
                BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());

                try {
                    validateRequest(request);
                    Part receivedFilePart = request.getPart(FILE_PARAMETER);

                    if (isXMLFile(receivedFilePart)) {
                        Battlefield battlefield;

                        synchronized (getServletContext()) {
                            battlefield = machineEngine.loadMachineFromXMLFile(receivedFilePart.getInputStream(), battlefieldManager::doesGameTitleAlreadyExist);
                            battlefieldManager.addBattlefield(battlefield);
                        }

                        battlefield.setUBoat(uBoat);
                        battlefield.setFileAsString(readFromInputStream(receivedFilePart.getInputStream()));
                        uBoat.setJoinedGameTitle(battlefield.getGameTitle());
                    } else {
                        throw new ServletException("Received file is not of type XML!");
                    }

                    ServletUtils.setResponseSuccessMessage(response, "Machine file loaded successfully!");
                } catch (Exception e) {
                    ServletUtils.setResponseErrorMessage(response, e.getMessage());
                }
            }
        }
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        inputStream.reset();

        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }

    private String getFileName(final Part part) throws ServletException {
        final String partHeader = part.getHeader("content-disposition");

        for (String content : partHeader.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }

        throw new ServletException("Did not receive a file name!");
    }

    private void validateRequest(HttpServletRequest request) throws ServletException, IOException {
        Part receivedFilePart = request.getPart(FILE_PARAMETER);

        if (receivedFilePart == null) {
            throw new ServletException("Could not find a file specified with the " + FILE_PARAMETER + " key!");
        }

        boolean isReceivedFileXMLFile = isXMLFile(receivedFilePart);

        if (!isReceivedFileXMLFile) {
            throw new ServletException("Received file type is not supported! Please upload XML files only!");
        }
    }

    private boolean isXMLFile(Part receivedFilePart) throws ServletException {
        String receivedFileName = getFileName(receivedFilePart);

        return receivedFileName.endsWith("." + XML);
    }
}
