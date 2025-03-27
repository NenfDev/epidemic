package com.ibexmc.epidemic.players;

import com.ibexmc.epidemic.Epidemic;

import java.util.UUID;

public class InvinciblePlayers {
    private UUID uuid;
    private int start = 0;
    private int end = 0;

    public InvinciblePlayers(UUID uuid, int start, int end) {
        this.uuid = uuid;
        this.start = start;
        this.end = end;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public boolean isInvincible() {
        int today = Epidemic.instance().gameData().day().get();
        return (this.start >= today && this.end < today);
    }
}
