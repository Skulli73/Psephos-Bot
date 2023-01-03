package io.github.skulli73.main.objects.electoralMethods;

import io.github.skulli73.main.objects.Ballot;
import io.github.skulli73.main.objects.Election;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.util.HashMap;
import java.util.List;

public interface ElectoralMethod {

    public EmbedBuilder calculateWinner(List<Ballot> pBallots, List<String> pCandidates, int pAmountSeats);

    public String methodName();

    public default EmbedBuilder genericEmbed(Election pElection) {
        EmbedBuilder lEmbedBuilder = new EmbedBuilder();
        lEmbedBuilder.setTitle(pElection.title);
        StringBuilder lStringBuilder = new StringBuilder()
                .append("**Candidates:**");
        for (String lCandidate : pElection.candidates)
            lStringBuilder.append("\n").append(lCandidate);
        lEmbedBuilder.setDescription(lStringBuilder.toString());
        lEmbedBuilder.setFooter(methodName());
        return lEmbedBuilder;
    }

    public List<MessageBuilder> getBallot(Election pElection);

    public void onSelectCandidate(MessageComponentInteraction pInteraction);

    public Ballot handleBallot(MessageComponentInteraction pInteraction, Election pElection);

    public int amountOfComponents(Election pElection);

    public String getBltFile(List<Ballot> pBallots, List<String> pCandidates, String pName);
}