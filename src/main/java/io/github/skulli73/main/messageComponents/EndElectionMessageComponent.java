package io.github.skulli73.main.messageComponents;

import io.github.skulli73.main.objects.Election;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static io.github.skulli73.main.MainPsephos.*;

public class EndElectionMessageComponent {
    public EndElectionMessageComponent(MessageComponentInteraction pInteraction) {
        Election lElection                  = elections.get(Integer.parseInt(pInteraction.getCustomId().substring(1)));
        EmbedBuilder lEmbedBuilder          = lElection.electoralMethod.calculateWinner(lElection.ballots, lElection.candidates, 1);
        lEmbedBuilder.setTitle(lElection.title + " - Results");
        lEmbedBuilder.setFooter(lElection.electoralMethod.methodName());
        TextChannel lChannel = discordApi.getChannelById(lElection.channel).get().asTextChannel().get();
        try {
            discordApi.getMessageById(lElection.message, lChannel).get().delete();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        lChannel.sendMessage(lEmbedBuilder);
        elections.remove(lElection.id);
        saveElections();
    }
}