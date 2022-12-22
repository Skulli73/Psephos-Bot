package io.github.skulli73.main.commands.StartElectionCommands;

import io.github.skulli73.main.objects.Election;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
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
        Election lElection = getElection(pInteraction, lId);
        Message lMessage = sendFirstMessage(lElection, pInteraction.getChannel().get());
        lElection.message = lMessage.getId();
        lElection.channel = lMessage.getChannel().getId();
        elections.put(lId, lElection);
        saveElections();
    }
    public abstract Election getElection(SlashCommandInteraction pInteraction, int pId);

    public abstract Message sendFirstMessage(Election pElection, TextChannel pChannel);

    public EmbedBuilder genericEmbed(Election pElection) {
        return pElection.electoralMethod.genericEmbed(pElection);
    }
}
