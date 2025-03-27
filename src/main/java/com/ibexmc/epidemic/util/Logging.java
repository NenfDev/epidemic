package com.ibexmc.epidemic.util;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.functions.StringFunctions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logging {
    private static Epidemic plugin = Epidemic.instance();

    /**
     * debug(String, String, String)
     * Provides debug level logging.  Is not turned on by default.
     * @param className Name of the class data is being reported on
     * @param functionName Name of the function data is being reported on
     * @param message Free-text debug information
     */
    public static void debug(String className, String functionName, String message)
    {
        if (plugin != null) {
            if (plugin.config != null) {

                if (plugin.gameData().debug().get()) {
                    ConsoleCommandSender consoleCommandSender = plugin.getServer().getConsoleSender();
                    consoleCommandSender.sendMessage(StringFunctions.colorToString(ChatColor.LIGHT_PURPLE + "[" + plugin.getDisplayName() + ".Debug]" + ChatColor.AQUA + "[" + className + "." + functionName + "] " + ChatColor.WHITE + message));
                    if (plugin.config.isDebugLogToFile()) {
                        try
                        {
                            File dataFolder = plugin.getDataFolder();
                            if(!dataFolder.exists())
                            {
                                boolean res = dataFolder.mkdir();
                                if (!res) {
                                    debug("Debug", "logToFile", "Error getting data folder");
                                }
                            }

                            File saveTo = new File(plugin.getDataFolder(), plugin.getInternalName() + "_debug.log");
                            if (!saveTo.exists())
                            {
                                if (!saveTo.createNewFile()) {
                                    Logging.dev(
                                            "Logging",
                                            "debugToFile(String)",
                                            "Unexpected Error saving file: " + saveTo.getName()
                                    );
                                }
                            }

                            FileWriter fw = new FileWriter(saveTo, true);
                            PrintWriter pw = new PrintWriter(fw);
                            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                            pw.println(timeStamp + " - " + message);
                            pw.flush();
                            pw.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * dev(String, String, String)
     * Provides developer level logging.  Has a lot more information being provided
     * Only accessible if MOTD is set to com.ibexmc.testbed
     * @param className Name of the class data is being reported on
     * @param functionName Name of the function data is being reported on
     * @param message Free-text debug information
     */
    public static void dev(String className, String functionName, String message)
    {
        if (plugin.config != null) {
            if (plugin.config.isDebugLog()) {
                if (Bukkit.getMotd().equalsIgnoreCase("work.torp.testbed")) {
                    ConsoleCommandSender consoleCommandSender = plugin.getServer().getConsoleSender();
                    consoleCommandSender.sendMessage("!!! > " + ChatColor.RED + "[" + plugin.getDisplayName() + ".Dev]" + ChatColor.BLUE + "[" + className + "." + functionName + "] " + ChatColor.WHITE + message);
                }
            }
        }
    }

    /**
     * error(String, String, String, String)
     * Error reporting logging
     * @param errorCode Error code, to enable easier troubleshooting
     * @param className Name of the class error is being reported on
     * @param functionName Name of the function error is being reported on
     * @param message Free-text error information
     */

    /**
     * log(String, String)
     * Sends log messages to the console
     * @param function User friendly function name to report
     * @param message Free-text logging information
     */
    public static void log(String function, String message)
    {
        ConsoleCommandSender ccsLog = plugin.getServer().getConsoleSender();
        ccsLog.sendMessage(StringFunctions.colorToString(ChatColor.DARK_PURPLE + "[" + plugin.getDisplayName() + "]" + ChatColor.GOLD + "[" + function + "] " + ChatColor.WHITE + message));
    }
}
