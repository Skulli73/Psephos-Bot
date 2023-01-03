package io.github.skulli73.main.messageComponents;

import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.Voter;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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

        List<MessageBuilder> lMessageBuilder = lElection.getBallot();
        try{
            ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

            PrivateChannel lDmChannel = lUser.openPrivateChannel().get();
            CompletableFuture<Message> lFuture = lMessageBuilder.get(0).send(lDmChannel);
            lFuture.thenAccept(c -> {
                if (c != null) {
                    lElection.voters.add(new Voter(lUser.getId(), lElection.electoralMethod.amountOfComponents(lElection)));
                    elections.put(lElection.id, lElection);
                    int i = 0;
                    for(MessageBuilder lMessageBuilder2: lMessageBuilder) {
                        if(i!=0) {
                            lMessageBuilder2.send(lDmChannel);
                        }
                        i++;
                    }
                    saveElections();
                    pInteraction.acknowledge();
                }
            });

            ScheduledFuture<?> scheduledFuture = executor.schedule(() -> {
                if (!lFuture.isDone()) {
                    pInteraction.createImmediateResponder().append("You have your dms disabled.").setFlags(MessageFlag.EPHEMERAL).respond();
                }
            }, 2, TimeUnit.SECONDS);

            lFuture.exceptionally(t -> {
                if (t instanceof IllegalStateException) {
                    pInteraction.createImmediateResponder().append("You have your dms disabled.").setFlags(MessageFlag.EPHEMERAL).respond();
                }
                scheduledFuture.cancel(false);
                return null;
            });


        } catch(Exception e) {
            System.err.println(e);
            return;
        }
    }
}
