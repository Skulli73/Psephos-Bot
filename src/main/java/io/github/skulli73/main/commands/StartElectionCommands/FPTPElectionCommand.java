package io.github.skulli73.main.commands.StartElectionCommands;

import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.electoralMethods.firstPastThePost;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

public class FPTPElectionCommand extends AbstractElectionCommand{
    public FPTPElectionCommand (SlashCommandInteraction pInteraction) {
        super(pInteraction);
    }

    @Override
    public Election getElection(SlashCommandInteraction pInteraction, int pId) {
        return new Election(pInteraction.getOptions().get(0).getArgumentStringValueByName("name").get(), pId, new firstPastThePost(), pInteraction.getOptions().get(0).getArgumentRoleValueByName("voting_role").get().getId());
    }

    @Override
    public Message sendFirstMessage(Election pElection, TextChannel pChannel) {
        MessageBuilder lMessageBuilder = new MessageBuilder();
        lMessageBuilder.addEmbed(genericEmbed(pElection));
        try {
            return lMessageBuilder.addComponents(
                    ActionRow.of(
                            Button.secondary("a" + pElection.id, "Add a candidate"),
                            Button.success("s" + pElection.id, "Start Election"),
                            Button.danger("d" + pElection.id, "Abort Election")
                    )
            )
                    .send(pChannel).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
