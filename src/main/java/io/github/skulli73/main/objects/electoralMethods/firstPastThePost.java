package io.github.skulli73.main.objects.electoralMethods;

import io.github.skulli73.main.objects.Ballot;
import io.github.skulli73.main.objects.Vote;

import java.util.HashMap;
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
}
