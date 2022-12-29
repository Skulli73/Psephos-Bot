package io.github.skulli73.main.commands.StartElectionCommands;

import io.github.skulli73.main.objects.Election;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static io.github.skulli73.main.MainPsephos.*;

public abstract class AbstractElectionCommand {
    public AbstractElectionCommand(SlashCommandInteraction pInteraction) {
        int lId = nextElectionId;
        nextElectionId++;
        saveNextElectionId();
        Election lElection = getElection(pInteraction, lId);
        Message lMessage = sendFirstMessage(lElection, pInteraction.getChannel().get());
        lElection.message = lMessage.getId();
        lElection.channel = lMessage.getChannel().getId();
        lElection.creator = pInteraction.getUser().getId();
        elections.put(lId, lElection);
        saveElections();
    }
    public abstract Election getElection(SlashCommandInteraction pInteraction, int pId);

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

    public EmbedBuilder genericEmbed(Election pElection) {
        return pElection.electoralMethod.genericEmbed(pElection);
    }
}
