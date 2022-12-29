package io.github.skulli73.main.commands;

import io.github.skulli73.main.commands.StartElectionCommands.FPTPElectionCommand;
import io.github.skulli73.main.commands.StartElectionCommands.IRVElectionCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
public class StartElectionCommand {
    public StartElectionCommand(SlashCommandInteraction pInteraction) {
        if(pInteraction.getOptions().get(0).getArgumentStringValueByName("electoral_method").get().equals("fptp"))
            new FPTPElectionCommand(pInteraction);
        else if(pInteraction.getOptions().get(0).getArgumentStringValueByName("electoral_method").get().equals("irv"))
            new IRVElectionCommand(pInteraction);
    }
}
