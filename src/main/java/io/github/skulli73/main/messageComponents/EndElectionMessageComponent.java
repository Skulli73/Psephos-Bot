package io.github.skulli73.main.messageComponents;

import io.github.skulli73.main.objects.Election;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static io.github.skulli73.main.MainPsephos.*;

public class EndElectionMessageComponent extends AbstractElectionMessageComponent{
    public EndElectionMessageComponent(MessageComponentInteraction pInteraction) {
        super(pInteraction);
    }

    @Override
    void execute(MessageComponentInteraction pInteraction) {
        Election lElection                  = elections.get(Integer.parseInt(pInteraction.getCustomId().substring(1)));

        TextChannel lChannel = discordApi.getChannelById(lElection.channel).get().asTextChannel().get();
        try {
            discordApi.getMessageById(lElection.message, lChannel).get().delete();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        MessageBuilder lMessageBuilder = new MessageBuilder();
        File lFile = new File(lElection.id + lElection.title + "election.blt");
        try {
            lFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            FileWriter lWriter = new FileWriter(lFile.getPath());
            lWriter.write(lElection.electoralMethod.getBltFile(lElection.ballots, lElection.candidates, lElection.title));
            lWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        lMessageBuilder.addAttachment(lFile);
        lMessageBuilder.send(lChannel);
        EmbedBuilder lEmbedBuilder          = lElection.electoralMethod.calculateWinner(lElection.ballots, lElection.candidates, 1);
        if(lEmbedBuilder != null) {
            lEmbedBuilder.setTitle(lElection.title + " - Results");
            lEmbedBuilder.setFooter(lElection.electoralMethod.methodName());
            lChannel.sendMessage(lEmbedBuilder);
        }
        elections.remove(lElection.id);
        saveElections();

    }
}
