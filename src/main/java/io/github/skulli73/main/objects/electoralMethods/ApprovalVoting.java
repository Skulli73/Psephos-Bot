package io.github.skulli73.main.objects.electoralMethods;

import io.github.skulli73.main.objects.Ballot;
import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.Vote;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.*;

public class ApprovalVoting extends AbstractElectionRankedOrApproval{
    String meta = "io.github.skulli73.main.objects.electoralMethods.ApprovalVoting";
    public ApprovalVoting() {

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
                    lResults.put(lVote.candidate, 1 + lResults.get(lVote.candidate));
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
        return "Approval Voting";
    }
    public List<MessageBuilder> getBallot(Election pElection) {
        List<MessageBuilder> lMessageBuilder = new LinkedList<>();
        List<SelectMenuOption> lSelectMenuOptions = new LinkedList<>();
        for(int i = 0; i<Math.ceil(((double)pElection.candidates.size())/4); i++) {
            lMessageBuilder.add(new MessageBuilder());
        }
        for(String lCandidate:pElection.candidates) {
            lSelectMenuOptions.add(SelectMenuOption.create(lCandidate, lCandidate.replaceAll(" ", "_")));
        }
        int i = 0;
        for(String ignored:pElection.candidates) {
            int lIndex = (int) Math.floor(((double)i)/4);
            lMessageBuilder.get(lIndex).addComponents(
                    ActionRow.of(
                            SelectMenu.create("v" + i + "_" + pElection.id, "Candidate you approve of (Does not have to be filled)", lSelectMenuOptions)
                    )
            );
            i++;
        }

        return lMessageBuilder;
    }
}
