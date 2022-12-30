package io.github.skulli73.main.messageComponents;

import io.github.skulli73.main.objects.Ballot;
import io.github.skulli73.main.objects.Election;
import org.javacord.api.interaction.MessageComponentInteraction;

import static io.github.skulli73.main.MainPsephos.elections;
import static io.github.skulli73.main.MainPsephos.saveElections;

public class SubmitBallotMessageComponent {
    public SubmitBallotMessageComponent(MessageComponentInteraction pInteraction) {
        int lElectionId = Integer.parseInt(pInteraction.getCustomId().substring(1));
        if(elections.containsKey(lElectionId)) {
            Election lElection = elections.get(lElectionId);
            Ballot lBallot = lElection.electoralMethod.handleBallot(pInteraction, lElection);
            if(lBallot != null) {
                lElection.ballots.add(lBallot);
                pInteraction.createImmediateResponder().append("Your vote was registered.").respond();
                pInteraction.getMessage().delete();
                elections.put(lElection.id, lElection);
                saveElections();
            } else {
                pInteraction.createImmediateResponder().append("This is not a valid ballot.").respond();
            }

            lElection.updateMessage();
        } else
            pInteraction.createImmediateResponder().append("This Election is already finished.").respond();
    }
}
