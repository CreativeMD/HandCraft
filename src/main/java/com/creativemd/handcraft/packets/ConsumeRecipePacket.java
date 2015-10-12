package com.creativemd.handcraft.packets;

import java.util.ArrayList;
import java.util.Arrays;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.utils.HashMapList;
import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.handcraft.recipe.HandRecipe;
import com.creativemd.handcraft.tick.ServerTick;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ConsumeRecipePacket extends CreativeCorePacket{
	
	public ConsumeRecipePacket() {
		
	}
	
	public StackInfo[] info;
	public ItemStack result;
	public int ammount;
	
	public ConsumeRecipePacket(HandRecipe recipe, int ammount) {
		this.info = recipe.input;
		this.result = recipe.output;
		this.ammount = ammount;
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		writeStackInfos(buf, new ArrayList<StackInfo>(Arrays.asList(info)));
		writeItemStack(buf, result);
		buf.writeInt(ammount);
	}

	@Override
	public void readBytes(ByteBuf buf) {
		info = readStackInfos(buf).toArray(new StackInfo[0]);
		result = readItemStack(buf);
		ammount = buf.readInt();
	}

	@Override
	public void executeClient(EntityPlayer player) {
		
	}

	@Override
	public void executeServer(EntityPlayer player) {
		HandRecipe recipe = HandRecipe.getRecipe(info, result);
		if(recipe != null)
			ServerTick.addChain(player, recipe, ammount);
	}

}
