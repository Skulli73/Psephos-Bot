package io.github.skulli73.main.commands;

import io.github.skulli73.main.commands.StartElectionCommands.FPTPElectionCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
public class StartElectionCommand {
    public StartElectionCommand(SlashCommandInteraction pInteraction) {
        if(pInteraction.getOptions().get(0).getArgumentStringValueByName("electoral_method").get().equals("fptp")) {
            new FPTPElectionCommand(pInteraction);
        }
    }
}
