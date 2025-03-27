package com.ibexmc.epidemic.dependencies;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.Logging;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.plugin.Plugin;
import com.ibexmc.domain.Domain;

public class Dependencies {
    // Domain
    private boolean hasDomain = false;
    private Domain domain;

    /**
     * Hooks into Domain
     * @return If true, hooked in successfully
     */
    public boolean hookDomain()
    {
        Plugin plug = Epidemic.instance().getServer().getPluginManager().getPlugin("Domain");

        if (plug != null)
        {
            domain = ((Domain) plug);
            hasDomain = true;
            Logging.debug("Dependencies", "hookDomain", "Domain Found");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets the Domain instance
     * @return Domain instance
     */
    public com.ibexmc.domain.api.API getDomain()
    {
        return domain.getAPI();
    }

    /**
     * Checks if Domain is installed on the server
     * @return If true, Domain is installed
     */
    public boolean hasDomain()
    {
        return hasDomain;
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
}
