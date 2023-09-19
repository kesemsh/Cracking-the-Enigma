package servlets;

import battlefield.Battlefield;
import battlefield.manager.BattlefieldManager;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.automatic.decryption.message.candidate.DecryptedMessageCandidate;
import object.user.type.UserType;
import users.User;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static connection.constants.Constants.*;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = CANDIDATES_SERVLET, value = CANDIDATES)
public class CandidatesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            validateCandidatesRequest(request);
            String candidatesListSizeParameter = request.getParameter(CANDIDATES_LIST_SIZE_PARAMETER);
            int candidatesListSize = Integer.parseInt(candidatesListSizeParameter);

            if (SessionUtils.doesUserHaveSession(request, response)) {
                User user = ServletUtils.getUserManager(getServletContext())
                        .getUser(SessionUtils.getUsername(request), UserType.fromString(SessionUtils.getUserType(request)));

                if (user.isJoinedToBattlefield()) {
                    BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());
                    Battlefield battlefield = battlefieldManager.getBattlefield(user.getJoinedGameTitle());
                    List<DecryptedMessageCandidate> decryptedMessageCandidateList = battlefield.getDecryptedMessageCandidateList();
                    List<DecryptedMessageCandidate> deltaDecryptedMessageCandidateList = new ArrayList<>();

                    if (candidatesListSize < decryptedMessageCandidateList.size()) {
                        for (int i = candidatesListSize; i < decryptedMessageCandidateList.size() - candidatesListSize; i++) {
                            deltaDecryptedMessageCandidateList.add(decryptedMessageCandidateList.get(i));
                        }
                    }

                    if (user.getUserType().equals(UserType.ALLY))
                    {
                        List<DecryptedMessageCandidate> deltaDecryptedMessageCandidateListByAllies = deltaDecryptedMessageCandidateList.stream()
                                .filter(x -> x.getAlliesName().equals(user.getUsername())).collect(Collectors.toList());

                        ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(deltaDecryptedMessageCandidateListByAllies));
                    } else if (user.getUserType().equals(UserType.AGENT)) {
                        List<DecryptedMessageCandidate> deltaDecryptedMessageCandidateListByAgent = deltaDecryptedMessageCandidateList.stream()
                                .filter(x -> x.getAgentName().equals(user.getUsername())).collect(Collectors.toList());

                        ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(deltaDecryptedMessageCandidateListByAgent));
                    } else {
                        ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(deltaDecryptedMessageCandidateList));
                    }
                } else {
                    ServletUtils.setResponseErrorMessage(response, "User has not joined a battlefield!");
                }
            }
        } catch (Exception e) {
            ServletUtils.setResponseErrorMessage(response, e.getMessage());
        }
    }

    private void validateCandidatesRequest(HttpServletRequest request) throws ServletException {
        String candidatesListSizeParameter = request.getParameter(CANDIDATES_LIST_SIZE_PARAMETER);

        if (candidatesListSizeParameter == null) {
            throw new ServletException("Missing the \"" + CANDIDATES_LIST_SIZE_PARAMETER + "\" parameter!");
        } else if (Integer.parseInt(candidatesListSizeParameter) < 0) {
            throw new ServletException("Candidates list size is a negative number!");
        }
    }
}
