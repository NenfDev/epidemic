package com.ibexmc.epidemic.dependencies.papi;

import com.ibexmc.epidemic.Epidemic;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PAPISupport extends PlaceholderExpansion {
    private Epidemic plugin;

    public PAPISupport(Epidemic plugin){
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return "epidemic";
    }

    @Override
    public String getAuthor() {
        return "torpkev";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }


    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        PAPIValues papiValues = Epidemic.instance().gameData().papi().get(player.getUniqueId());
        if (papiValues == null) {
            return "";
        }

        if (identifier.equals("ailments")) {
            return papiValues.getAilments();
        }
        if (identifier.contains("has_")) {
            String ailmentKey = identifier.replace("has_", "");
            if (papiValues.getAilmentMap().containsKey(ailmentKey)) {
                return papiValues.getAilmentMap().get(ailmentKey).toString();
            } else {
                return "Unknown";
            }
        }
        if (identifier.equals("thirst")) {
            return Double.toString(papiValues.getThirst());
        }
        if (identifier.equals("thirst_text")) {
            return papiValues.getThirstText();
        }
        if (identifier.equals("temp")) {
            return Double.toString(papiValues.getTemp());
        }
        if (identifier.equals("temp_meter")) {
            return papiValues.getTempMeter();
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
    }
}
