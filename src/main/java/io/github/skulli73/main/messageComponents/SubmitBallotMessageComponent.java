package io.github.skulli73.main.messageComponents;

import io.github.skulli73.main.objects.Ballot;
import io.github.skulli73.main.objects.Election;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.MessageComponentInteraction;

import static io.github.skulli73.main.MainPsephos.*;

public class SubmitBallotMessageComponent {
    public SubmitBallotMessageComponent(MessageComponentInteraction pInteraction) {
        int lElectionId = Integer.parseInt(pInteraction.getCustomId().substring(1));
        if(elections.containsKey(lElectionId)) {
            Election lElection = elections.get(lElectionId);
            if(lElection.ballots.stream().anyMatch(c -> c.voterId == pInteraction.getUser().getId())) {
                pInteraction.createImmediateResponder().append("You already voted").respond();
                return;
            }
             Ballot lBallot = lElection.electoralMethod.handleBallot(pInteraction, lElection);
            if(lBallot != null) {
                lElection.ballots.add(lBallot);
                Server server = discordApi.getServerById(lElection.server).get();
                Channel logChannel = server.getChannels().stream().filter(c->c.getName().contains("log")).findFirst().orElse(null);
                if(logChannel!= null) {
                    if(logChannel.asTextChannel().isPresent()) {
                        TextChannel logTextChannel = logChannel.asTextChannel().get();
                        logTextChannel.sendMessage(lBallot.votes.toString());
                    }
                }
                pInteraction.createImmediateResponder().append("Your vote was registered.").respond();
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
