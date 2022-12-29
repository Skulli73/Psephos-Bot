package io.github.skulli73.main.messageComponents;

import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.Voter;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.MessageComponentInteraction;

import javax.xml.soap.Text;
import java.util.concurrent.ExecutionException;

import static io.github.skulli73.main.MainPsephos.*;

public class StartElectionMessageComponent extends AbstractElectionMessageComponent{
    public StartElectionMessageComponent(MessageComponentInteraction pInteraction) {
        super(pInteraction);
    }

    @Override
    void execute(MessageComponentInteraction pInteraction) {
        Election lElection = elections.get(Integer.parseInt(pInteraction.getCustomId().substring(1)));
        if(lElection.candidates.size()>0) {
            TextChannel lChannel = discordApi.getChannelById(lElection.channel).get().asTextChannel().get();
            try {
                discordApi.getMessageById(lElection.message, lChannel).get().delete();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            try {
                lElection.message = new MessageBuilder()
                        .addEmbed(lElection.electoralMethod.genericEmbed(lElection))
                        .addComponents(
                                ActionRow.of(
                                        Button.danger("e" + lElection.id, "End Election")
                                )
                        )
                        .send(lChannel)
                        .get()
                        .getId();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }


            Role lRole = discordApi.getRoleById(lElection.roleId).get();
            MessageBuilder lMessageBuilder = lElection.electoralMethod.getBallot(lElection);
            lMessageBuilder.addEmbed(lElection.electoralMethod.genericEmbed(lElection));
            ActionRow lActionRow = ActionRow.of(
                    Button.success("b" + lElection.id, "Submit")
            );
            lMessageBuilder.addComponents(lActionRow);
            for(User lUser: lRole.getUsers()) {
                try{
                    PrivateChannel lDmChannel = lUser.openPrivateChannel().get();
                    lMessageBuilder.send(lDmChannel);
                    lElection.voters.add(new Voter(lUser.getId(), lElection.electoralMethod.amountOfComponents(lElection)));
                    elections.put(lElection.id, lElection);
                    saveElections();
                } catch(Exception e) {
                    pInteraction.getChannel().get().sendMessage("I could not message " + lUser.getMentionTag());
                }
            }
        } else
            pInteraction.createImmediateResponder().append("You require at least one candidate to start an election.").respond();
    }
}
