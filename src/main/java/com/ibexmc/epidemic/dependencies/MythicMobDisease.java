package com.ibexmc.epidemic.dependencies;

public class MythicMobDisease {
    private String ailmentName;
    private int chance;
    private double severityModifier;
    private int cooldown;
    private boolean onHit;
    private boolean onProximity;
    private int proximityRadius;

    public MythicMobDisease(String ailmentName, int chance, double severityModifier, int cooldown, boolean onHit, boolean onProximity, int proximityRadius) {
        this.ailmentName = ailmentName;
        this.chance = chance;
        this.severityModifier = severityModifier;
        this.cooldown = cooldown;
        this.onHit = onHit;
        this.onProximity = onProximity;
        this.proximityRadius = proximityRadius;
    }

    public String getAilmentName() {
        return ailmentName;
    }

    public int getChance() {
        return chance;
    }

    public double getSeverityModifier() {
        return severityModifier;
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean isOnHit() {
        return onHit;
    }

    public boolean isOnProximity() {
        return onProximity;
    }

    public int getProximityRadius() {
        return proximityRadius;
    }
}
