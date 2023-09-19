package object.automatic.decryption.status;

import object.automatic.decryption.winner.ContestWinner;

public class ContestStatus {
    private final ContestWinner contestWinner;
    private final Boolean contestInProgress;

    public ContestStatus(ContestWinner contestWinner, Boolean contestInProgress) {
        this.contestWinner = contestWinner;
        this.contestInProgress = contestInProgress;
    }

    public ContestWinner getContestWinner() {
        return contestWinner;
    }

    public Boolean getContestInProgress() {
        return contestInProgress;
    }
}
