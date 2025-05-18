package dev.github.gabrielmartins.api.cooldown;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Cooldown {

    private final long start;
    private final long duration;

    public boolean isExpired() {
        return System.currentTimeMillis() - start >= duration;
    }

    public long getRemaining() {
        long timeLeft = (start + duration) - System.currentTimeMillis();
        return Math.max(0, timeLeft);
    }
}
