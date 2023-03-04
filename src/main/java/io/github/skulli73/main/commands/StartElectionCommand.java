package io.github.skulli73.main.commands;

import io.github.skulli73.main.commands.StartElectionCommands.ApprovalElectionCommand;
import io.github.skulli73.main.commands.StartElectionCommands.FPTPElectionCommand;
import io.github.skulli73.main.commands.StartElectionCommands.IRVElectionCommand;
import io.github.skulli73.main.commands.StartElectionCommands.RCElectionCommand;
import io.github.skulli73.main.objects.electoralMethods.InstantRunoffVoting;
import org.javacord.api.interaction.SlashCommandInteraction;
public class StartElectionCommand {
    public StartElectionCommand(SlashCommandInteraction pInteraction) {
        switch (pInteraction.getOptions().get(0).getArgumentStringValueByName("electoral_method").get()) {
            case "fptp": new FPTPElectionCommand(pInteraction);break;
            case "irv": new IRVElectionCommand(pInteraction);break;
            case "rc": new RCElectionCommand(pInteraction);break;
            case "approval": new ApprovalElectionCommand(pInteraction);break;
            default: break;
        }
    }
}
