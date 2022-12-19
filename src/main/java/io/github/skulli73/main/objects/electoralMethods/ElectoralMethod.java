package io.github.skulli73.main.objects.electoralMethods;

import io.github.skulli73.main.objects.Ballot;

import java.util.HashMap;
import java.util.List;

public interface ElectoralMethod {

    public HashMap<String, Integer> calculateWinner(List<Ballot> pBallots, List<String> pCandidates, int pAmountSeats);
}
