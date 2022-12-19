package io.github.skulli73.main.objects;

public class Vote {
    public String candidate;
    public int amountVotes;

    public Vote(String pCandidate, int pAmountVotes) {
        candidate       = pCandidate;
        amountVotes     = pAmountVotes;
    }
}
