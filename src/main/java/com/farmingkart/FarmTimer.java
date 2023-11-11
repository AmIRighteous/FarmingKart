package com.farmingkart;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FarmTimer {

    @Getter
    private boolean active;
    @Getter
    private boolean completed;
    private int ticksElapsed;
    private LocalDateTime lastElapsedTick;

    public Duration getGameTime() {
        return Duration.ofMillis(600* ticksElapsed);
    }

    public Duration getRealTime()
    {
        if (active && lastElapsedTick != null) {
            long millisSinceTick = ChronoUnit.MILLIS.between(lastElapsedTick, LocalDateTime.now());
            return getGameTime().plus(Duration.ofMillis(millisSinceTick));
        }
        return getGameTime();
    }

    public void reset() {
        stop();
        ticksElapsed = 0;
        lastElapsedTick = null;
        active = false;
        completed = false;
    }

    public void start() {
        active = true;
        completed = false;
    }

    public void stop() {
        active = false;
        completed = true;
    }

    public void tick() {
        if (active && !completed) {
            ticksElapsed++;
            lastElapsedTick = LocalDateTime.now();
        }
    }
}
