package com.ibexmc.epidemic.ailments;

import com.ibexmc.epidemic.Epidemic;
import com.ibexmc.epidemic.util.ConfigParser;
import org.bukkit.Location;

import java.io.File;

public class Hotspot {

    public Hotspot(Epidemic instance, File file, Location location, String key) {
        ConfigParser configParser = new ConfigParser(Epidemic.instance(), file);
        this.location = location;
        ConfigParser.ConfigReturn crAilment = configParser.getStringValue("hotspots." + key + ".ailment","",true);
        if (crAilment.isSuccess()) {
            if (AilmentManager.getAilmentByInternalName(crAilment.getString()) != null) {
                this.ailment = crAilment.getString();
                ConfigParser.ConfigReturn crChance = configParser.getIntValue("hotspots." + key + ".chance",0,true);
                if (crChance.isSuccess()) {
                    this.chance = crChance.getInt();
                } else {
                    this.chance = 0;
                }
                ConfigParser.ConfigReturn crDistance = configParser.getIntValue("hotspots." + key + ".distance",0,true);
                if (crDistance.isSuccess()) {
                    this.distance = crDistance.getInt();
                } else {
                    this.distance = 10;
                }
                ConfigParser.ConfigReturn crShow = configParser.getBooleanValue("hotspots." + key + ".particles.show",false,false);
                if (crShow.isSuccess()) {
                    this.showParticles = crShow.getBoolean();
                } else {
                    this.showParticles = false;
                }
                ConfigParser.ConfigReturn crRed = configParser.getIntValue("hotspots." + key + ".particles.red",0,false);
                if (crRed.isSuccess()) {
                    this.red = crRed.getInt();
                } else {
                    this.red = -1;
                }
                ConfigParser.ConfigReturn crGreen = configParser.getIntValue("hotspots." + key + ".particles.green",0,false);
                if (crGreen.isSuccess()) {
                    this.green = crGreen.getInt();
                } else {
                    this.green = -1;
                }
                ConfigParser.ConfigReturn crBlue = configParser.getIntValue("hotspots." + key + ".particles.blue",0,false);
                if (crBlue.isSuccess()) {
                    this.blue = crBlue.getInt();
                } else {
                    this.blue = -1;
                }
                ConfigParser.ConfigReturn crSize = configParser.getIntValue("hotspots." + key + ".particles.size",0,false);
                if (crSize.isSuccess()) {
                    this.size = crSize.getInt();
                } else {
                    this.size = -1;
                }
                ConfigParser.ConfigReturn crCount = configParser.getIntValue("hotspots." + key + ".particles.count",0,false);
                if (crCount.isSuccess()) {
                    this.count = crCount.getInt();
                } else {
                    this.count = -1;
                }
                this.showParticles = (this.showParticles && this.red > 0 && this.green > 0 && this.blue > 0 && this.size > 0 && this.count > 0);
            }
        }
    }

    private final Location location;
    private String ailment;
    private int chance = 0;
    private int distance = 10;
    private boolean showParticles = false;
    private int red = -1;
    private int green = -1;
    private int blue = -1;
    private int size = -1;
    private int count = -1;

    public Location getLocation() {
        return location;
    }

    public String getAilment() {
        return ailment;
    }

    public int getChance() {
        return chance;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isShowParticles() {
        return showParticles;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    public int getSize() {
        return size;
    }

    public int getCount() {
        return count;
    }
}
