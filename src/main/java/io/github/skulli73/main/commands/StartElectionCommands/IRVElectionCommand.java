package io.github.skulli73.main.commands.StartElectionCommands;

import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.electoralMethods.FirstPastThePost;
import io.github.skulli73.main.objects.electoralMethods.InstantRunoffVoting;
import org.javacord.api.interaction.SlashCommandInteraction;

public class IRVElectionCommand extends AbstractElectionCommand{
    public IRVElectionCommand(SlashCommandInteraction pInteraction) {
        super(pInteraction);
    }

    @Override
    public Election getElection(SlashCommandInteraction pInteraction, int pId) {
        return new Election(pInteraction.getOptions().get(0).getArgumentStringValueByName("name").get(), pId, new InstantRunoffVoting(), pInteraction.getOptions().get(0).getArgumentRoleValueByName("voting_role").get().getId());
    }


}
