package com.creativemd.handcraft.gui;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.creativemd.creativecore.client.avatar.Avatar;
import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.handcraft.recipe.HandRecipe;
import com.creativemd.handcraft.tick.ClientTick;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public class HandRecipeControl extends GuiControl{
	
	public HandRecipe recipe;
	public Avatar avatar;
	public int ammount;
	
	public HandRecipeControl(String name, int x, int y, int width, int height, HandRecipe recipe) {
		super(name, x, y, width, height);
		this.recipe = recipe;
		ItemStack result = recipe.output.copy();
		result.stackSize = 1;
		this.avatar = new AvatarItemStack(result);
	}
	
	public void updateCalculation(EntityPlayer player)
	{
		this.ammount = recipe.countPossibleRecipes(player.inventory);
	}

	@Override
	public void drawControl(FontRenderer renderer) {
		GL11.glDisable(GL11.GL_LIGHTING);
		if(ammount == 0)
			RenderHelper2D.drawRect(0, 0, width, height, Vec3.createVectorHelper(0.2, 0.2, 0.2), 0.5);
		avatar.handleRendering(mc, renderer, 18, 18);
		String title = "" + ammount;
		if(recipe.output.stackSize > 1)
			title += "x" + recipe.output.stackSize;
		GL11.glPushMatrix();
		GL11.glScaled(0.5, 0.5, 0.5);
		renderer.drawStringWithShadow(title, width*2-renderer.getStringWidth(title)-2, height*2-renderer.FONT_HEIGHT, 14737632);
		GL11.glPopMatrix();
	}
	
	@Override
	public ArrayList<String> getTooltip()
	{
		ArrayList<String> tip = new ArrayList<String>();
		tip.add("§e" + recipe.output.getDisplayName());
		for (int i = 0; i < recipe.input.length; i++) {
			String text = "" + recipe.input[i].stackSize + " " + recipe.input[i].toTitle();
			tip.add(text);
		}
		return tip;
	}
	
	public boolean mousePressed(int posX, int posY, int button){
		updateCalculation(parent.container.player);
		if(ammount == 0)
			return false;
		int ammount = 1;
		if(button == 1)
			ammount = 5;
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			ammount = this.ammount;
		
		playSound("gui.button.press");
		ClientTick.addRecipeChain(parent.container.player, recipe, ammount);
		return true;
	}

}
