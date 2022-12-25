package io.github.skulli73.main.objects.electoralMethods;

import io.github.skulli73.main.objects.Ballot;
import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.Vote;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class firstPastThePost implements ElectoralMethod{
    String meta = "io.github.skulli73.main.objects.electoralMethods.firstPastThePost";

    public firstPastThePost() {

    }

    @Override
    public HashMap<String, Integer> calculateWinner(List<Ballot> pBallots, List<String> pCandidates, int pAmountSeats) {
        HashMap<String, Integer> lResults = new HashMap<>();
        for(String lCandidate: pCandidates) {
            lResults.put(lCandidate, 0);
        }
        for(Ballot lBallot:pBallots) {
            for(Vote lVote: lBallot.votes) {
                if (lResults.containsKey(lVote.candidate)) {
                    lResults.put(lVote.candidate, lVote.amountVotes + lResults.get(lVote.candidate));
                } else
                    System.out.println("Non-valid Candidate: " + lVote.candidate);

            }
        }
        return lResults;
    }

    @Override
    public String methodName() {
        return "First Past the Post";
    }

    public MessageBuilder getBallot(Election pElection) {
        MessageBuilder lMessageBuilder = new MessageBuilder();
        List<SelectMenuOption> lSelectMenuOptions = new LinkedList<>();
        for(String lCandidate:pElection.candidates) {
            lSelectMenuOptions.add(SelectMenuOption.create(lCandidate, lCandidate.replaceAll(" ", "")));
        }
        lMessageBuilder.addComponents(
                ActionRow.of(
                        SelectMenu.create("v" + pElection.id, "Your Vote", lSelectMenuOptions)
                )
        );
        return lMessageBuilder;
    }

    @Override
    public void onSelectCandidate(MessageComponentInteraction pInteraction) {
        pInteraction.acknowledge();
    }
}
