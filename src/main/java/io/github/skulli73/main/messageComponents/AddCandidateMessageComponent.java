package io.github.skulli73.main.messageComponents;

import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.TextInput;
import org.javacord.api.entity.message.component.TextInputStyle;
import org.javacord.api.interaction.MessageComponentInteraction;

public class AddCandidateMessageComponent {
    public AddCandidateMessageComponent(MessageComponentInteraction pInteraction) {
        pInteraction.respondWithModal("am" + pInteraction.getCustomId().substring(1), "Add Candidate" ,
                ActionRow.of(TextInput.create(TextInputStyle.SHORT, "candidate", "Name of the Candidate")));
    }
}
