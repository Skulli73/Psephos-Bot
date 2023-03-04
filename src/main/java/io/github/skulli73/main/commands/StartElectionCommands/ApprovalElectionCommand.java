package io.github.skulli73.main.commands.StartElectionCommands;

import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.electoralMethods.ApprovalVoting;
import io.github.skulli73.main.objects.electoralMethods.InstantRunoffVoting;
import io.github.skulli73.main.objects.electoralMethods.NoCalculationRanked;
import org.javacord.api.interaction.SlashCommandInteraction;

public class ApprovalElectionCommand extends AbstractElectionCommand{
    public ApprovalElectionCommand(SlashCommandInteraction pInteraction) {
        super(pInteraction);
    }

    @Override
    public Election getElection(SlashCommandInteraction pInteraction, int pId) {
        return new Election(pInteraction.getOptions().get(0).getArgumentStringValueByName("name").get(), pId, new ApprovalVoting(), pInteraction.getOptions().get(0).getArgumentRoleValueByName("voting_role").get().getId());
    }
}
