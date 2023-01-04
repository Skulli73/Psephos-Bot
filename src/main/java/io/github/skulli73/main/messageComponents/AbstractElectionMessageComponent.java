package io.github.skulli73.main.messageComponents;

import io.github.skulli73.main.objects.Election;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.concurrent.ExecutionException;

import static io.github.skulli73.main.MainPsephos.elections;

public abstract class AbstractElectionMessageComponent {
    public AbstractElectionMessageComponent (MessageComponentInteraction pInteraction) {
        Election lElection = elections.get(Integer.parseInt(pInteraction.getCustomId().substring(1)));
        if(lElection.creator == pInteraction.getUser().getId() || pInteraction.getUser().getRoles(pInteraction.getServer().get()).stream().anyMatch(c->c.getAllowedPermissions().stream().anyMatch(d->d == PermissionType.ADMINISTRATOR))) {
            execute(pInteraction);
        } else {
            pInteraction.acknowledge();
            try {
                pInteraction.getUser().openPrivateChannel().get().sendMessage("You are not the creator of this message.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    abstract void execute(MessageComponentInteraction pInteraction);
}
