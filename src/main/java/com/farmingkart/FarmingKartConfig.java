package com.farmingkart;

import net.runelite.client.config.*;

@ConfigGroup("HerbFarmingKart")
public interface FarmingKartConfig extends Config
{
	String CONFIG_GROUP_NAME = "farmingkart";
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
