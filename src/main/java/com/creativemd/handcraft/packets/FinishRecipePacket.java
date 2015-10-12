package com.creativemd.handcraft.packets;

import java.util.ArrayList;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.handcraft.tick.ServerTick;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class FinishRecipePacket extends CreativeCorePacket{
	
	public FinishRecipePacket()
	{
		
	}

	@Override
	public void writeBytes(ByteBuf buf) {
		
	}

	@Override
	public void readBytes(ByteBuf buf) {
		
	}

	@Override
	public void executeClient(EntityPlayer player) {
		
	}

	@Override
	public void executeServer(EntityPlayer player) {
		ServerTick.finishProduct(player);
	}
	
	
}
