package com.creativemd.handcraft.container;

import com.creativemd.creativecore.common.container.SubContainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class SubContainerHandCraft extends SubContainer{

	public SubContainerHandCraft(EntityPlayer player) {
		super(player);
	}

	@Override
	public void createControls() {
		
	}

	@Override
	public void onGuiPacket(int controlID, NBTTagCompound nbt, EntityPlayer player) {
		
	}

}
