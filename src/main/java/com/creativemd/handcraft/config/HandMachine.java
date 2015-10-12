package com.creativemd.handcraft.config;

import java.util.ArrayList;

import com.creativemd.creativecore.common.container.slot.ContainerControl;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.gui.controls.GuiTextfield;
import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.handcraft.HandCraft;
import com.creativemd.handcraft.recipe.HandRecipe;
import com.creativemd.ingameconfigmanager.api.common.machine.RecipeMachine;
import com.creativemd.ingameconfigmanager.api.common.segment.machine.AddRecipeSegment;
import com.creativemd.ingameconfigmanager.api.tab.ModTab;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class HandMachine extends RecipeMachine<HandRecipe>{

	public HandMachine(ModTab tab, String name) {
		super(tab, name);
	}

	@Override
	public int getWidth() {
		return 5;
	}

	@Override
	public int getHeight() {
		return 2;
	}

	@Override
	public int getOutputCount() {
		return 1;
	}

	@Override
	public void addRecipeToList(HandRecipe recipe) {
		HandCraft.recipes.add(recipe);
	}

	@Override
	public void clearRecipeList() {
		HandCraft.recipes.clear();
	}

	@Override
	public ItemStack[] getOutput(HandRecipe recipe) {
		return new ItemStack[]{recipe.output};
	}

	@Override
	public ArrayList<HandRecipe> getAllExitingRecipes() {
		return HandCraft.recipes;
	}

	@Override
	public void fillGrid(ItemStack[] grid, HandRecipe recipe) {
		for (int i = 0; i < recipe.input.length; i++) {
			grid[i] = recipe.input[i].getItemStack();
		}
	}

	@Override
	public boolean doesSupportStackSize() {
		return true;
	}

	@Override
	public void fillGridInfo(StackInfo[] grid, HandRecipe recipe) {
		for (int i = 0; i < recipe.input.length; i++) {
			grid[i] = recipe.input[i].copy();
		}
	}
	
	@Override
	public void onBeforeSave(HandRecipe recipe, NBTTagCompound nbt)
	{
		nbt.setInteger("time", recipe.time);
	}
	
	@Override
	public void parseExtraInfo(NBTTagCompound nbt, AddRecipeSegment segment, ArrayList<GuiControl> guiControls, ArrayList<ContainerControl> containerControls)
	{
		for (int i = 0; i < guiControls.size(); i++) {
			if(guiControls.get(i).is("time"))
			{
				int power = 0;
				try
				{
					power = Integer.parseInt(((GuiTextfield)guiControls.get(i)).text);
				}catch(Exception e){
					power = 0;
				}
				nbt.setInteger("time", power);
			}
		}
	}
	
	@Override
	public void onControlsCreated(HandRecipe recipe, boolean isAdded, int x, int y, int maxWidth, ArrayList<GuiControl> guiControls, ArrayList<ContainerControl> containerControls)
	{
		if(isAdded)
		{
			guiControls.add(new GuiTextfield("time", recipe != null ? "" + recipe.time : "200", x+maxWidth-80, y, 70, 20).setNumbersOnly());
		}
	}

	@Override
	public HandRecipe parseRecipe(StackInfo[] input, ItemStack[] output, NBTTagCompound nbt, int width, int height) {
		ArrayList<StackInfo> info = new ArrayList<StackInfo>();
		for (int i = 0; i < input.length; i++) {
			if(input[i] != null)
				info.add(input[i]);
		}
		return new HandRecipe(info.toArray(new StackInfo[0]), output[0], nbt.getInteger("time"));
	}

	@Override
	public ItemStack getAvatar() {
		return new ItemStack(Items.paper);
	}

}
