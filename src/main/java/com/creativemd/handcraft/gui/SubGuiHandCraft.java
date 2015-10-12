package com.creativemd.handcraft.gui;

import java.util.ArrayList;

import com.creativemd.creativecore.common.gui.SubGui;
import com.creativemd.creativecore.common.gui.controls.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.GuiScrollBox;
import com.creativemd.creativecore.common.gui.event.ControlChangedEvent;
import com.creativemd.creativecore.common.gui.event.ControlClickEvent;
import com.creativemd.handcraft.HandCraft;
import com.creativemd.handcraft.recipe.HandRecipe;
import com.n247s.api.eventapi.eventsystem.CustomEventSubscribe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class SubGuiHandCraft extends SubGui{
	
	public SubGuiHandCraft() {
		super(200, 200);
	}
	
	public static final int itemsPerRow = 8;

	@Override
	public void createControls() {
		ArrayList<String> lines = new ArrayList<String>();
		for (int i = 0; i < CreativeTabs.creativeTabArray.length; i++) {
			if(CreativeTabs.creativeTabArray[i] != CreativeTabs.tabAllSearch && CreativeTabs.creativeTabArray[i] != CreativeTabs.tabInventory)
				lines.add(I18n.format(CreativeTabs.creativeTabArray[i].getTranslatedTabLabel(), new Object[0]));
		}
		controls.add(new GuiComboBox("combo", 10, 10, 180, lines));
		controls.add(new GuiScrollBox("scroll", container.player, 10, 30, 180, 160));
		updateRecipes();
		
	}
	
	public void updateRecipes()
	{
		GuiScrollBox scroll = (GuiScrollBox)getControl("scroll");
		int scrolled = scroll.scrolled;
		scroll.gui.controls.clear();
		scroll.maxScroll = 0;
		scroll.scrolled = 0;
		String selected = ((GuiComboBox)getControl("combo")).caption;
		CreativeTabs tab = null;
		for (int i = 0; i < CreativeTabs.creativeTabArray.length; i++) {
			if(I18n.format(CreativeTabs.creativeTabArray[i].getTranslatedTabLabel(), new Object[0]).equals(selected))
			{
				tab = CreativeTabs.creativeTabArray[i];
				break;
			}
		}
		int count = 0;
		for (int i = 0; i < HandCraft.recipes.size(); i++) {
			if(HandCraft.recipes.get(i).output.getItem().getCreativeTab() != tab)
				continue;
				
			int row = count/itemsPerRow;
			HandRecipeControl control = new HandRecipeControl("r" + count, 2+(count-row*itemsPerRow)*20, row*20, 20, 20, HandCraft.recipes.get(i));
			control.updateCalculation(container.player);
			((GuiScrollBox)getControl("scroll")).addControl(control);
			count++;
		}
		scroll.scrolled = scrolled;
	}
	
	@CustomEventSubscribe
	public void onClicked(ControlClickEvent event)
	{
		if(event.source instanceof HandRecipeControl)
			updateRecipes();
	}
	
	@CustomEventSubscribe
	public void onComboChange(ControlChangedEvent event)
	{
		if(event.source.is("combo"))
			updateRecipes();
	}

	@Override
	public void drawOverlay(FontRenderer fontRenderer) {
		
	}

}
