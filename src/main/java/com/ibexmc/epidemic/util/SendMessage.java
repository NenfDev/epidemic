package com.ibexmc.epidemic.util;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SendMessage {
    /**
     * Sends a message to the CommandSender
     * @param messageCode Message code used for locale
     * @param message Default message
     * @param sender CommandSender to send message to
     * @param includeHeader If true, include the Epidemic prefix
     * @param parameters Map of String, String used for placeholders
     */
    public static void Sender(String messageCode, String message, CommandSender sender, boolean includeHeader, HashMap<String, String> parameters) {
        Epidemic instance = Epidemic.instance();
        String header = "";
        if (includeHeader)
        {
            header = Locale.localeText("prefix", "&2[&fEpidemic&2]&r ", null);
        }
        if (sender != null)
        {
            if (!messageCode.equalsIgnoreCase("na")) {
                if (instance.gameData().locale().has(messageCode)) {
                    String newMessage = instance.gameData().locale().get(messageCode);
                    if (!StringFunctions.isNullOrEmpty(newMessage)) {
                        message = newMessage;
                    }
                } else {
                    File localeYML = new File(Epidemic.instance().getDataFolder()+"/lang.yml");
                    FileConfiguration localeConfig = YamlConfiguration.loadConfiguration(localeYML);
                    localeConfig.set(messageCode, message);
                    try {
                        localeConfig.save(localeYML);
                    } catch (IOException e) {
                        Logging.dev("Alert", "Sender", "Error updating lang.yml: " + e.getMessage());
                    }
                }
            }
            if (message != null) {
                if (parameters != null) {
                    for (Map.Entry<String, String> params : parameters.entrySet()) {
                        message = message.replace(params.getKey(), params.getValue());
                    }
                }
                sender.sendMessage(StringFunctions.colorToString(header + ChatColor.WHITE + StringFunctions.colorToString(message)));
            }
        }
    }
    /**
     * Sends a message to the Player
     * @param messageCode Message code used for locale
     * @param message Default message
     * @param player Player to send message to
     * @param includeHeader If true, include the Epidemic prefix
     * @param parameters Map of String, String used for placeholders
     */
    public static void Player(String messageCode, String message, Player player, boolean includeHeader, HashMap<String, String> parameters) {
        Epidemic instance = Epidemic.instance();
        String header = "";
        if (includeHeader)
        {
            header = Locale.localeText("prefix", "&2[&fEpidemic&2]&r ", null);
        }
        if (player != null)
        {
            if (Bukkit.getPlayer(player.getUniqueId()) != null) {
                if (!messageCode.equalsIgnoreCase("na")) {
                    if (instance.gameData().locale().has(messageCode)) {
                        String newMessage = instance.gameData().locale().get(messageCode);
                        if (!StringFunctions.isNullOrEmpty(newMessage)) {
                            message = newMessage;
                        }
                    } else {
                        File localeYML = new File(Epidemic.instance().getDataFolder()+"/lang.yml");
                        FileConfiguration localeConfig = YamlConfiguration.loadConfiguration(localeYML);
                        localeConfig.set(messageCode, message);

                        try {
                            localeConfig.save(localeYML);
                        } catch (IOException e) {
                            Logging.dev("Alert", "Player", "Error updating lang.yml: " + e.getMessage());
                        }
                    }
                }
                if (message != null) {
                    if (parameters != null) {
                        for (Map.Entry<String, String> params : parameters.entrySet()) {
                            message = message.replace(params.getKey(), params.getValue());
                        }
                    }
                    player.sendMessage(StringFunctions.colorToString(header + ChatColor.WHITE + StringFunctions.colorToString(message)));
                }
            } else {
                Logging.debug("Alert", "Player.save()", "Invalid Player: " + player.getUniqueId() + " - Message: " + message);
            }
        } else {
            Logging.debug("Alert", "Player.save()", "Invalid Player: " + player.getUniqueId() + " - Message: " + message);
        }
    }

    /**
     * Sends a message to the players action bar
     * @param player Player to send the message to
     * @param message Message to send
     */
    public static void actionBar(Player player, String message) {
        if (player != null) {
            if (Bukkit.getPlayer(player.getUniqueId()) != null) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(StringFunctions.colorToString(message)));
            } else {
                Logging.debug("SendMessage", "actionBar(Player, String)", "Invalid Player: " + player.getUniqueId() + " - Message: " + message);
            }
        } else {
            Logging.debug("SendMessage", "actionBar(Player, String)", "Invalid Player - Message: " + message);
        }
    }

    /**
     * Sends a title message to the players screen
     * @param player Player to send the message to
     * @param title Title used for the message
     * @param subtitle Subtitle used for the message
     * @param fadeIn Number of ticks to fade in
     * @param stay Number of ticks to stay on screen
     * @param fadeOut Number of ticks to fade out
     */
    public static void title(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (player != null) {
            if (Bukkit.getPlayer(player.getUniqueId()) != null) {
                player.resetTitle();
                player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
            } else {
                Logging.debug("SendMessage", "title(Player, String, String, int, int, int)", "Invalid Player: " + player.getUniqueId() + " - Title: " + title + " Subtitle: " + subtitle);
            }
        } else {
            Logging.debug("SendMessage", "title(Player, String, String, int, int, int)", "Invalid Player - Title: " + title + " Subtitle: " + subtitle);
        }
    }
}
