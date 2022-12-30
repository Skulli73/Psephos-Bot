package io.github.skulli73.main.messageComponents;

import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.Voter;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.MessageComponentInteraction;

import static io.github.skulli73.main.MainPsephos.*;

public class VoteMessageComponent {
    public VoteMessageComponent(MessageComponentInteraction pInteraction) {
        int lElectionId = Integer.parseInt(pInteraction.getCustomId().substring(1));
        User lUser = pInteraction.getUser();

        if(!elections.containsKey(lElectionId)) {
            return;
        }
        Election lElection = elections.get(lElectionId);

        Role lRole = discordApi.getRoleById(lElection.roleId).get();

        if(!lRole.hasUser(pInteraction.getUser())) {
            return;
        }

        if(lElection.voters.stream().filter(c->c.userId==lUser.getId()).count() > 0) {
            return;
        }

        MessageBuilder lMessageBuilder = lElection.getBallot();
        try{
            PrivateChannel lDmChannel = lUser.openPrivateChannel().get();
            lMessageBuilder.send(lDmChannel);
            lElection.voters.add(new Voter(lUser.getId(), lElection.electoralMethod.amountOfComponents(lElection)));
            elections.put(lElection.id, lElection);
            saveElections();
        } catch(Exception e) {
            return;
        }

        pInteraction.acknowledge();
    }
}
