package com.creativemd.handcraft.tick;

import java.util.ArrayList;
import java.util.HashMap;

import com.creativemd.creativecore.common.utils.HashMapList;
import com.creativemd.creativecore.common.utils.InventoryUtils;
import com.creativemd.creativecore.common.utils.WorldUtils;
import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.handcraft.recipe.HandRecipe;
import com.creativemd.handcraft.recipe.RecipeChain;
import com.creativemd.handcraft.recipe.HandRecipe.ConsumeResult;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ServerTick {
	
	private static HashMapList<EntityPlayer, RecipeChain> chains = new HashMapList<EntityPlayer, RecipeChain>();
	
	public static boolean removeChain(EntityPlayer player, RecipeChain chain)
	{
		return chains.removeValue(player, chain);
	}
	
	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event)
	{
		ArrayList<RecipeChain> playerChains = chains.getValues(event.player);
		if(playerChains != null)
		{
			for (int i = 0; i < playerChains.size(); i++) {
				for (int j = 0; j < playerChains.get(i).result.stacks.size(); j++) {
					if(!InventoryUtils.addItemStackToInventory(event.player.inventory, playerChains.get(i).result.stacks.get(j)))
						WorldUtils.dropItem(event.player, playerChains.get(i).result.stacks.get(j));
				}
			}
			
			chains.removeKey(event.player);
		}
	}
	
	public static void finishProduct(EntityPlayer player)
	{
		ArrayList<RecipeChain> chainsOfPlayer = chains.getValues(player);
		if(chainsOfPlayer != null && chainsOfPlayer.size() > 0)
		{
			chainsOfPlayer.get(0).processRecipe();
		}
	}
	
	public static void addChain(EntityPlayer player, HandRecipe recipe, int ammount)
	{
		ConsumeResult result = recipe.consumeRecipes(player.inventory, ammount);	
		if(result != null)
			chains.add(player, new RecipeChain(player, recipe, ammount, result));
				
	}
}
