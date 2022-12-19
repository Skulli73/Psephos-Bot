package io.github.skulli73.main.listeners;

import com.google.gson.*;
import io.github.skulli73.main.commands.StartElectionCommand;
import io.github.skulli73.main.objects.CanUseBot;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import static io.github.skulli73.main.MainPsephos.path;

public class SlashCommandListener implements SlashCommandCreateListener {
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction lInteraction = event.getSlashCommandInteraction();
        CanUseBot lCanUseBot = new CanUseBot();
        String lPath = path + "botConfig\\whitelist_config.json";
        File lFile = new File(lPath);
        if(lFile.exists()) {
            StringBuilder lJsonBuilder = new StringBuilder();
            Scanner lReader = null;
            try {
                lReader = new Scanner(lFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            while (lReader.hasNextLine()) {
                String data = lReader.nextLine();
                lJsonBuilder.append(data);
            }
            lReader.close();
            System.out.println("Whitelist Config:\n" + lJsonBuilder);
            JsonObject lJsonObject = JsonParser.parseString(lJsonBuilder.toString()).getAsJsonObject();
            for(JsonElement lJsonElement: lJsonObject.get("whitelistedServers").getAsJsonArray())
                lCanUseBot.whitelistedServers.add(lJsonElement.getAsLong());
            for(JsonElement lJsonElement: lJsonObject.get("blackListedServers").getAsJsonArray())
                lCanUseBot.blackListedServers.add(lJsonElement.getAsLong());
            lCanUseBot.whiteListNecessary = lJsonObject.get("whiteListNecessary").getAsBoolean();
        } else {
            try {
                lFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Gson lGson = new GsonBuilder().setPrettyPrinting().create();
            try {
                FileWriter lWriter = new FileWriter(lPath);
                lWriter.write(lGson.toJson(lCanUseBot));
                lWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(lInteraction.getChannel().get().asTextChannel().get().asPrivateChannel().isPresent() ||lCanUseBot.serverCanUseBot(lInteraction.getServer().get())) {
            if(lInteraction.getCommandName().equals("start_election"))
                new StartElectionCommand(lInteraction);
        } else
            lInteraction.createImmediateResponder().append("This server cannot use the Psephos Bot").respond();

    }
}
