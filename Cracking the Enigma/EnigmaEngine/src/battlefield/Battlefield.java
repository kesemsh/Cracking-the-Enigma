package battlefield;

import machine.Machine;
import object.automatic.decryption.active.teams.details.ActiveTeamDetails;
import object.automatic.decryption.data.input.DecryptionInputData;
import object.automatic.decryption.data.pre.decryption.PreDecryptionData;
import object.automatic.decryption.difficulty.DecryptionDifficulty;
import object.automatic.decryption.message.candidate.DecryptedMessageCandidate;
import object.automatic.decryption.results.DecryptionTaskResults;
import object.automatic.decryption.status.ContestStatus;
import object.automatic.decryption.winner.ContestWinner;
import users.Ally;
import users.UBoat;
import users.User;

import java.util.ArrayList;
import java.util.List;

public class Battlefield {
    private final Machine machine;
    private final String gameTitle;
    private final int alliesCount;
    private final DecryptionDifficulty decryptionDifficulty;
    private final List<DecryptedMessageCandidate> decryptedMessageCandidateList;
    private final List<Ally> allyList;
    private UBoat uBoat;
    private boolean gameInProgress;
    private PreDecryptionData preDecryptionData;
    private ContestWinner contestWinner;
    private String fileAsString;

    public Battlefield(Machine machine, String gameTitle, int alliesCount, DecryptionDifficulty decryptionDifficulty) {
        this.machine = machine;
        this.gameTitle = gameTitle;
        this.alliesCount = alliesCount;
        this.decryptionDifficulty = decryptionDifficulty;
        decryptedMessageCandidateList = new ArrayList<>();
        allyList = new ArrayList<>();
        gameInProgress = false;
        contestWinner = null;
    }

    public void setUBoat(UBoat uBoat) {
        this.uBoat = uBoat;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public int getAlliesCount() {
        return alliesCount;
    }

    public String getFileAsString() {
        return fileAsString;
    }

    public DecryptionDifficulty getDecryptionDifficulty() {
        return decryptionDifficulty;
    }

    public String getUBoatName() {
        return uBoat.getUsername();
    }

    public int getCurrentAlliesCount() {
        return allyList.size();
    }

    public void updateGameStatus() {
        gameInProgress = uBoat.isReady() && allyList.size() == alliesCount && allyList.stream().allMatch(User::isReady);
        if (gameInProgress) {
            startGame();
        }
    }

    private void startGame() {
        allyList.forEach(ally -> ally.startDecryptionManager(new DecryptionInputData(decryptionDifficulty, ally.getAgentsCount(), ally.getTaskSize(), preDecryptionData.getMessageToDecrypt()), machine));
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public void leaveBattlefield(Ally ally) {
        allyList.remove(ally);
        ally.leaveBattlefield();
    }

    public void joinBattlefield(Ally ally) {
        allyList.add(ally);
        ally.setJoinedGameTitle(gameTitle);
        ally.addAgentsToBattlefield();
    }

    public List<ActiveTeamDetails> getActiveTeamsDetailsList() {
        List<ActiveTeamDetails> activeTeamDetailsList = new ArrayList<>();

        allyList.forEach(x -> activeTeamDetailsList.add(new ActiveTeamDetails(x.getUsername(), x.getAgentsCount(), x.getTaskSize())));

        return activeTeamDetailsList;
    }

    public List<DecryptedMessageCandidate> getDecryptedMessageCandidateList() {
        return decryptedMessageCandidateList;
    }

    public void onDecryptionResultsReceived(DecryptionTaskResults decryptionTaskResults) {
        decryptedMessageCandidateList.addAll(decryptionTaskResults.getDecryptedMessageCandidatesResultList());
        checkForWinner(decryptionTaskResults);
    }

    private void checkForWinner(DecryptionTaskResults decryptionTaskResults) {
        decryptionTaskResults.getDecryptedMessageCandidatesResultList().forEach(x -> {
            if (x.getMessage().equals(preDecryptionData.getOriginalMessageWithoutExcludedChars()) && x.getMachineConfiguration().equals(preDecryptionData.getMachineConfiguration())) {
                contestWinner = new ContestWinner(x.getAlliesName(), x.getAgentName(), x.getMachineConfiguration(), x.getMessage());
                gameInProgress = false;
            }
        });
    }

    public void setPreDecryptionData(PreDecryptionData preDecryptionData) {
        this.preDecryptionData = preDecryptionData;
    }

    public PreDecryptionData getPreDecryptionData() {
        return preDecryptionData;
    }

    public ContestStatus getContestStatus() {
        return new ContestStatus(contestWinner, gameInProgress);
    }

    public String getMessageToDecrypt() {
        return preDecryptionData.getMessageToDecrypt();
    }

    public void setFileAsString(String inputStreamString) {
        fileAsString = inputStreamString;
    }

    public void removeAlliesFromBattlefield() {
        allyList.forEach(Ally::leaveBattlefield);
        allyList.clear();
    }

    public void resetBattlefield() {
        uBoat.setReady(false);
        decryptedMessageCandidateList.clear();
        removeAlliesFromBattlefield();
        gameInProgress = false;
        contestWinner = null;
    }
}
