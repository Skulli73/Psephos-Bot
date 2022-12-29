package io.github.skulli73.main.objects.electoralMethods;

import io.github.skulli73.main.objects.Ballot;
import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.Vote;
import io.github.skulli73.main.objects.Voter;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.util.*;
import java.util.stream.Stream;

import static io.github.skulli73.main.MainPsephos.elections;
import static io.github.skulli73.main.MainPsephos.saveElections;

public class InstantRunoffVoting implements ElectoralMethod{
    String meta = "io.github.skulli73.main.objects.electoralMethods.InstantRunoffVoting";

    public InstantRunoffVoting() {

    }

    @Override
    public EmbedBuilder calculateWinner(List<Ballot> pBallots, List<String> pCandidates, int pAmountSeats) {
        Collections.shuffle(pCandidates);
        EmbedBuilder lEmbedBuilder = new EmbedBuilder();
        HashMap<String, Integer> lResults;
        boolean end = false;
        int i = 0;
        while(!end) {
            for(Ballot lBallot: pBallots) {
                if(lBallot.votes.isEmpty()) {
                    pBallots.remove(lBallot);
                }
            }

            lResults = new HashMap<>();
            for(String lCandidate: pCandidates) {
                lResults.put(lCandidate, 0);
            }
            for(Ballot lBallot:pBallots) {
                lResults.put(lBallot.votes.get(0).candidate, lResults.get(lBallot.votes.get(0).candidate)+1);
            }
            System.out.println(lResults);


            StringBuilder lResultRound = new StringBuilder();

            Object[] lCandidates = lResults.keySet().toArray();
            Arrays.sort(lCandidates, Comparator.comparingInt(lResults::get));
            Stack<Object> lStack =new Stack<>();
            for(Object lObject:lCandidates) {
                lStack.push(lObject);
            }
            int j = 0;
            for(Object ignored:lCandidates) {
                lCandidates[j] = lStack.pop();
                j++;
            }

            for(Object lCandidateObj: lCandidates) {
                String lCandidate = (String) lCandidateObj;
                lResultRound.append(lCandidate).append(": ").append(lResults.get(lCandidate)).append("\n");
            }



            if(lResults.get((String)lCandidates[0]) > 0.5*pBallots.size()) {
                lEmbedBuilder.addField("Round " + (i+1), lResultRound.toString());
                lEmbedBuilder.addField("Result", lCandidates[0] + " has won this election, having reached more than 50% of left votes.");
                end = true;
            } else {
                String lEliminated = (String)lCandidates[lCandidates.length-1];
                lResultRound.append(lEliminated).append(" was eliminated.");
                lEmbedBuilder.addField("Round " + (i+1), lResultRound.toString());
                for(Ballot lBallot:pBallots) {
                    lBallot.votes.removeIf(lVote -> Objects.equals(lVote.candidate, lEliminated));
                }
                pCandidates.remove(lEliminated);
            }
            i++;
        }
        return lEmbedBuilder;
    }

    @Override
    public String methodName() {
        return "Instant Runoff Voting";
    }

    @Override
    public MessageBuilder getBallot(Election pElection) {
        MessageBuilder lMessageBuilder = new MessageBuilder();
        List<SelectMenuOption> lSelectMenuOptions = new LinkedList<>();
        for(String lCandidate:pElection.candidates) {
            lSelectMenuOptions.add(SelectMenuOption.create(lCandidate, lCandidate.replaceAll(" ", "")));
        }
        int i = 0;
        for(String ignored:pElection.candidates) {
            lMessageBuilder.addComponents(
                    ActionRow.of(
                            SelectMenu.create("v" + i + "_" + pElection.id, "Rank " + (i+1), lSelectMenuOptions)
                    )
            );
            i++;
        }

        return lMessageBuilder;
    }

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
}
