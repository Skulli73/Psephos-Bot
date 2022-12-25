package io.github.skulli73.main.listeners;

import io.github.skulli73.main.messageComponents.AbortElectionMessageComponent;
import io.github.skulli73.main.messageComponents.AddCandidateMessageComponent;
import io.github.skulli73.main.messageComponents.SelectCandidateMessageComponent;
import io.github.skulli73.main.messageComponents.StartElectionMessageComponent;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.awt.event.ComponentListener;

public class MessageComponentListener implements MessageComponentCreateListener {
    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction lInteraction = event.getInteraction().asMessageComponentInteraction().get();
        System.out.println("MessageComponentInteraction received:\n CustomId: "+ lInteraction.getCustomId());
        if(lInteraction.getCustomId().charAt(0) == 'a') {
            new AddCandidateMessageComponent(lInteraction);
        }
        else if(lInteraction.getCustomId().charAt(0) == 'd') {
            new AbortElectionMessageComponent(lInteraction);
        }
        else if(lInteraction.getCustomId().charAt(0) == 's') {
            new StartElectionMessageComponent(lInteraction);
        }
        else if(lInteraction.getCustomId().charAt(0) == 'v') {
            new SelectCandidateMessageComponent(lInteraction);
        }
    }
}
