package io.github.skulli73.main.objects;

import java.util.List;

public class Ballot {
    public List<Vote>   votes;
    public long         voterId;
    public Ballot(List<Vote> pVotes, long pVoterId) {
        votes = pVotes;
        voterId = pVoterId;
    }
}
