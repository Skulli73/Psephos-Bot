package io.github.skulli73.main.objects;

import io.github.skulli73.main.objects.electoralMethods.ElectoralMethod;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.github.skulli73.main.MainPsephos.discordApi;

public class Election {
    public String           title;
    public int              id;
    public List<String>     candidates;
    public long             roleId;

    public ElectoralMethod  electoralMethod;
    public List<Ballot>     ballots;
    public long             message;
    public long             channel;
    public long             creator;
    public List<Voter>      voters;

    public Election(String pTitle, int pId, ElectoralMethod pElectoralMethod, long pRoleId) {
        id              = pId;
        title           = pTitle;
        candidates      = new LinkedList<>();
        ballots         = new LinkedList<>();
        electoralMethod = pElectoralMethod;
        roleId          = pRoleId;
        voters          = new LinkedList<>();
    }

    public void updateMessage() {
        try {
            Message lMessage = discordApi.getMessageById(message, discordApi.getChannelById(channel).get().asTextChannel().get()).get();
            lMessage.edit(electoralMethod.genericEmbed(this).setFooter(electoralMethod.methodName()+"\n" + ballots.size() + " people have voted."));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MessageBuilder> getBallot() {
        List<MessageBuilder> lMessageBuilder = electoralMethod.getBallot(this);
        lMessageBuilder.get(0).addEmbed(electoralMethod.genericEmbed(this));
        ActionRow lActionRow = ActionRow.of(
                Button.success("b" + id, "Submit")
        );
        lMessageBuilder.get(lMessageBuilder.size()-1).addComponents(lActionRow);
        return lMessageBuilder;
    }
}
