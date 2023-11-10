package com.farmingkart;

import net.runelite.client.config.*;

@ConfigGroup("HerbFarmingKart")
public interface FarmingKartConfig extends Config
{
//	@ConfigSection(
//		name = "Farm Patches",
//		description = "Unlocked Farm patches",
//		position = 0
//	)
//	String farmPatches = "farmPatches";
	@Range(
			min = 3,
			max = 9
	)
	@ConfigItem(
		keyName = "numPatches",
		name = "Herb Patches",
		description = "The message to show to the user when they login"//,
//		position = 0,
//		section = farmPatches
	)
	default int numPatches()
	{
		return 3;
	}
}
