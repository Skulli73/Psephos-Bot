package io.github.skulli73.main.objects.electoralMethods;

import io.github.skulli73.main.objects.Ballot;
import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.Vote;
import io.github.skulli73.main.objects.Voter;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.util.*;

import static io.github.skulli73.main.MainPsephos.elections;
import static io.github.skulli73.main.MainPsephos.saveElections;

public class FirstPastThePost implements ElectoralMethod{
    String meta = "io.github.skulli73.main.objects.electoralMethods.FirstPastThePost";

    public FirstPastThePost() {

    }

    @Override
    public EmbedBuilder calculateWinner(List<Ballot> pBallots, List<String> pCandidates, int pAmountSeats) {
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

        Object[] lCandidates = lResults.keySet().toArray();
        Arrays.sort(lCandidates, Comparator.comparingInt(lResults::get));
        Stack<Object> lStack =new Stack<>();
        for(Object lObject:lCandidates) {
            lStack.push(lObject);
        }
        int i = 0;
        for(Object ignored:lCandidates) {
            lCandidates[i] = lStack.pop();
            i++;
        }
        EmbedBuilder lEmbedBuilder = new EmbedBuilder();
        for(Object lObject:lCandidates) {
            String lCandidate   = (String) lObject;
            int lPercentage  = (int) (((double)lResults.get(lCandidate))/pBallots.size()*100);
            StringBuilder lBar  = new StringBuilder();
            int lAmountBar  = lPercentage/10;
            for(int j = 0; j<10; j++) {
                if(j<lAmountBar)
                    lBar.append(":black_large_square:");
                else
                    lBar.append(":white_large_square:");
            }
            lEmbedBuilder.addField(lObject+ ": " +  lResults.get(lCandidate) + "  (" + lPercentage + "%"+ ")", lBar.toString());
        }
        return lEmbedBuilder;
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
                        SelectMenu.create("v_" + pElection.id, "Your Vote", lSelectMenuOptions)
                )
        );
        return lMessageBuilder;
    }

    @Override
    public void onSelectCandidate(MessageComponentInteraction pInteraction) {
        pInteraction.acknowledge();
        Election lElection = elections.get(Integer.parseInt(pInteraction.getCustomId().substring(2)));
        int i = 0;
        for(Voter lVoter: lElection.voters) {
            if(lVoter.userId == pInteraction.getUser().getId()) {
                lVoter.componentsSelection[0] = pInteraction.asSelectMenuInteraction().get().getChosenOptions().get(0).getLabel();
                lElection.voters.set(i ,lVoter);
            }
            i++;
        }
        elections.put(lElection.id, lElection);
        saveElections();
    }

    @Override
    public Ballot handleBallot(MessageComponentInteraction pInteraction, Election pElection) {
        List<Vote> lVotes = new LinkedList<>();
        Voter lVoter = pElection.voters.stream().filter(c->c.userId == pInteraction.getUser().getId()).findFirst().orElse(null);
        if(lVoter == null)
            return null;
        if(lVoter.componentsSelection[0] == null) {
            return null;
        }
        lVotes.add(new Vote(lVoter.componentsSelection[0], 1));
        return new Ballot(lVotes, pInteraction.getUser().getId());
    }
    public int amountOfComponents(Election pElection) {
        return 1;
    }
}
