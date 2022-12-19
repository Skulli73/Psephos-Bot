package io.github.skulli73.main.objects;

import io.github.skulli73.main.objects.electoralMethods.ElectoralMethod;

import java.util.LinkedList;
import java.util.List;

public class Election {
    public String           title;
    public int              id;
    public List<String>     candidates;
    public long             roleId;

    public ElectoralMethod  electoralMethod;
    public List<Ballot>     ballots;

    public Election(String pTitle, int pId, ElectoralMethod pElectoralMethod, long pRoleId) {
        id              = pId;
        title           = pTitle;
        candidates      = new LinkedList<>();
        ballots         = new LinkedList<>();
        electoralMethod = pElectoralMethod;
        roleId          = pRoleId;
    }
}
