package com.farmingkart;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FarmingOverlay extends OverlayPanel {
    private final Client client;
    private final FarmingKartConfig config;
    private final FarmingKartPlugin plugin;
    private static TitleComponent TITLE = TitleComponent.builder().color(Color.WHITE).text("FarmingKart").build();
    private static TitleComponent PAUSED = TitleComponent.builder().color(Color.WHITE).text("Timer Paused").build();
    private static TitleComponent RUNNING = TitleComponent.builder().color(Color.GREEN).text("Timer Running!").build();
    private static TitleComponent FINISHED = TitleComponent.builder().color(Color.RED).text("FINISHED!").build();
    private final LineComponent timeComponent = LineComponent.builder().left("Time:").right("").build();

    @Inject
    private FarmingOverlay(FarmingKartConfig config, FarmingKartPlugin plugin, Client client) {
        this.config = config;
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.MED);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Duration elapsedTime = plugin.getTimer().getRealTime();
        graphics.setFont(FontManager.getRunescapeFont());
        panelComponent.getChildren().clear();
        if(plugin.playerWithinPatchArea() || plugin.getTimer().isActive() || plugin.getTimer().isCompleted()) {
            panelComponent.getChildren().add(TITLE);
            timeComponent.setRightColor(Color.white);
            timeComponent.setRight(formatTime(elapsedTime.toSeconds()));
            if (plugin.getTimer().isActive()) {
                panelComponent.getChildren().add(RUNNING);
            } else if (plugin.getTimer().isCompleted()) {
                panelComponent.getChildren().add(FINISHED);
            } else {
                panelComponent.getChildren().add(PAUSED);
            }
            panelComponent.getChildren().add(timeComponent);
        } else {
            panelComponent.getChildren().clear();
            setPriority(OverlayPriority.LOW);
        }
        return super.render(graphics);
    }

    public String formatTime(final long remaining)
    {
        final long hours = TimeUnit.SECONDS.toHours(remaining);
        final long minutes = TimeUnit.SECONDS.toMinutes(remaining % 3600);
        final long seconds = remaining % 60;

        if(remaining < 60) {
            return String.format("%01ds", seconds);
        }
        if(remaining < 3600) {
            return String.format("%2dm %02ds", minutes, seconds);
        }
        return String.format("%1dh %02dm", hours, minutes);
    }
}
