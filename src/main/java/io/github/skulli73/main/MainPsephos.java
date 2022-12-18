package io.github.skulli73.main;


import io.github.skulli73.main.listeners.SlashCommandListener;
import io.github.skulli73.main.managers.SlashCommandManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.Permissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class MainPsephos {
    public static String        path = System.getProperty("user.dir") + "\\src\\main\\java\\io\\github\\skulli73\\main\\";
    public static DiscordApi    discordApi;
    public static SlashCommandListener slashCommandListener;
    public static void main(String[]args) {
        new MainPsephos();
    }
    public MainPsephos() {
        loadBot();
    }
    private void loadBot() {
        discordApi = new DiscordApiBuilder()
                .setToken(getToken())
                .setAllIntents()
                .login()
                .join();

        new SlashCommandManager(discordApi);

        slashCommandListener = new SlashCommandListener();

        discordApi.addSlashCommandCreateListener(slashCommandListener);

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
}
