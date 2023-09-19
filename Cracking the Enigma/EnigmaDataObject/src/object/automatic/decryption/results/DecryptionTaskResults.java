package object.automatic.decryption.results;

import object.automatic.decryption.message.candidate.DecryptedMessageCandidate;

import java.util.List;

public class DecryptionTaskResults {
    private final List<DecryptedMessageCandidate> decryptedMessageCandidatesResultList;

    public DecryptionTaskResults(List<DecryptedMessageCandidate> decryptedMessageCandidatesResultList) {
        this.decryptedMessageCandidatesResultList = decryptedMessageCandidatesResultList;
    }

    public List<DecryptedMessageCandidate> getDecryptedMessageCandidatesResultList() {
        return decryptedMessageCandidatesResultList;
    }
}
