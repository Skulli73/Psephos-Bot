package io.github.skulli73.main;


import com.google.gson.*;
import io.github.skulli73.main.listeners.MessageComponentListener;
import io.github.skulli73.main.listeners.ModalListener;
import io.github.skulli73.main.listeners.SlashCommandListener;
import io.github.skulli73.main.managers.SlashCommandManager;
import io.github.skulli73.main.objects.Ballot;
import io.github.skulli73.main.objects.Election;
import io.github.skulli73.main.objects.Vote;
import io.github.skulli73.main.objects.Voter;
import io.github.skulli73.main.objects.electoralMethods.ElectoralMethod;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MainPsephos {
    public static String        path = "";
    public static DiscordApi    discordApi;
    public static SlashCommandListener slashCommandListener;

    public static MessageComponentListener messageComponentListener;
    public static ModalListener modalListener;

    public static HashMap<Integer, Election> elections;

    public static Integer nextElectionId;
    public static void main(String[]args) {
        new MainPsephos();
    }
    public MainPsephos() {
        loadBot();
        loadElections();
        loadNextElection();
    }
    private void loadBot() {
        discordApi = new DiscordApiBuilder()
                .setToken(getToken())
                .setAllIntents()
                .login()
                .join();

        new SlashCommandManager(discordApi);

        slashCommandListener = new SlashCommandListener();
        messageComponentListener = new MessageComponentListener();
        modalListener = new ModalListener();

        discordApi.addSlashCommandCreateListener(slashCommandListener);
        discordApi.addMessageComponentCreateListener(messageComponentListener);
        discordApi.addModalSubmitListener(modalListener);

        System.out.println("Invite Link: " + discordApi.createBotInvite(Permissions.fromBitmask(8)));
    }
    private String getToken() {
        StringBuilder token = new StringBuilder();
        try {
            File myObj = new File(path + "TOKEN");
            if(myObj.exists()){
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    token.append(data);
                }
                myReader.close();
            } else System.err.println("Error");
        } catch (FileNotFoundException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
        return token.toString();
    }
    private void loadNextElection() {
        File lFile = new File(path + "next_election.json");
        if(lFile.exists()) {
            StringBuilder lJsonBuilder = new StringBuilder();
            Scanner lReader = null;
            try {
                lReader = new Scanner(lFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            while(lReader.hasNextLine()) {
                lJsonBuilder.append(lReader.nextLine());
            }
            lReader.close();
            nextElectionId = JsonParser.parseString(lJsonBuilder.toString()).getAsInt();
        } else {
            nextElectionId = 0;
        }
        saveNextElectionId();
    }
    private void loadElections() {


        elections = new HashMap<Integer, Election>();
        File lFile = new File(path + "elections.json");
        if(lFile.exists()) {
            try {
                StringBuilder lJsonBuilder = new StringBuilder();
                Scanner lReader = new Scanner(lFile);
                while(lReader.hasNextLine()) {
                    lJsonBuilder.append(lReader.nextLine());
                }
                lReader.close();
                for(Map.Entry<String, com.google.gson.JsonElement> lEntry : JsonParser.parseString(lJsonBuilder.toString()).getAsJsonObject().entrySet()) {
                    JsonObject lJsonObject = lEntry.getValue().getAsJsonObject();
                    ElectoralMethod lElectoralMethod = (ElectoralMethod) new Gson().fromJson(lJsonObject.get("electoralMethod").getAsJsonObject(), Class.forName(lJsonObject.get("electoralMethod").getAsJsonObject().get("meta").getAsString()));
                    Election lElection = new Election(lJsonObject.get("title").getAsString(), lJsonObject.get("id").getAsInt(), lElectoralMethod, lJsonObject.get("roleId").getAsLong());
                    for(JsonElement lJsonElement: lJsonObject.get("candidates").getAsJsonArray()) {
                        lElection.candidates.add(lJsonElement.getAsString());
                    }
                    for(JsonElement lJsonElement: lJsonObject.get("ballots").getAsJsonArray()) {
                        JsonObject lJsonObject2 = lJsonElement.getAsJsonObject();
                        List<Vote> lVoteList = new LinkedList<>();
                        for(JsonElement lJsonElement2:lJsonObject2.get("votes").getAsJsonArray()) {
                            JsonObject lJsonObject3 = lJsonElement2.getAsJsonObject();
                            lVoteList.add(new Vote(lJsonObject3.get("candidate").getAsString(), lJsonObject3.get("amountVotes").getAsInt()));
                        }
                        Ballot lBallot = new Ballot(lVoteList, lJsonObject2.get("voterId").getAsLong());
                        lElection.ballots.add(lBallot);
                    }
                    if(lJsonObject.get("message") != null)
                        lElection.message = lJsonObject.get("message").getAsLong();
                    if(lJsonObject.get("channel") != null)
                        lElection.channel = lJsonObject.get("channel").getAsLong();
                    if(lJsonObject.get("creator") != null)
                        lElection.creator = lJsonObject.get("creator").getAsLong();
                    for(JsonElement lJsonElement:lJsonObject.get("voters").getAsJsonArray()) {
                        JsonObject lJsonObject2 = lJsonElement.getAsJsonObject();
                        String[] lComponentSelection = new String[lJsonObject2.get("componentsSelection").getAsJsonArray().size()];
                        int i = 0;
                        for(JsonElement lJsonElement2: lJsonObject2.get("componentsSelection").getAsJsonArray()) {
                            if(lJsonElement2.isJsonNull())
                                lComponentSelection[i] = null;
                            else
                                lComponentSelection[i] = lJsonElement2.getAsString();
                            i++;
                        }
                        Voter lVoter = new Voter(lJsonObject2.get("userId").getAsLong(), lComponentSelection.length);
                        lVoter.componentsSelection = lComponentSelection;
                        lElection.voters.add(lVoter);
                    }
                    elections.put(Integer.valueOf(lEntry.getKey()), lElection);
                }
            } catch (FileNotFoundException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        } else {
            saveElections();
        }
    }
    public static void saveElections() {
        Gson lGson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        File lFile = new File(path + "elections.json");
        if(!lFile.exists()) {
            try {
                lFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileWriter lFileWriter = new FileWriter(lFile.getPath());
            String lJson = lGson.toJson(elections);
            lFileWriter.write(lJson);
            lFileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void saveNextElectionId() {
        Gson lGson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        File lFile = new File(path + "next_election.json");
        if(!lFile.exists()) {
            try {
                lFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileWriter lFileWriter = new FileWriter(lFile.getPath());
            String lJson = lGson.toJson(nextElectionId);
            lFileWriter.write(lJson);
            lFileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
