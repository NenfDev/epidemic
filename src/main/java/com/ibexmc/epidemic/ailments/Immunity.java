package com.ibexmc.epidemic.ailments;

public class Immunity {

    private Ailment ailment;
    private int amount;

    public Immunity(Ailment ailment, int amount) {
        this.ailment = ailment;
        this.amount = amount;
    }

    /**
     * Gets the ailment assigned to this Immunity
     * @return Ailment assigned
     */
    public Ailment getAilment() {
        return ailment;
    }

    /**
     * Sets the ailment assigned to this Immunity
     * @param ailment Ailment to assign
     */
    public void setAilment(Ailment ailment) {
        this.ailment = ailment;
    }

    /**
     * Gets the amount of immunity for the ailment
     * @return Immunity amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of immunity for the ailment
     * @param amount Amount to apply
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

}
