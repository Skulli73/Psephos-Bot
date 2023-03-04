package io.github.skulli73.main.objects.electoralMethods;

import io.github.skulli73.main.objects.Ballot;
import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.Vote;
import io.github.skulli73.main.objects.Voter;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static io.github.skulli73.main.MainPsephos.elections;
import static io.github.skulli73.main.MainPsephos.saveElections;

public abstract class AbstractElectionRankedOrApproval implements ElectoralMethod{
    @Override
    public void onSelectCandidate(MessageComponentInteraction pInteraction) {
        int lComponentId = Integer.parseInt(pInteraction.getCustomId().split("_")[0].substring(1));
        Election lElection = elections.get(Integer.parseInt(pInteraction.getCustomId().split("_")[1]));
        int i = 0;
        for(Voter lVoter: lElection.voters) {
            if(lVoter.userId == pInteraction.getUser().getId()) {
                lVoter.componentsSelection[lComponentId] = pInteraction.asSelectMenuInteraction().get().getChosenOptions().get(0).getLabel();
                lElection.voters.set(i ,lVoter);
            }
            i++;
        }
        elections.put(lElection.id, lElection);
        saveElections();
        pInteraction.acknowledge();
    }

    @Override
    public Ballot handleBallot(MessageComponentInteraction pInteraction, Election pElection) {
        List<Vote> lVotes = new LinkedList<>();
        Voter lVoter = pElection.voters.stream().filter(c->c.userId == pInteraction.getUser().getId()).findFirst().orElse(null);
        if(lVoter == null)
            return null;
        if(Arrays.stream(lVoter.componentsSelection)
                .filter(Objects::nonNull)
                .distinct()
                .count() != Arrays.stream(lVoter.componentsSelection)
                .filter(Objects::nonNull)
                .count()) {
            return null;
        }
        boolean end = false;
        for(int i = 0; i<lVoter.componentsSelection.length && !end; i++) {
            String lVote = lVoter.componentsSelection[i];
            if(lVote != null) {
                lVotes.add(new Vote(lVote, 0));
            } else
                end = true;
        }
        return new Ballot(lVotes, pInteraction.getUser().getId());
    }
    public int amountOfComponents(Election pElection) {
        return pElection.candidates.size();
    }

    public String getBltFile(List<Ballot> pBallots, List<String> pCandidates, String pName) {
        StringBuilder lStringBuilder = new StringBuilder();
        lStringBuilder.append(pCandidates.size()).append(" ").append(1).append("\n");
        for(Ballot lBallot:pBallots) {
            lStringBuilder.append("1 ");
            for(Vote lVote:lBallot.votes) {
                lStringBuilder.append(lVote.candidate).append(" ");
            }
            lStringBuilder.append("0").append("\n");
        }
        lStringBuilder.append("0").append("\n");

        String lResult = lStringBuilder.toString();
        int i = 1;
        for(String lCandidate:pCandidates) {
            lResult = lResult.replaceAll(lCandidate, String.valueOf(i));
            lResult = lResult + "\"" + lCandidate + "\"\n";
            i++;
        }

        lResult = lResult + "\"" + pName + "\"";

        return lResult;
    }
}
