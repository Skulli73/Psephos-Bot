package io.github.skulli73.main.managers;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.Arrays;

public class SlashCommandManager {
    public SlashCommandManager(DiscordApi pApi) {
        SlashCommand.with("start_election", "Start an Election", Arrays.asList(
                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "single_winner", "Elections with a single winner.", Arrays.asList(
                    SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "electoral_method", "Method used for the election", true, Arrays.asList(
                            SlashCommandOptionChoice.create("first_past_the_post", "fptp"),
                            SlashCommandOptionChoice.create("instant_runoff_voting", "irv")
                    )),
                    SlashCommandOption.create(SlashCommandOptionType.ROLE, "voting_role", "Role needed to vote in the election", true),
                    SlashCommandOption.create(SlashCommandOptionType.STRING, "name", "Name of the Election", true)
                )),
                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "no_calculation", "Elections where no results are calculated.", Arrays.asList(
                    SlashCommandOption.createWithChoices(SlashCommandOptionType.STRING, "electoral_method", "Method used for the election", true, Arrays.asList(
                            SlashCommandOptionChoice.create("ranked_choice", "rc")
                        )),
                        SlashCommandOption.create(SlashCommandOptionType.ROLE, "voting_role", "Role needed to vote in the election", true),
                        SlashCommandOption.create(SlashCommandOptionType.STRING, "name", "Name of the Election", true)
                    )
                )

        ))
                .setDefaultEnabledForPermissions(PermissionType.MANAGE_CHANNELS, PermissionType.ADMINISTRATOR)
                .setEnabledInDms(false)
                .createGlobal(pApi)
                .join();
    }
}
