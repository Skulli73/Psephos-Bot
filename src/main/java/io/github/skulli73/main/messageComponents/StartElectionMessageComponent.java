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
                        .append(discordApi.getRoleById(lElection.roleId).get().getMentionTag())
                        .addEmbed(lElection.electoralMethod.genericEmbed(lElection))
                        .addComponents(
                                ActionRow.of(
                                        Button.success("u" + lElection.id, "Vote"),
                                        Button.danger("e" + lElection.id, "End Election")
                                )
                        )
                        .send(lChannel)
                        .get()
                        .getId();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }



        } else
            pInteraction.createImmediateResponder().append("You require at least one candidate to start an election.").respond();
    }
}
