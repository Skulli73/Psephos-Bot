package io.github.skulli73.main.objects.electoralMethods;

import io.github.skulli73.main.objects.Ballot;
import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.Vote;
import io.github.skulli73.main.objects.Voter;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.util.*;

import static io.github.skulli73.main.MainPsephos.elections;
import static io.github.skulli73.main.MainPsephos.saveElections;

public class NoCalculationRanked extends AbstractElectionRankedOrApproval {
    String meta = "io.github.skulli73.main.objects.electoralMethods.NoCalculationRanked";

    public NoCalculationRanked() {

    }

    @Override
    public EmbedBuilder calculateWinner(List<Ballot> pBallots, List<String> pCandidates, int pAmountSeats) {
        return null;
    }

    @Override
    public String methodName() {
        return "Ranked Voting without Calculation";
    }

    @Override
    public List<MessageBuilder> getBallot(Election pElection) {
        List<MessageBuilder> lMessageBuilder = new LinkedList<>();
        List<SelectMenuOption> lSelectMenuOptions = new LinkedList<>();
        for(int i = 0; i<Math.ceil(((double)pElection.candidates.size())/4); i++) {
            lMessageBuilder.add(new MessageBuilder());
        }
        for(String lCandidate:pElection.candidates) {
            lSelectMenuOptions.add(SelectMenuOption.create(lCandidate, lCandidate.replaceAll(" ", "_")));
        }
        int i = 0;
        for(String ignored:pElection.candidates) {
            int lIndex = (int) Math.floor(((double)i)/4);
            lMessageBuilder.get(lIndex).addComponents(
                    ActionRow.of(
                            SelectMenu.create("v" + i + "_" + pElection.id, "Rank " + (i+1), lSelectMenuOptions)
                    )
            );
            i++;
        }

        return lMessageBuilder;
    }


}
