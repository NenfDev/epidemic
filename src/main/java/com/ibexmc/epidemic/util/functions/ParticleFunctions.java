package com.ibexmc.epidemic.util.functions;

import com.ibexmc.epidemic.Epidemic;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ParticleFunctions {

    /**
     * Gets the left side of the location
     * @param location Location to check
     * @param distance Distance
     * @return Left location
     */
    public static Location getLeftSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    /**
     * Gets the right side of the location
     * @param location Location to check
     * @param distance Distance
     * @return Right location
     */
    public static Location getRightSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.cos(angle), 0, Math.sin(angle)).normalize().multiply(distance));
    }

    /**
     * Gets the below the location
     * @param location Location to check
     * @param distance Distance
     * @return Below location
     */
    public static Location getBelow(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(0, Math.cos(angle), Math.sin(angle)).normalize().multiply(distance));
    }

    /**
     * Gets the above the location
     * @param location Location to check
     * @param distance Distance
     * @return Above location
     */
    public static Location getAbove(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(0, Math.cos(angle), Math.sin(angle)).normalize().multiply(distance));
    }

    /**
     * Gets the back side of the location
     * @param location Location to check
     * @param distance Distance
     * @return Back location
     */
    public static Location getBackSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().subtract(new Vector(Math.sin(angle), 0, Math.cos(angle)).normalize().multiply(distance));
    }

    /**
     * Gets the front side of the location
     * @param location Location to check
     * @param distance Distance
     * @return Front location
     */
    public static Location getFrontSide(Location location, double distance) {
        float angle = location.getYaw() / 60;
        return location.clone().add(new Vector(Math.sin(angle), 0, Math.cos(angle)).normalize().multiply(distance));
    }

    /**
     * Displays particle dust at the specified location
     * @param particleLocation Location to display particle dust
     * @param count Amount of particle dust
     * @param r Red
     * @param g Green
     * @param b Blue
     * @param size Particle size
     */
    public static void displayParticleDust(Location particleLocation, int count, int r, int g, int b, int size) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(r, g, b), size);
        particleLocation.getWorld().spawnParticle(Particle.REDSTONE, particleLocation, count, dustOptions);
    }

    /**
     * Displays particle dust for the player at the specified location
     * @param player Player to display for
     * @param particleLocation Location to display particle dust
     * @param count Amount of particle dust
     * @param r Red
     * @param g Green
     * @param b Blue
     * @param size Particle Size
     */
    public static void displayParticleDustPlayer(Player player, Location particleLocation, int count, int r, int g, int b, int size) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(r, g, b), size);

        player.spawnParticle(Particle.REDSTONE, particleLocation, count, dustOptions);
    }

    /**
     * Displays particle dust between 2 points for the player
     * @param player Player to display for
     * @param location1 Location 1
     * @param location2 Location 2
     * @param count Amount of particle dust
     * @param r Red
     * @param g Green
     * @param b Blue
     * @param size Particle Size
     */
    public static void displayParticleDustPlayerBetween2Points(Player player, Location location1, Location location2, int count, int r, int g, int b, int size) {
        Location particleLocation1 = location1.clone();
        Location particleLocation2 = location2.clone();
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(r, g, b), size);
        Vector vector = particleLocation1.toVector().subtract(particleLocation2.toVector());

        new BukkitRunnable() {
            private int step = 0;
            public void run() {
                step++;
                for (double i = 0; i <= particleLocation1.distance(particleLocation2); i += 1.0) {
                    vector.multiply(i);
                    particleLocation1.add(vector);
                    player.spawnParticle(Particle.REDSTONE, particleLocation1, count, dustOptions);
                    particleLocation1.subtract(vector);
                    vector.normalize();
                    if (step >= 15) {
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(Epidemic.instance(), 0, 3);
    }

    /**
     * Apply vomit effect to specified player
     * @param player Player to apply effect to
     */
    public static void vomit(Player player) {
        new BukkitRunnable() {
            private int step = 0;

            public void run() {
                step++;
                Location location = player.getLocation();
                if (step >= 0 && step < 7) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (0),
                            player.getLocation().getY() + player.getEyeHeight(),
                            player.getLocation().getZ() + (0));
                    ParticleFunctions.displayParticleDust(
                            location,
                            20,  // count
                            46, // red
                            97,   // green
                            46,   // blue
                            1    // size
                    );
                }
                if (step >= 5 && step < 10) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * 0.2),
                            player.getLocation().getY() + player.getEyeHeight() - 0.3,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * 0.2));
                    ParticleFunctions.displayParticleDust(
                            location,
                            20,  // count
                            46, // red
                            120,   // green
                            36,   // blue
                            1    // size
                    );
                }
                if (step >= 7 && step < 15) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * 0.4),
                            player.getLocation().getY() + player.getEyeHeight() - 0.6,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * 0.4));
                    ParticleFunctions.displayParticleDust(
                            location,
                            15,  // count
                            36, // red
                            102,   // green
                            42,   // blue
                            1    // size
                    );
                }
                if (step >= 10 && step < 15) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * 0.5),
                            player.getLocation().getY() + player.getEyeHeight() - 0.9,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * 0.5));
                    ParticleFunctions.displayParticleDust(
                            location,
                            15,  // count
                            22, // red
                            110,   // green
                            42,   // blue
                            2    // size
                    );
                }
                if (step >= 13 && step < 20) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * 0.6),
                            player.getLocation().getY() + player.getEyeHeight() - 1.2,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * 0.6));
                    ParticleFunctions.displayParticleDust(
                            location,
                            10,  // count
                            46, // red
                            100,   // green
                            46,   // blue
                            2    // size
                    );
                }
                if (step >= 17 && step < 20) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * 0.8),
                            player.getLocation().getY() + player.getEyeHeight() - 1.5,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * 0.8));
                    ParticleFunctions.displayParticleDust(
                            location,
                            15,  // count
                            52, // red
                            130,   // green
                            46,   // blue
                            2    // size
                    );
                }
                if (step >= 17 && step < 28) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX()),
                            player.getLocation().getY(),
                            player.getLocation().getZ() + (player.getFacing().getModZ()));
                    ParticleFunctions.displayParticleDust(
                            location,
                            5,  // count
                            30, // red
                            100,   // green
                            30,   // blue
                            3    // size
                    );
                }
                if (step >= 25 && step < 35) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX()),
                            player.getLocation().getY() -0.2,
                            player.getLocation().getZ() + (player.getFacing().getModZ()));
                    ParticleFunctions.displayParticleDust(
                            location,
                            5,  // count
                            38, // red
                            98,   // green
                            51,   // blue
                            3    // size
                    );
                }
                if (step >= 35) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Epidemic.instance(), 0, 3);
    }

    /**
     * Apply losing bowel control effect to specified player
     * @param player Player to apply effect to
     */
    public static void bowel(Player player) {
        new BukkitRunnable() {
            private int step = 0;

            public void run() {
                step++;
                Location location = player.getLocation();
                if (step >= 0 && step < 15) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * - 0.2),
                            player.getLocation().getY() + 0.6,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * - 0.2));
                    ParticleFunctions.displayParticleDust(
                            location,
                            20,  // count
                            128, // red
                            64,   // green
                            43,   // blue
                            1    // size
                    );
                    if (step % 2 == 0) {
                        float volume = 0.3f;
                        float pitch = 0.2f;
                        Bukkit.getWorld(player.getLocation().getWorld().getUID()).playSound(player.getLocation(), Sound.ENTITY_SLIME_SQUISH, volume, pitch);
                    }
                }
                if (step >= 5 && step < 20) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * - 0.2),
                            player.getLocation().getY() + 0.4,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * - 0.2));
                    ParticleFunctions.displayParticleDust(
                            location,
                            20,  // count
                            128, // red
                            64,   // green
                            43,   // blue
                            1    // size
                    );
                }
                if (step >= 10 && step < 25) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * - 0.3),
                            player.getLocation().getY() + 0.2,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * - 0.3));
                    ParticleFunctions.displayParticleDust(
                            location,
                            20,  // count
                            128, // red
                            64,   // green
                            43,   // blue
                            1    // size
                    );
                }
                if (step >= 15 && step < 30) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * - 0.4),
                            player.getLocation().getY() + 0,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * - 0.4));
                    ParticleFunctions.displayParticleDust(
                            location,
                            20,  // count
                            128, // red
                            64,   // green
                            43,   // blue
                            2    // size
                    );
                }
                if (step >= 20 && step < 35) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * - 0.5),
                            player.getLocation().getY() + 0,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * - 0.5));
                    ParticleFunctions.displayParticleDust(
                            location,
                            20,  // count
                            128, // red
                            64,   // green
                            43,   // blue
                            2    // size
                    );
                }
                if (step % 3 == 0) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * - 0.5 + (step / 100)),
                            player.getLocation().getY() + 1.4,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * - 0.5 + (step / 100)));
                    ParticleFunctions.displayParticleDust(
                            location,
                            1,  // count
                            50, // red
                            100,   // green
                            43,   // blue
                            1    // size
                    );
                }
                if (step >= 35) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Epidemic.instance(), 0, 3);
    }

    /**
     * Apply urinate effect to specified player
     * @param player Player to apply effect to
     */
    public static void urinate(Player player) {
        new BukkitRunnable() {
            private int step = 0;

            public void run() {
                step++;
                Location location = player.getLocation();
                if (step >= 0 && step < 15) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                            player.getLocation().getY() + 0.6,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1));
                    ParticleFunctions.displayParticleDust(
                            location,
                            3,  // count
                            205, // red
                            205,   // green
                            22,   // blue
                            1    // size
                    );
                    if (step % 2 == 0) {
                        float volume = 0.3f;
                        float pitch = 0.2f;
                        Bukkit.getWorld(player.getLocation().getWorld().getUID()).playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, volume, pitch);
                    }
                }
                if (step >= 5 && step < 20) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * 0.2),
                            player.getLocation().getY() + 0.4,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * 0.2));
                    ParticleFunctions.displayParticleDust(
                            location,
                            6,  // count
                            205, // red
                            205,   // green
                            22,   // blue
                            1    // size
                    );
                }
                if (step >= 10 && step < 25) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * 0.3),
                            player.getLocation().getY() + 0.2,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * 0.3));
                    ParticleFunctions.displayParticleDust(
                            location,
                            9,  // count
                            205, // red
                            205,   // green
                            22,   // blue
                            1    // size
                    );
                }
                if (step >= 15 && step < 30) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX() + (player.getFacing().getModX() * 0.3),
                            player.getLocation().getY() + 0,
                            player.getLocation().getZ() + (player.getFacing().getModZ() * 0.3));
                    ParticleFunctions.displayParticleDust(
                            location,
                            10,  // count
                            205, // red
                            205,   // green
                            22,   // blue
                            1    // size
                    );
                }
                if (step >= 35) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Epidemic.instance(), 0, 3);
    }

    /**
     * Apply sweat effect to specified player
     * @param player Player to apply effect to
     */
    public static void sweat(Player player) {
        new BukkitRunnable() {
            private int step = 0;

            public void run() {
                step++;
                Location location = player.getLocation();
                if (step >= 0 && step < 35) {

                    if (step % 2 == 0) {
                        location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.2),
                                player.getLocation().getY() + player.getEyeHeight() + 0.1,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.2));
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                232, // red
                                248,   // green
                                252,   // blue
                                1   // size
                        );
                    }
                }

                if (step >= 15) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Epidemic.instance(), 0, 3);
    }

    /**
     * Apply injury effect to specified player
     * @param player Player to apply effect to
     */
    public static void injury(Player player) {
        float volume = 0.1f;
        float pitch = 0.1f;
        Bukkit.getWorld(player.getLocation().getWorld().getUID()).playSound(player.getLocation(), Sound.ENTITY_POLAR_BEAR_HURT, volume, pitch);
        new BukkitRunnable() {
            private int step = 0;

            public void run() {
                step++;
                Location location = player.getLocation();
                if (step >= 0 && step < 25) {
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX(),
                            player.getLocation().getY() + player.getEyeHeight() + 0.7,
                            player.getLocation().getZ());
                    ParticleFunctions.displayParticleDust(
                            location,
                            5,  // count
                            0, // red
                            0,   // green
                            0,   // blue
                            1    // size
                    );
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX(),
                            player.getLocation().getY() + player.getEyeHeight() + 0.8,
                            player.getLocation().getZ());
                    ParticleFunctions.displayParticleDust(
                            location,
                            3,  // count
                            10, // red
                            10,   // green
                            10,   // blue
                            1    // size
                    );
                    location = new Location (
                            player.getLocation().getWorld(),
                            player.getLocation().getX(),
                            player.getLocation().getY() + player.getEyeHeight() + 0.9,
                            player.getLocation().getZ() );
                    ParticleFunctions.displayParticleDust(
                            location,
                            1,  // count
                            20, // red
                            20,   // green
                            20,   // blue
                            1    // size
                    );
                    if (step % 2 == 0) {


                        if (step < 35) {

                            if (step % 5 == 0) {
                                location = new Location (
                                        player.getLocation().getWorld(),
                                        player.getLocation().getX(),
                                        player.getLocation().getY() + player.getEyeHeight() + 0.8,
                                        player.getLocation().getZ());
                                ParticleFunctions.displayParticleDust(
                                        location,
                                        3,  // count
                                        141, // red
                                        5,   // green
                                        5,   // blue
                                        1    // size
                                );
                            }
                        }
                    }
                }


                if (step >= 35) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Epidemic.instance(), 0, 3);
    }

    /**
     * Apply bleeding effect to specified player
     * @param player Player to apply effect to
     * @param head_front If true, apply to front of head
     * @param head_back If true, apply to back of head
     * @param chest_front If true, apply to chest
     * @param back If true, apply to back
     * @param left_arm If true, apply to left arm
     * @param right_arm If true, apply to right arm
     * @param left_leg If true, apply to left leg
     * @param right_leg If true, apply to right leg
     */
    public static void bleed(Player player, boolean head_front, boolean head_back, boolean chest_front, boolean back, boolean left_arm, boolean right_arm, boolean left_leg, boolean right_leg) {
        new BukkitRunnable() {
            private int step = 0;

            public void run() {
                step++;

                int r = 186;
                int g = 17;
                int b = 17;

                if (head_front) {
                    //Debug.save("Particles", "bleed", "Front head bleeding applied to " + player.getDisplayName());
                    Location location = player.getLocation();
                    if (step >= 0 && step < 35) {

                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * 0.2),
                                    player.getLocation().getY() + player.getEyeHeight(),
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * 0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    5,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    2   // size
                            );
                        }
                    }
                }
                if (head_back) {
                    //Debug.save("Particles", "bleed", "Back head bleeding applied to " + player.getDisplayName());
                    Location location = player.getLocation();
                    if (step >= 0 && step < 35) {

                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * - 0.2),
                                    player.getLocation().getY() + player.getEyeHeight(),
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * - 0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    5,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    2   // size
                            );
                        }
                    }
                }
                if (chest_front) {
                    //Debug.save("Particles", "bleed", "Chest bleeding applied to " + player.getDisplayName());
                    Location location = player.getLocation();
                    if (step >= 0 && step < 35) {
                        location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.2),
                                player.getLocation().getY() + 1.1,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.2));
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 10 && step < 35) {
                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * 0.2),
                                    player.getLocation().getY() + 1,
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * 0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    3,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    1   // size
                            );
                        }
                    }
                    if (step >= 10 && step < 35) {
                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * 0.2),
                                    player.getLocation().getY() + 0.8,
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * 0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    4,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    1   // size
                            );
                        }
                    }
                    if (step >= 15 && step < 35) {
                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * 0.2),
                                    player.getLocation().getY() + 0.6,
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * 0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    5,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    1   // size
                            );
                        }
                    }
                    if (step >= 20 && step < 35) {
                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * 0.2),
                                    player.getLocation().getY() + 0.4,
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * 0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    5,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    1   // size
                            );
                        }
                    }
                    if (step >= 25 && step < 35) {
                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * 0.2),
                                    player.getLocation().getY() + 0.2,
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * 0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    5,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    2   // size
                            );
                        }
                    }
                    if (step >= 30 && step < 35) {
                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * 0.2),
                                    player.getLocation().getY(),
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * 0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    5,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    2   // size
                            );
                        }
                    }
                }
                if (back) {
                    //Debug.save("Particles", "bleed", "Back bleeding applied to " + player.getDisplayName());
                    Location location = player.getLocation();
                    if (step >= 0 && step < 35) {
                        location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * -0.2),
                                player.getLocation().getY() + 1.1,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * -0.2));
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 10 && step < 35) {
                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * -0.2),
                                    player.getLocation().getY() + 1,
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * -0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    3,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    1   // size
                            );
                        }
                    }
                    if (step >= 10 && step < 35) {
                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * -0.2),
                                    player.getLocation().getY() + 0.8,
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * -0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    4,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    1   // size
                            );
                        }
                    }
                    if (step >= 15 && step < 35) {
                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * -0.2),
                                    player.getLocation().getY() + 0.6,
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * -0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    5,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    1   // size
                            );
                        }
                    }
                    if (step >= 20 && step < 35) {
                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * -0.2),
                                    player.getLocation().getY() + 0.4,
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * -0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    5,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    1   // size
                            );
                        }
                    }
                    if (step >= 25 && step < 35) {
                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * -0.2),
                                    player.getLocation().getY() + 0.2,
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * -0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    5,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    2   // size
                            );
                        }
                    }
                    if (step >= 30 && step < 35) {
                        if (step % 2 == 0) {
                            location = new Location (
                                    player.getLocation().getWorld(),
                                    player.getLocation().getX() + (player.getFacing().getModX() * -0.2),
                                    player.getLocation().getY(),
                                    player.getLocation().getZ() + (player.getFacing().getModZ() * -0.2));
                            ParticleFunctions.displayParticleDust(
                                    location,
                                    5,  // count
                                    r, // red
                                    g,   // green
                                    b,   // blue
                                    2   // size
                            );
                        }
                    }
                }
                if (left_arm) {
                    //Debug.save("Particles", "bleed", "Left Arm bleeding applied to " + player.getDisplayName());
                    Location location = player.getLocation();
                    if (step >= 0 && step < 15) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 1.1,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getLeftSide(base_location, 0.5) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 10 && step < 20) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.9,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getLeftSide(base_location, 0.5) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 15 && step < 25) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.7,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getLeftSide(base_location, 0.5) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 20 && step < 30) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.5,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getLeftSide(base_location, 0.5) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 25 && step < 35) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.3,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getLeftSide(base_location, 0.5) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 30 && step < 35) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY(),
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getLeftSide(base_location, 0.5) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                2   // size
                        );
                    }
                }
                if (right_arm) {
                    //Debug.save("Particles", "bleed", "Right Arm bleeding applied to " + player.getDisplayName());
                    Location location = player.getLocation();
                    if (step >= 0 && step < 15) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 1.1,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getRightSide(base_location, 0.5) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 10 && step < 20) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.9,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getRightSide(base_location, 0.5) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 15 && step < 25) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.7,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getRightSide(base_location, 0.5) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 20 && step < 30) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.5,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getRightSide(base_location, 0.5) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 25 && step < 35) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.3,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getRightSide(base_location, 0.5) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 30 && step < 35) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY(),
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getRightSide(base_location, 0.5) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                2   // size
                        );
                    }
                }
                if (left_leg) {
                    //Debug.save("Particles", "bleed", "Left Leg bleeding applied to " + player.getDisplayName());
                    Location location = player.getLocation();
                    if (step >= 0 && step < 15) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.4,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getLeftSide(base_location, 0.2) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 10 && step < 20) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.3,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getLeftSide(base_location, 0.2) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 15 && step < 25) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.2,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getLeftSide(base_location, 0.2) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 20 && step < 30) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.1,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getLeftSide(base_location, 0.2) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 25 && step < 35) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY(),
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getLeftSide(base_location, 0.2) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                2   // size
                        );
                    }
                }
                if (right_leg) {
                    //Debug.save("Particles", "bleed", "Right Leg bleeding applied to " + player.getDisplayName());
                    Location location = player.getLocation();
                    if (step >= 0 && step < 15) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.4,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getRightSide(base_location, 0.2) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 10 && step < 20) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.3,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getRightSide(base_location, 0.2) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 15 && step < 25) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.2,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getRightSide(base_location, 0.2) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 20 && step < 30) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY() + 0.1,
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getRightSide(base_location, 0.2) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                    if (step >= 25 && step < 35) {
                        Location base_location = new Location (
                                player.getLocation().getWorld(),
                                player.getLocation().getX() + (player.getFacing().getModX() * 0.1),
                                player.getLocation().getY(),
                                player.getLocation().getZ() + (player.getFacing().getModZ() * 0.1),
                                player.getLocation().getYaw(),
                                player.getLocation().getPitch());
                        location = getRightSide(base_location, 0.2) ;
                        ParticleFunctions.displayParticleDust(
                                location,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                2   // size
                        );
                    }
                }

                if (step >= 35) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Epidemic.instance(), 0, 3);
    }

    /**
     * Applies a post teleport effect to the specified player
     * @param player Player to apply effect to
     */
    public static void postTeleport(Player player) {
        float volume = 0.1f;
        float pitch = 0.1f;
        Bukkit.getWorld(player.getLocation().getWorld().getUID()).playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, volume, pitch);
        new BukkitRunnable() {
            private int step = 0;

            public void run() {
                step++;

                if (step < 25) {
                    int points = 12; //amount of points to be generated
                    for (int i = 0; i < 360; i += 360/points) {
                        double angle = (i * Math.PI / 180);
                        double x = 1 * Math.cos(angle);
                        double z = 1 * Math.sin(angle);
                        Location loc = player.getLocation().add(x, (step / 10), z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                10,  // count
                                200, // red
                                30,   // green
                                200,   // blue
                                1   // size
                        );
                    }


                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(Epidemic.instance(), 0, 3);
    }

    /**
     * Apply contagious effect to specified player
     * @param player Player to apply effect to
     */
    public static void contagious(Player player) {
        float volume = 0.1f;
        float pitch = 0.1f;
        Bukkit.getWorld(player.getLocation().getWorld().getUID()).playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, volume, pitch);
        new BukkitRunnable() {
            private int step = 0;

            public void run() {
                step++;

                int r = 128;
                int g = 172;
                int b = 89;

                if (step >= 0 && step < 5) {
                    int points = 12; //amount of points to be generated
                    for (int i = 0; i < 360; i += 360/points) {
                        double angle = (i * Math.PI / 180);
                        double x = 0.5 * Math.cos(angle);
                        double z = 0.5 * Math.sin(angle);
                        Location loc = player.getLocation().add(x, 0, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                        loc = player.getLocation().add(x, 0.5, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                        loc = player.getLocation().add(x, 1, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                        loc = player.getLocation().add(x, 1.5, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                        loc = player.getLocation().add(x, 2, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }


                }
                if (step >= 5 && step < 10) {
                    int points = 12; //amount of points to be generated
                    for (int i = 0; i < 360; i += 360/points) {
                        double angle = (i * Math.PI / 180);
                        double x = 0.75 * Math.cos(angle);
                        double z = 0.75 * Math.sin(angle);
                        Location loc = player.getLocation().add(x, 0, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                        loc = player.getLocation().add(x, 0.5, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                        loc = player.getLocation().add(x, 1, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                        loc = player.getLocation().add(x, 1.5, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                        loc = player.getLocation().add(x, 2, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                2,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }


                }
                if (step >= 10 && step < 15) {
                    int points = 12; //amount of points to be generated
                    for (int i = 0; i < 360; i += 360/points) {
                        double angle = (i * Math.PI / 180);
                        double x = 1 * Math.cos(angle);
                        double z = 1 * Math.sin(angle);
                        Location loc = player.getLocation().add(x, 0, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                1,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                        loc = player.getLocation().add(x, 0.5, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                1,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                        loc = player.getLocation().add(x, 1, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                1,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                        loc = player.getLocation().add(x, 1.5, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                1,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                        loc = player.getLocation().add(x, 2, z);
                        ParticleFunctions.displayParticleDust(
                                loc,
                                1,  // count
                                r, // red
                                g,   // green
                                b,   // blue
                                1   // size
                        );
                    }
                }
                if (step > 15) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Epidemic.instance(), 0, 3);
    }

}
