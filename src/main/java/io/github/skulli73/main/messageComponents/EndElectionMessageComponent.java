package io.github.skulli73.main.messageComponents;

import io.github.skulli73.main.objects.Election;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static io.github.skulli73.main.MainPsephos.*;

public class EndElectionMessageComponent {
    public EndElectionMessageComponent(MessageComponentInteraction pInteraction) {
        Election lElection                  = elections.get(Integer.parseInt(pInteraction.getCustomId().substring(1)));
        HashMap<String, Integer> lResults   = lElection.electoralMethod.calculateWinner(lElection.ballots, lElection.candidates, 1);
        Object[] lCandidates = lResults.keySet().toArray();
        Arrays.sort(lCandidates, Comparator.comparingInt(lResults::get));
        Stack<Object> lStack =new Stack<>();
        for(Object lObject:lCandidates) {
            lStack.push(lObject);
        }
        int i = 0;
        for(Object ignored:lCandidates) {
            lCandidates[i] = lStack.pop();
            i++;
        }
        EmbedBuilder lEmbedBuilder = new EmbedBuilder();
        for(Object lObject:lCandidates) {
            String lCandidate   = (String) lObject;
            int lPercentage  = (int) (((double)lResults.get(lCandidate))/lElection.ballots.size()*100);
            StringBuilder lBar  = new StringBuilder();
            int lAmountBar  = lPercentage/10;
            for(int j = 0; j<10; j++) {
                if(j<lAmountBar)
                    lBar.append(":black_large_square:");
                else
                    lBar.append(":white_large_square:");
            }
            lEmbedBuilder.addField(lObject+ ": " +  lResults.get(lCandidate) + "  (" + lPercentage + "%"+ ")", lBar.toString());
        }
        lEmbedBuilder.setTitle(lElection.title + " - Results");
        lEmbedBuilder.setFooter(lElection.electoralMethod.methodName());
        TextChannel lChannel = discordApi.getChannelById(lElection.channel).get().asTextChannel().get();
        try {
            discordApi.getMessageById(lElection.message, lChannel).get().delete();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        lChannel.sendMessage(lEmbedBuilder);
        elections.remove(lElection.id);
        saveElections();
    }
}
