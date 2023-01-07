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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static io.github.skulli73.main.MainPsephos.elections;
import static io.github.skulli73.main.MainPsephos.saveElections;

public class InstantRunoffVoting extends NoCalculationRanked{

    public InstantRunoffVoting() {

    }

    @Override
    public EmbedBuilder calculateWinner(List<Ballot> pBallots, List<String> pCandidates, int pAmountSeats) {


        Collections.shuffle(pCandidates);
        EmbedBuilder lEmbedBuilder = new EmbedBuilder();
        HashMap<String, Integer> lResults = null;
        HashMap<String, Integer> lPreviousResult;
        boolean end = false;
        int i = 0;
        AtomicInteger lAmountEmptyBallots = new AtomicInteger();
        while(!end) {
            String lAddition = "";
            pBallots.removeIf(lBallot -> {
                boolean remove = lBallot.votes.isEmpty();
                if (remove) {
                    lAmountEmptyBallots.getAndIncrement();
                }
                return remove;
            });
            lPreviousResult = lResults;
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





            if(lResults.get((String)lCandidates[0]) > 0.5*pBallots.size()) {
                addCandidatesStringBuilder(pBallots, lResults, lResultRound, lCandidates, "", true);
                lResultRound.append("Blank: ").append(lAmountEmptyBallots.get()).append("\n");
                lEmbedBuilder.addField("Round " + (i+1), lResultRound.toString());
                lEmbedBuilder.addField("Result", lCandidates[0] + " has won this election, having reached more than 50% of left votes.");
                end = true;
            } else {
                String lEliminated = "";
                String lLastRanked = (String)lCandidates[lCandidates.length-1];
                int lowestVotes = lResults.get(lLastRanked);
                HashMap<String, Integer> finalLResults = lResults;
                Stream<String> lStream = pCandidates.stream().filter(c -> finalLResults.get(c) == lowestVotes);
                boolean b = false;
                if(lStream.count() == 1)
                    lEliminated = pCandidates.stream().filter(c -> finalLResults.get(c) == lowestVotes).findFirst().get();
                else if(lPreviousResult != null){
                    HashMap<String, Integer> lPreviousResultsJustTiedCandidates = new HashMap<>();
                    for(String lKey:lPreviousResult.keySet()) {
                        if(lResults.containsKey(lKey))
                            lPreviousResultsJustTiedCandidates.put(lKey, lPreviousResult.get(lKey));
                    }
                    for(String lKey:lPreviousResult.keySet()) {
                        if(lResults.containsKey(lKey)) {
                            if (lResults.get(lKey) != lowestVotes) {
                                lPreviousResultsJustTiedCandidates.remove(lKey);
                            }
                        } else
                            lPreviousResultsJustTiedCandidates.remove(lKey);
                    }

                    int lLowestLastResult = Collections.min(lPreviousResultsJustTiedCandidates.values());
                    Stream<String> lStream2 = lPreviousResultsJustTiedCandidates.keySet().stream().filter(c -> lPreviousResultsJustTiedCandidates.get(c) == lLowestLastResult );
                    if(lStream2.count() == 1) {
                        lEliminated = lPreviousResultsJustTiedCandidates.keySet().stream().filter(c -> lPreviousResultsJustTiedCandidates.get(c) == lLowestLastResult ).findFirst().get();
                        lAddition = " due to having had the least votes in the previous round of all tied candidates";
                    } else {
                        Object[] lArray = lPreviousResultsJustTiedCandidates.keySet().stream().filter(c -> lPreviousResultsJustTiedCandidates.get(c) == lLowestLastResult ).toArray();
                        Random random = new Random();
                        int index = random.nextInt(lArray.length);
                        lEliminated = (String)lArray[index];
                        lAddition = " due to being tied in having the least votes in the previous round of all tied candidates and being selected randomly";
                    }
                } else {
                    Object[] lArray = pCandidates.stream().filter(c -> finalLResults.get(c) == lowestVotes).toArray();
                    Random random = new Random();
                    int index = random.nextInt(lArray.length);
                    lEliminated = (String)lArray[index];
                    lAddition = " due to being selected randomly as this is the first round";
                }

                addCandidatesStringBuilder(pBallots, lResults, lResultRound, lCandidates, lEliminated, false);

                lResultRound.append("Blank: ").append(lAmountEmptyBallots.get()).append("\n");

                lResultRound.append(lEliminated).append(" was eliminated").append(lAddition).append(".");
                lEmbedBuilder.addField("Round " + (i+1), lResultRound.toString());
                for(Ballot lBallot:pBallots) {
                    String finalLEliminated = lEliminated;
                    lBallot.votes.removeIf(lVote -> Objects.equals(lVote.candidate, finalLEliminated));
                }
                pCandidates.remove(lEliminated);
            }


            i++;
        }
        return lEmbedBuilder;
    }

    private void addCandidatesStringBuilder(List<Ballot> pBallots, HashMap<String, Integer> lResults, StringBuilder lResultRound, Object[] lCandidates, String lEliminated, boolean b) {
        int i = 0;
        for(Object lCandidateObj: lCandidates) {
            String lCandidate = (String) lCandidateObj;
            lResultRound.append(lCandidate).append(": ").append(lResults.get(lCandidate)).append("\n");
            String lBarSymbol;
            if(Objects.equals(lCandidate, lEliminated))
                lBarSymbol = ":red_square:";
            else if(b && i == 0) {
                lBarSymbol = ":green_square:";
            }
            else {
                lBarSymbol = ":black_large_square:";
            }
            int lPercentage  = (int) (((double) lResults.get(lCandidate))/ pBallots.size()*100);
            StringBuilder lBar  = new StringBuilder();
            int lAmountBar  = lPercentage/10;
            for(int k = 0; k<10; k++) {
                if(k<lAmountBar)
                    lBar.append(lBarSymbol);
                else
                    lBar.append(":white_large_square:");
            }
            lResultRound.append(lBar).append("\n");
            i++;
        }
    }

    @Override
    public String methodName() {
        return "Instant Runoff Voting";
    }


}
