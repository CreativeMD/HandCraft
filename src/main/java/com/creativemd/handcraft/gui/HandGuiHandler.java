package com.creativemd.handcraft.gui;

import com.creativemd.creativecore.common.container.SubContainer;
import com.creativemd.creativecore.common.gui.CustomGuiHandler;
import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.handcraft.container.SubContainerHandCraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class HandGuiHandler extends CustomGuiHandler{

	@Override
	public SubContainer getContainer(EntityPlayer player, NBTTagCompound nbt) {
		return new SubContainerHandCraft(player);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public SubGui getGui(EntityPlayer player, NBTTagCompound nbt) {
		return new SubGuiHandCraft();
	}

}
