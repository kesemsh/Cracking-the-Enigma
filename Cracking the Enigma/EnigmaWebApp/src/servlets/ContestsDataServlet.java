package servlets;

import battlefield.Battlefield;
import battlefield.manager.BattlefieldManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import object.automatic.decryption.data.contest.ContestData;
import object.user.type.UserType;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static connection.constants.Constants.*;
import static connection.settings.ConnectionSettings.*;

@WebServlet(name = CONTESTS_DATA_SERVLET, value = CONTESTS_DATA)
public class ContestsDataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (SessionUtils.doesUserHaveSession(request, response)) {
           try {
                validateContestsDataRequest(request);
                String battlefieldName = request.getParameter(BATTLEFIELD_NAME_PARAMETER);

                if (!SessionUtils.isUserDesiredUserType(request, response, UserType.UBOAT, false)) {
                    BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());
                    Map<String, Battlefield> gameTitleToBattlefield = battlefieldManager.getGameTitleToBattlefield();
                    List<ContestData> contestDataList = new ArrayList<>();

                    if (battlefieldName.equalsIgnoreCase(ALL_BATTLEFIELDS)) {
                        gameTitleToBattlefield.forEach((gameTitle, battlefield) -> {
                            if (battlefield.getPreDecryptionData() != null) {
                                contestDataList.add(new ContestData(battlefield.getGameTitle(), battlefield.getUBoatName(), battlefield.isGameInProgress(),
                                        battlefield.getDecryptionDifficulty(), battlefield.getAlliesCount(), battlefield.getCurrentAlliesCount(), battlefield.getPreDecryptionData().getMessageToDecrypt()));
                            } else {
                                contestDataList.add(new ContestData(battlefield.getGameTitle(), battlefield.getUBoatName(), battlefield.isGameInProgress(),
                                        battlefield.getDecryptionDifficulty(), battlefield.getAlliesCount(), battlefield.getCurrentAlliesCount(), null));
                            }
                        });
                    } else {
                        Battlefield battlefield = battlefieldManager.getBattlefield(battlefieldName);

                        if (battlefield.getPreDecryptionData() != null) {
                            contestDataList.add(new ContestData(battlefield.getGameTitle(), battlefield.getUBoatName(), battlefield.isGameInProgress(),
                                    battlefield.getDecryptionDifficulty(), battlefield.getAlliesCount(), battlefield.getCurrentAlliesCount(), battlefield.getPreDecryptionData().getMessageToDecrypt()));
                        } else {
                            contestDataList.add(new ContestData(battlefield.getGameTitle(), battlefield.getUBoatName(), battlefield.isGameInProgress(),
                                    battlefield.getDecryptionDifficulty(), battlefield.getAlliesCount(), battlefield.getCurrentAlliesCount(), null));
                        }
                    }

                    ServletUtils.setResponseSuccessMessage(response, GSON_INSTANCE.toJson(contestDataList));
                } else {
                    ServletUtils.setResponseErrorMessage(response, "User of type \"" + UserType.UBOAT + "\" cannot access this information!");
                }
            } catch (Exception e) {
               ServletUtils.setResponseErrorMessage(response, e.getMessage());
           }
        }
    }

    private void validateContestsDataRequest(HttpServletRequest request) throws ServletException {
        String battlefieldNameParameter = request.getParameter(BATTLEFIELD_NAME_PARAMETER);
        BattlefieldManager battlefieldManager = ServletUtils.getBattlefieldManager(getServletContext());

        if (battlefieldNameParameter == null) {
            throw new ServletException("You must specify the data type using the parameter \"" + BATTLEFIELD_NAME_PARAMETER +"\"!");
        } else if (!battlefieldNameParameter.equalsIgnoreCase(ALL_BATTLEFIELDS) && battlefieldManager.getBattlefield(battlefieldNameParameter) == null) {
            throw new ServletException("Data type specified must be \"" + ALL_BATTLEFIELDS + "\" or an existing battlefield name!");
        }
    }
}
