package com.ibexmc.epidemic.util;

import com.ibexmc.epidemic.Epidemic;
import org.bukkit.entity.Player;

public class Permission {

    public enum PermissionType {
        ADMIN("epidemic.admin"),
        BYPASS("epidemic.bypass"),
        CRAFT("epidemic.craft"),
        CURE("epidemic.cure"),
        CURES_TAKE("epidemic.cures.take"),
        DOCTOR("epidemic.doctor"),
        GIVE("epidemic.give"),
        HEALTH("epidemic.health"),
        INFECT("epidemic.infect"),
        RECIPES_DISPLAY("epidemic.recipes.display"),
        REMEDY_TAKE("epidemic.remedy.take"),
        TEMP("epidemic.temp"),
        TEMP_INFO("epidemic.temp.info"),
        THIRST("epidemic.thirst");



        private final String text;

        PermissionType(String text) {
            this.text = text;
        }

        /**
         * Gets the permission text for the PermissionType (example, ADMIN gives epidemic.admin)
         * @return Permission Text
         */
        public String getText() {
            return this.text;
        }

        /**
         * Gets a permission type from the text (example, epidemic.admin gives ADMIN)
         * @param text Permission text
         * @return Permission Type
         */
        public static PermissionType fromString(String text) {
            for (PermissionType b : PermissionType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    /**
     * Checks if the player has a permission based on the Permission type
     * If the player is op or has ADMIN, always returns true
     * @param player Player
     * @param permissionType Permission Type
     * @return If true, player has permission
     */
    public static boolean hasPermission(Player player, PermissionType permissionType) {
        if (player.isOp()) {
            return true;
        }
        if (player.hasPermission(PermissionType.ADMIN.getText())) {
            return true;
        } else {
            return player.hasPermission(permissionType.getText());
        }
    }

    /**
     * Checks if the player has the permission specified.  Intended for use where a
     * permission not currently in the enum is used (for example, epidemic.craft.REMEDYNAME)
     * If it is a base permission, PermissionType lookup should always be used
     * @param player Player
     * @param permission Permission
     * @return If true, player has permission
     */
    public static boolean hasPermission(Player player, String permission) {
        if (player.isOp()) {
            return true;
        }
        if (player.hasPermission(PermissionType.ADMIN.getText())) {
            return true;
        } else {
            return player.hasPermission(permission);
        }
    }

    /**
     * Checks if the player has ADMIN permission
     * @param player Player
     * @return If true, player has ADMIN permission
     */
    public static boolean isAdmin(Player player) {
        return hasPermission(player, PermissionType.ADMIN);
    }

    /**
     * Checks if the player can use Bypass mode
     * @param player Player
     * @return If true, player can use Bypass mode
     */
    public static boolean canBypass(Player player) {
        return hasPermission(player, PermissionType.BYPASS);
    }

    /**
     * Checks if the player can use Bypass, and is in Bypass mode currently
     * Checking IF they can use bypass in case permission is revoked while currently IN bypass
     * @param player Player
     * @return If true, player is in bypass mode
     */
    public static boolean inBypass(Player player) {

        return (canBypass(player) && Epidemic.instance().gameData().bypass().has(player.getUniqueId()));
    }
}
