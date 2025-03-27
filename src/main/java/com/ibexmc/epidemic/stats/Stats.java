package com.ibexmc.epidemic.stats;

public class Stats {

    //region Objects
    private int othersInfected = 0;
    private int infectedByOthers = 0;
    private int afflicted = 0;
    private int cured = 0;
    private int bloodDrawn = 0;
    //endregion
    //region Constructor
    public Stats() {

    }

    public Stats(int othersInfected, int infectedByOthers, int afflicted, int cured, int bloodDrawn) {
        this.othersInfected = othersInfected;
        this.infectedByOthers = infectedByOthers;
        this.afflicted = afflicted;
        this.cured = cured;
        this.bloodDrawn = bloodDrawn;
    }
    //endregion
    //region Getters & Setters

    /**
     * Gets the number of other players infected
     * @return Number of other player infected
     */
    public int getOthersInfected() {
        return othersInfected;
    }

    /**
     * Sets the number of other players infected
     * @param othersInfected Number of other players infected
     */
    public void setOthersInfected(int othersInfected) {
        this.othersInfected = othersInfected;
    }

    /**
     * Gets the number of times infected by others
     * @return Number of times infected by others
     */
    public int getInfectedByOthers() {
        return infectedByOthers;
    }

    /**
     * Sets the number of times infected by others
     * @param infectedByOthers Nubmer of times infected by others
     */
    public void setInfectedByOthers(int infectedByOthers) {
        this.infectedByOthers = infectedByOthers;
    }

    /**
     * Gets number of times afflicted
     * @return Number of times afflicted
     */
    public int getAfflicted() {
        return afflicted;
    }

    /**
     * Sets number of times afflicted
     * @param afflicted Number of times afflicted
     */
    public void setAfflicted(int afflicted) {
        this.afflicted = afflicted;
    }

    /**
     * Gets number of times cured
     * @return Number of times cured
     */
    public int getCured() {
        return cured;
    }

    /**
     * Sets number of times cured
     * @param cured Number of times cured
     */
    public void setCured(int cured) {
        this.cured = cured;
    }

    /**
     * Gets number of times blood has been drawn
     * @return Number of times blood has been drawn
     */
    public int getBloodDrawn() {
        return bloodDrawn;
    }

    /**
     * Sets number of times blood has been drawn
     * @param bloodDrawn Number of times blood has been drawn
     */
    public void setBloodDrawn(int bloodDrawn) {
        this.bloodDrawn = bloodDrawn;
    }
    //endregion
}
