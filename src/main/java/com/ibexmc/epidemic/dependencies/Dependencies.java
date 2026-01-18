package com.ibexmc.epidemic.dependencies;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Logging;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class Dependencies {
    // Domain (optional)
    private boolean hasDomain = false;
    private Plugin domainPlugin;
    private Object domainApi;

    /**
     * Hooks into Domain using reflection (optional dependency)
     * @return If true, hooked in successfully
     */
    public boolean hookDomain()
    {
        Plugin plug = Epidemic.instance().getServer().getPluginManager().getPlugin("Domain");

        if (plug != null)
        {
            try {
                Method getAPI = plug.getClass().getMethod("getAPI");
                Object api = getAPI.invoke(plug);
                this.domainPlugin = plug;
                this.domainApi = api;
                hasDomain = (api != null);
                Logging.debug("Dependencies", "hookDomain", "Domain Found");
                return hasDomain;
            } catch (Exception ex) {
                Logging.debug("Dependencies", "hookDomain", "Domain present but API not accessible: " + ex.getMessage());
                hasDomain = false;
                domainPlugin = null;
                domainApi = null;
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Checks if Domain is installed on the server
     * @return If true, Domain is installed
     */
    public boolean hasDomain()
    {
        return hasDomain;
    }

    /**
     * Reflects Domain API: flagFromName
     */
    public Object flagFromName(String name) {
        if (!hasDomain || domainApi == null) return null;
        try {
            Method m = domainApi.getClass().getMethod("flagFromName", String.class);
            return m.invoke(domainApi, name);
        } catch (Exception ex) {
            Logging.debug("Dependencies", "flagFromName", "Error: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Reflects Domain API: flagAtLocation
     */
    public boolean flagAtLocation(Object flag, Location location) {
        if (!hasDomain || domainApi == null || flag == null || location == null) return false;
        try {
            Method m = domainApi.getClass().getMethod("flagAtLocation", flag.getClass(), Location.class);
            Object res = m.invoke(domainApi, flag, location);
            return (res instanceof Boolean) && ((Boolean) res);
        } catch (Exception ex) {
            Logging.debug("Dependencies", "flagAtLocation", "Error: " + ex.getMessage());
            return false;
        }
    }

    // WorldGuard
    private boolean hasWorldGuard = false;
    private WorldGuardPlugin worldGuard;

    /**
     * Hooks into WorldGuard
     * @return If true, hooked successfully
     */
    public boolean hookWorldGuard() {
        Plugin plug = Epidemic.instance().getServer().getPluginManager().getPlugin("WorldGuard");
        if (plug != null) {
            worldGuard = ((WorldGuardPlugin) plug);
            hasWorldGuard = true;
            return true;
        }
        return false;
    }

    /**
     * Gets the WorldGuard plugin
     * @return WorldGuard Plugin
     */
    public WorldGuardPlugin getWorldGuardPlugin() {
        return worldGuard;
    }

    /**
     * Gets the WorldGuard instance
     * @return WorldGuard instance
     */
    public WorldGuard getWorldGuard() {
        return WorldGuard.getInstance();
    }

    /**
     * Checks if WorldGuard is installed on the server
     * @return if true, WorldGuard is installed
     */
    public boolean hasWorldGuard() {
        return hasWorldGuard;
    }

    // ItemsAdder
    private boolean hasItemsAdder = false;

    public boolean hookItemsAdder() {
        Plugin plug = Epidemic.instance().getServer().getPluginManager().getPlugin("ItemsAdder");
        if (plug != null && plug.isEnabled()) {
            hasItemsAdder = true;
            return true;
        }
        return false;
    }

    public boolean hasItemsAdder() {
        return hasItemsAdder;
    }

    // MythicMobs
    private boolean hasMythicMobs = false;

    public boolean hookMythicMobs() {
        Plugin plug = Epidemic.instance().getServer().getPluginManager().getPlugin("MythicMobs");
        if (plug != null && plug.isEnabled()) {
            hasMythicMobs = true;
            return true;
        }
        return false;
    }

    public boolean hasMythicMobs() {
        return hasMythicMobs;
    }
}
