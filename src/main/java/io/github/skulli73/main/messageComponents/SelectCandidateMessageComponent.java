package io.github.skulli73.main.messageComponents;

import io.github.skulli73.main.objects.Election;
import org.javacord.api.interaction.MessageComponentInteraction;

import static io.github.skulli73.main.MainPsephos.elections;

public class SelectCandidateMessageComponent {
    public SelectCandidateMessageComponent (MessageComponentInteraction pInteraction) {
        int lElectionId = Integer.parseInt(pInteraction.getCustomId().substring(1));
        if(elections.containsKey(lElectionId)) {
            Election lElection = elections.get(lElectionId);
            lElection.electoralMethod.onSelectCandidate(pInteraction);
        } else
            pInteraction.createImmediateResponder().append("This Election is already finished.").respond();
    }
}
