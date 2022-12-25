package io.github.skulli73.main.objects;

import java.util.List;

public class Voter {
    public long userId;
    public String[] componentsSelection;
    public Voter(long pUserId, int pAmountComponents) {
        userId = pUserId;
        componentsSelection = new String[pAmountComponents];
    }
}
