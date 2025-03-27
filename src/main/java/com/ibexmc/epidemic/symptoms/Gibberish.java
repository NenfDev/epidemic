package com.ibexmc.epidemic.symptoms;

import com.ibexmc.epidemic.Epidemic;
import org.bukkit.entity.Player;
import com.ibexmc.epidemic.ailments.Afflicted;

import java.util.*;

public class Gibberish {
    /**
     * Shuffles letters in a string
     * @param sentence String to shuffle
     * @return Shuffled string
     */
    private static String shuffle(String sentence) {
        String[] words = sentence.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            List<Character> letters = new ArrayList<>();
            for (char letter : word.toCharArray()) {
                Random r = new Random();
                char c = (char)(r.nextInt(26) + 'a');
                letters.add(c);
            }
            if (letters.size() > 2) {
                Collections.shuffle(letters.subList(1, letters.size() - 1));
            }
            for (char letter : letters) {
                builder.append(letter);
            }
            builder.append(" ");
        }
        return builder.toString();
    }

    /**
     * Gets player chat text and applies either gibberish or muteness to it
     * @param player Player to get text for
     * @param text Text player sent to chat
     * @param chance Chance of it being applied
     * @param checkAilment If true, check ailment before applying
     * @return Either muted or gibberish text
     */
    public String getText(Player player, String text, int chance, boolean checkAilment) {
        if (checkAilment) {
            Set<Afflicted> afflictionSet = Epidemic.instance().data().getPlayerAfflictionsByUUID(player.getUniqueId());
            if (afflictionSet != null) {
                for (Afflicted afflicted : afflictionSet) {
                    if (afflicted.getAilment().isMute()) {
                        return "!#mute#!";
                    }
                    if (afflicted.getAilment().isGibberish()) {
                        Random r = new Random();
                        int randomInt = r.nextInt(100) + 1;
                        if (randomInt <= chance) {
                            return shuffle(text.toLowerCase());
                        } else {
                            return text;
                        }

                    }
                }
            }
        } else {
            // If not checking the ailment, force the gibberish
            return shuffle(text.toLowerCase());
        }
        return text;
    }
}
