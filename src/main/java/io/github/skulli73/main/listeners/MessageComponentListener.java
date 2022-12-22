package io.github.skulli73.main.listeners;

import io.github.skulli73.main.messageComponents.AddCandidateMessageComponent;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.awt.event.ComponentListener;

public class MessageComponentListener implements MessageComponentCreateListener {
    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction lInteraction = event.getInteraction().asMessageComponentInteraction().get();
        if(lInteraction.getCustomId().charAt(0) == 'a') {
            new AddCandidateMessageComponent(lInteraction);
        }
    }
}
