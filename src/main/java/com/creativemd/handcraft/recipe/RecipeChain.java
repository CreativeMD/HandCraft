package com.creativemd.handcraft.recipe;

import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.utils.HashMapList;
import com.creativemd.creativecore.common.utils.InventoryUtils;
import com.creativemd.creativecore.common.utils.WorldUtils;
import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.handcraft.HandCraft;
import com.creativemd.handcraft.packets.ConsumeRecipePacket;
import com.creativemd.handcraft.packets.FinishRecipePacket;
import com.creativemd.handcraft.recipe.HandRecipe.ConsumeResult;
import com.creativemd.handcraft.recipe.HandRecipe.RecipeOverflow;
import com.creativemd.handcraft.tick.ClientTick;
import com.creativemd.handcraft.tick.ServerTick;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class RecipeChain {
	
	public EntityPlayer player;
	public ConsumeResult result;
	public HandRecipe recipe;
	public int ammount;
	
	public RecipeChain(EntityPlayer player, HandRecipe recipe, int ammount, ConsumeResult result)
	{
		this.player = player;
		this.result = result;
		this.recipe = recipe;
		this.ammount = ammount;
	}
	
	@SideOnly(Side.CLIENT)
	public void processClient()
	{
		if(isResult(getCurrentRecipe()))
			GuiControl.playSound(HandCraft.modid + ":finishchain");	
		else	
			GuiControl.playSound(HandCraft.modid + ":finishstep");
		PacketHandler.sendPacketToServer(new FinishRecipePacket());
	}
	
	public void processRecipe()
	{
		HandRecipe recipe = getCurrentRecipe();
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			processClient();
		}else{
			RecipeOverflow overflow = null;
			for (int i = 0; i < result.overflow.size(); i++) {
				if(result.overflow.get(i).id == result.recipes.size()-1)
				{
					overflow = result.overflow.get(i);
					break;
				}
			}
			if(overflow != null)
			{
				if(!InventoryUtils.addItemStackToInventory(player.inventory, overflow.stack.copy()))
					WorldUtils.dropItem(player, overflow.stack.copy());
				result.overflow.remove(overflow);
			}
			if(isResult(recipe))
				if(!InventoryUtils.addItemStackToInventory(player.inventory, recipe.output.copy()))
					WorldUtils.dropItem(player, recipe.output.copy());
		}
		
		result.recipes.remove(0);
		
		if(size() == 0)
			removeChain();
	}
	
	public void removeChain()
	{
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			removeChainClient();
		else
			removeChainServer();
	}
	
	public HandRecipe getRecipe(int index)
	{
		return result.recipes.get(index);
	}
	
	public int size()
	{
		return result.recipes.size();
	}
	
	@SideOnly(Side.CLIENT)
	public void removeChainClient()
	{
		ClientTick.chains.remove(this);
	}
	
	public void removeChainServer()
	{
		ServerTick.removeChain(player, this);
	}
	
	public boolean isResult(HandRecipe recipe)
	{
		return this.recipe == recipe;
	}
	
	public HandRecipe getCurrentRecipe()
	{
		return result.recipes.get(0);
	}
	
	public HandRecipe getResultRecipe()
	{
		return result.recipes.get(result.recipes.size()-1);
	}
	
}
