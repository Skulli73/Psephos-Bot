package io.github.skulli73.main.commands.StartElectionCommands;

import io.github.skulli73.main.objects.Election;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.Collections;

import static io.github.skulli73.main.MainPsephos.elections;
import static io.github.skulli73.main.MainPsephos.saveElections;

public abstract class AbstractElectionCommand {
    public AbstractElectionCommand(SlashCommandInteraction pInteraction) {
        int lId;
        if(elections.size() > 0)
            lId =Collections.max(elections.keySet())+1;
        else
            lId = 0;
        elections.put(lId, getElection(pInteraction, lId));
        saveElections();
    }
    public abstract Election getElection(SlashCommandInteraction pInteraction, int pId);
}
