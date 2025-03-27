package com.ibexmc.epidemic.dependencies.papi;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.ailments.Afflicted;
import com.ibexmc.epidemic.ailments.Ailment;
import com.ibexmc.epidemic.util.Locale;
import com.ibexmc.epidemic.temperature.Temperature;
import com.ibexmc.epidemic.thirst.Thirst;
import com.ibexmc.epidemic.util.functions.MathUtil;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PAPIValues {

    private String ailments = "";
    private Map<String, Boolean> ailmentMap = new HashMap<>();
    private double thirst = 0d;
    private String thirstText = "";
    private double temp = 0d;
    private String tempMeter = "";

    public PAPIValues(String ailments, Map<String, Boolean> ailmentMap, double thirst, String thirstText, double temp, String tempMeter) {
        this.ailments = ailments;
        this.ailmentMap = ailmentMap;
        this.thirst = thirst;
        this.thirstText = thirstText;
        this.temp = temp;
        this.tempMeter = tempMeter;
    }

    /**
     * Gets the player temperature
     * @return Temperature
     */
    public double getTemp() {
        return temp;
    }

    /**
     * Gets the player thermometer value
     * @return Thermometer
     */
    public String getTempMeter() {
        return tempMeter;
    }

    /**
     * Gets the players thirst level
     * @return Thirst amount
     */
    public double getThirst() {
        return thirst;
    }

    /**
     * Gets the players thirst text
     * @return Thrist text
     */
    public String getThirstText() {
        return thirstText;
    }

    /**
     * Gets a comma delimited list of ailments for the player
     * @return Ailments
     */
    public String getAilments() {
        return ailments;
    }

    /**
     * Gets the ailment map
     * @return Ailment map
     */
    public Map<String, Boolean> getAilmentMap() { return this.ailmentMap; }

    /**
     * Updates the PAPI values for the specified player
     * @param player Player to update values for
     */
    public static void update(Player player) {
        String ailments = "None";
        double thirstAmt = 0d;
        String thirstText = "n/a";
        double tempAmt = 0d;
        String tempMeter = "n/a";

        if (Epidemic.instance().config.isEnableThirst()) {
            Thirst thirst = Epidemic.instance().data().getThirstBar().get(player.getUniqueId());
            if (thirst != null) {
                thirstAmt = thirst.getThirstAmount();
                thirstText = thirst.getThirstText();
            }
        }
        if (Epidemic.instance().config.isEnableTemperature()) {
            Temperature temperature = new Temperature(player);
            if (temperature != null) {
                tempAmt = MathUtil.round(temperature.getTemperature(), 1);
                tempMeter = temperature.getThermometerText();
            }
        }
        Map<String, Boolean> ailmentMap = new HashMap<>();
        for (Ailment ailment : Epidemic.instance().data().getAvailableAilments()) {
            ailmentMap.put(ailment.getInternalName(), false);
        }
        List<String> ailmentsList = new ArrayList<>();
        if (Epidemic.instance().data().getPlayerAfflictionsByUUID(player.getUniqueId()).size() > 0) {
            for (Afflicted afflicted : Epidemic.instance().data().getPlayerAfflictionsByUUID(player.getUniqueId())) {
                ailmentMap.put(afflicted.getAilment().getInternalName(), true);
                boolean hasSymptoms = false;
                if (afflicted.getStartTimestamp() != null) {
                    long afflictedSeconds = (new Timestamp(System.currentTimeMillis()).getTime() - afflicted.getStartTimestamp().getTime()) / 1000;
                    hasSymptoms = (afflictedSeconds >= afflicted.getAilment().getTimeToSymptoms());
                }
                if (afflicted.getAilment().isReportBeforeSymptoms() || hasSymptoms) {
                    ailmentsList.add(afflicted.getAilment().getDisplayName());
                }
            }
        }
        if (ailmentsList.size() > 0) {
            ailments = String.join(", ", ailmentsList);
        } else {
            ailments = Locale.localeText("no_ailment", "No ailments", null);
        }

        PAPIValues papiValues = new PAPIValues(
                ailments,
                ailmentMap,
                thirstAmt,
                thirstText,
                tempAmt,
                tempMeter
        );
        Epidemic.instance().gameData().papi().put(player.getUniqueId(), papiValues);
    }
}
