package com.farmingkart;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@PluginDescriptor(
	name = "Herb Farming Kart"
)
public class FarmingKartPlugin extends Plugin
{
	Map<String, WorldPoint> patchToBottomRight = new HashMap<>() {{
		put("Harmony", new WorldPoint(3791, 2837, 0));
		put("Morytania", new WorldPoint(3607, 3529, 0));
		put("Ardougne", new WorldPoint(2672, 3374, 0));
		put("Catherby", new WorldPoint(2815, 3463, 0));
		put("Falador", new WorldPoint(3060, 3311, 0));
		put("Falador", new WorldPoint(3060, 3311, 0));
		put("Farming Guild", new WorldPoint(1240, 3726, 0));
		put("Kourend", new WorldPoint(1740, 3550, 0));
		put("Troll Stronghold", new WorldPoint(0, 0, 0));//TODO find meeee
		put("Weiss", new WorldPoint(0, 0, 0));//TODO find meeee
	}};
	List<String> farmPatches = List.of("Ardougne", "Catherby", "Falador", "Farming Guild", "Harmony", "Kourend", "Morytania", "Troll Stronghold", "Weiss");

	@Inject
	private Client client;
	@Inject
	private FarmingKartConfig config;
	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}
	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says ", null);
		}
	}

	@Provides
    FarmingKartConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FarmingKartConfig.class);
	}
}
