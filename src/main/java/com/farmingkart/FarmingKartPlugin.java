package com.farmingkart;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.*;

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
		put("Troll Stronghold", new WorldPoint(2828, 3694, 0));
		put("Weiss", new WorldPoint(2850, 3934, 0));
	}};
	Set<Integer> herbIds = Set.of(
			199, 	//grimy guam
			200,	//noted grimy guam
			249,	//guam leaf
			250,	//noted guam leaf
			201,	//grimy marrentill
			202,	//noted grimy marrentill
			251,	//marrentill
			252,	//noted marrentill
			203,	//grimy tarromin
			204,	//noted grimy tarromin
			253,	//tarromin
			254,	//noted tarromin
			205,	//grimy harralander
			206,	//noted grimy harralander
			255,	//harralander
			256,	//noted harralander
			207,	//grimy ranarr weed
			208,	//noted grimy ranarr weed
			257,	//ranarr weed
			258,	//noted ranarr weed
			209,	//grimy irit leaf
			210,	//noted grimy irit leaf
			259,	//irit leaf
			260,	//noted irit leaf
			211,	//grimy avantoe
			212,	//noted grimy avantoe
			261,	//avantoe
			262,	//noted avantoe
			213,	//grimy kwuarm
			214,	//noted grimy kwuarm
			263,	//kwuarm
			264,	//noted kwuarm
			215,	//grimy cadantine
			216,	//noted grimy cadantine
			265,	//cadantine
			266,	//noted cadantine
			217,	//grimy dwarf weed
			218,	//noted grimy dwarf weed
			267,	//dwarf weed
			268,	//noted dwarf weed
			219,	//grimy torstol
			220,	//noted grimy torstol
			269,	//torstol
			270,	//noted torstol
			2485,	//grimy lantadyme
			2486,	//noted grimy lantadyme
			2481,	//lantadyme
			2482,	//noted lantadyme
			3049,	//grimy toadflax
			3050,	//noted grimy toadlfax
			2998,	//toadflax
			2999,	//noted toadflax
			3051,	//grimy snapdragon
			3052,	//noted grimy snapdragon
			3000,	//snapdragon
			3001	//noted snapdragon
	);

	Map<String, List<WorldPoint>> patchToTiles = new HashMap<>();
	@Inject
	private Client client;
	@Inject
	private FarmingKartConfig config;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private FarmingOverlay farmingOverlay;
	@Inject
	private ConfigManager configManager;
	private int ticker = 5;
	private int seedPlantingId = 2291; //TODO Confirm
	private int harvestHerbId = 2282; //TODO Confirm
	private int compostId = 8197; //TODO Confirm
	private int spadeId = 830; //TODO Confirm
	private Set<String> patchesHarvested = new HashSet<>();
	@Getter
	private FarmTimer timer = new FarmTimer();
	@Getter
	private WorldPoint playerLocation;
	private ItemContainer inventory;
	Map<String, Integer> hiscores = new HashMap<>();
	@Override
	protected void startUp() throws Exception
	{
		loadHiScores();
		for (String s: patchToBottomRight.keySet()) {
			patchToTiles.put(s, getAdjPatchSquares(patchToBottomRight.get(s)));
		}
		overlayManager.add(farmingOverlay);
	}
	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(farmingOverlay);
		saveHiScores();
	}


	@Subscribe
	public void onGameTick(GameTick t) {
		timer.tick();
		Player local = client.getLocalPlayer();
		playerLocation = local.getWorldLocation();
		int animationId = local.getAnimation();
		if (!timer.isActive()){
			if (animationId == harvestHerbId || animationId == spadeId) {
				timer.reset();
				timer.start();
			}
		} else {
			if (animationId == seedPlantingId && !patchesHarvested.contains(getPlayerPatch())) {
				patchesHarvested.add(getPlayerPatch());
				if (patchesHarvested.size() == config.numPatches()) {
					timer.stop();
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Run complete! Time: " + farmingOverlay.formatTime(timer.getRealTime().getSeconds()), null);
					inventory = client.getItemContainer(InventoryID.INVENTORY);
					updateHiScores(timer.getRealTime().getSeconds(), calculateHerbsFarmed(inventory.getItems()));
				} else {
					client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", getPlayerPatch() + " Split: " + farmingOverlay.formatTime(timer.getRealTime().getSeconds()), null);
				}
			}
		}

	}

	private void updateHiScores(long t, int herbs) {
		if (herbs > hiscores.get("herbCount")) {
			hiscores.put("herbCount", herbs);
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "New Herb PB! " + hiscores.get("herbCount") + " herbs.", null);
		} else {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Herb PB: " + hiscores.get("herbCount"), null);
		}
		if (Math.toIntExact(t) < hiscores.get("timePB")) {
			hiscores.put("timePB", Math.toIntExact(t));
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "New Time PB! " + farmingOverlay.formatTime(t) + " herbs.", null);
		} else {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Time PB: " + farmingOverlay.formatTime(hiscores.get("timePB").longValue()) + " herbs.", null);
		}


	}
	private void loadHiScores() {
		Integer herbCount = configManager.getRSProfileConfiguration(FarmingKartConfig.CONFIG_GROUP_NAME, "herbCount", int.class);
		if (herbCount == null) {
			herbCount = 0;
		}
		hiscores.put("herbCount", herbCount);
		Integer timePB = configManager.getRSProfileConfiguration(FarmingKartConfig.CONFIG_GROUP_NAME, "timePB", int.class);
		if (timePB == null) {
			timePB = Integer.MAX_VALUE;
		}
		hiscores.put("timePB", timePB);
	}

	private void saveHiScores() {
		configManager.setRSProfileConfiguration(FarmingKartConfig.CONFIG_GROUP_NAME, "herbCount", hiscores.get("herbCount"));
		configManager.setRSProfileConfiguration(FarmingKartConfig.CONFIG_GROUP_NAME, "timePB", hiscores.get("timePB"));
	}

	private int calculateHerbsFarmed(Item[] inv) {
		int output = 0;
		for (Item i: inv) {
			if (herbIds.contains(i.getId())) {
				output += i.getQuantity();
			}
		}
		return output;
	}

	private List<WorldPoint> getAdjPatchSquares(WorldPoint bottomRight) {
		List<WorldPoint> output = new ArrayList<>();
		int x = bottomRight.getX();
		int y = bottomRight.getY();
		output.add(bottomRight);
		output.add(new WorldPoint(x-1,y-1,0));
		output.add(new WorldPoint(x-2,y-1,0));
		output.add(new WorldPoint(x-3,y,0));
		output.add(new WorldPoint(x-3,y+1,0));
		output.add(new WorldPoint(x-2,y+2,0));
		output.add(new WorldPoint(x-1,y+2,0));
		output.add(new WorldPoint(x,y+1,0));
		return output;
	}

	public String getPlayerPatch() {
		for (String s: patchToTiles.keySet()) {
			if (patchToTiles.get(s).contains(playerLocation)) {
				return s;
			}
		}
		return null;
	}

	public boolean playerWithinPatchArea() {
		return getPlayerPatch() != null;
	}

	@Provides
    FarmingKartConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FarmingKartConfig.class);
	}
}
