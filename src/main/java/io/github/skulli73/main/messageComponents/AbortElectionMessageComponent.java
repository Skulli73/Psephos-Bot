package io.github.skulli73.main.messageComponents;

import io.github.skulli73.main.objects.Election;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

import static io.github.skulli73.main.MainPsephos.*;

public class AbortElectionMessageComponent extends AbstractElectionMessageComponent {
    public AbortElectionMessageComponent(MessageComponentInteraction pInteraction) {
        super(pInteraction);
    }
    void execute(MessageComponentInteraction pInteraction) {
        Election lElection = elections.get(Integer.parseInt(pInteraction.getCustomId().substring(1)));
        try {
            discordApi.getMessageById(lElection.message, discordApi.getChannelById(lElection.channel).get().asServerTextChannel().get()).get().delete();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        elections.remove(lElection.id);
        saveElections();
        pInteraction.createImmediateResponder().append(lElection.title + " was cancelled").respond();
    }
}
