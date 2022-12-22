package io.github.skulli73.main.listeners;

import io.github.skulli73.main.objects.Election;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.interaction.ModalSubmitEvent;
import org.javacord.api.interaction.ModalInteraction;
import org.javacord.api.listener.interaction.ModalSubmitListener;

import java.util.concurrent.ExecutionException;

import static io.github.skulli73.main.MainPsephos.elections;
import static io.github.skulli73.main.MainPsephos.saveElections;

public class ModalListener implements ModalSubmitListener {
    @Override
    public void onModalSubmit(ModalSubmitEvent event) {
        ModalInteraction lInteraction = event.getInteraction().asModalInteraction().get();
        if (lInteraction.getCustomId().startsWith("am")) {
            Election lElection = elections.get(Integer.parseInt(lInteraction.getCustomId().substring(2)));
            lElection.candidates.add(lInteraction.getTextInputValueByCustomId("candidate").get());
            lElection.updateMessage();
            elections.put(lElection.id, lElection);
            saveElections();

            lInteraction.createImmediateResponder().append(lInteraction.getTextInputValueByCustomId("candidate").get() + " was registered as a candidate.").respond();

        }
    }
}
