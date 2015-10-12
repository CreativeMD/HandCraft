package com.creativemd.handcraft.config;

import com.creativemd.ingameconfigmanager.api.core.TabRegistry;
import com.creativemd.ingameconfigmanager.api.tab.ModTab;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class HandCraftConfigLoader {
	
	public static ModTab tab = new ModTab("HandCraft", new ItemStack(Items.paper, 1, 0));
	public static HandMachine handMachine;
	
	public static void loadConfig()
	{
		handMachine = new HandMachine(tab, "HandRecipes");
		TabRegistry.registerModTab(tab);
	}
	
}
