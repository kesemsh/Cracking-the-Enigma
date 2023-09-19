package machine.automatic.decryption.task.results;

import machine.automatic.decryption.decrypted.message.candidate.DecryptedMessageCandidate;

import java.util.List;

public class DecryptionTaskResults {
    private final List<DecryptedMessageCandidate> decryptedMessageCandidatesResultList;
    private final long timeTaken;

    public DecryptionTaskResults(List<DecryptedMessageCandidate> decryptedMessageCandidatesResultList, long timeTaken) {
        this.decryptedMessageCandidatesResultList = decryptedMessageCandidatesResultList;
        this.timeTaken = timeTaken;
    }

    public List<DecryptedMessageCandidate> getDecryptedMessageCandidatesResultList() {
        return decryptedMessageCandidatesResultList;
    }

    public long getTimeTaken() {
        return timeTaken;
    }
}
