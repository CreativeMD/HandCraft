package com.creativemd.handcraft.tick;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.creativemd.creativecore.client.rendering.RenderHelper2D;
import com.creativemd.creativecore.common.container.ContainerSub;
import com.creativemd.creativecore.common.gui.GuiHandler;
import com.creativemd.creativecore.common.gui.controls.GuiControl;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.utils.HashMapList;
import com.creativemd.creativecore.common.utils.InventoryUtils;
import com.creativemd.creativecore.common.utils.WorldUtils;
import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.handcraft.HandCraft;
import com.creativemd.handcraft.container.SubContainerHandCraft;
import com.creativemd.handcraft.gui.SubGuiHandCraft;
import com.creativemd.handcraft.packets.ConsumeRecipePacket;
import com.creativemd.handcraft.packets.FinishRecipePacket;
import com.creativemd.handcraft.recipe.HandRecipe;
import com.creativemd.handcraft.recipe.HandRecipe.ConsumeResult;
import com.creativemd.handcraft.recipe.RecipeChain;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;

@SideOnly(Side.CLIENT)
public class ClientTick {
	
	public static void registerKey()
	{
		inventory = new KeyBinding("key.openhandcraft", Keyboard.KEY_C, "key.categories.handcraft");		
		ClientRegistry.registerKeyBinding(inventory);
	}
	
	public static KeyBinding inventory;
	
	public static ArrayList<RecipeChain> chains = new ArrayList<RecipeChain>();
	public static float progress = 0;
	
	public static long lastTick = System.currentTimeMillis();
	
	public static Minecraft mc = Minecraft.getMinecraft();
	
	public static void renderRecipe(int x, int y, HandRecipe recipe, double progress, boolean isResult)
	{
		double percent = progress/recipe.time;
		
		GL11.glPushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		//RenderHelper.
		//OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		//GL11.glEnable(GL11.GL_BLEND);
        //OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		//GL11.glColor4d(1, 1, 1, 1);
		//mc.fontRenderer.drawString(recipe.output.stackSize + "", x, y, GuiControl.White);
		Vec3 color = Vec3.createVectorHelper(100, 100, 100);
		if(isResult)
			color = Vec3.createVectorHelper(0, 0, 0);
		RenderHelper2D.drawRect(x, y, x+18, y+18, color, 0.2);
		
		RenderHelper2D.renderItem(recipe.output, x, y, 1, 0);
		GL11.glTranslated(0, 0, 100);
		String text = recipe.output.stackSize + "";
		mc.fontRenderer.drawStringWithShadow(text, x+18-mc.fontRenderer.getStringWidth(text), y+18-mc.fontRenderer.FONT_HEIGHT, GuiControl.White);
		GL11.glTranslated(0, 0, -100);
		RenderHelper2D.drawRect(x, y+18, x+16, y+20, Vec3.createVectorHelper(0, 0, 0), 1);
		RenderHelper2D.drawRect(x, y+18, (int) (x+16*percent), y+20, Vec3.createVectorHelper(0, 255, 0), 1);
		GL11.glPopMatrix();
		//RenderHelper2D.renderItem(recipe.output, x, y, 1, percent*360);
	}
	
	@SubscribeEvent
	public void onRenderTick(RenderTickEvent event)
	{
		if(event.phase == Phase.END)
		{
			if(mc.thePlayer != null)
			{
				if(!mc.isGamePaused())
				{
					if(mc.inGameHasFocus && mc.thePlayer.openContainer == mc.thePlayer.inventoryContainer && GameSettings.isKeyDown(inventory))
						GuiHandler.openGui(HandCraft.guiID, new NBTTagCompound());
					
					if(chains.size() > 0)
					{
						
						double timeLeft = (System.currentTimeMillis() - lastTick);
						
						progress += timeLeft/10;
						
						int rendered = 0;
						int chainIndex = 0;
						int entryIndex = 0;
						for (int i = 0; rendered < 20; i++) {
							if(chains.size() > chainIndex)
							{
								if(chains.get(chainIndex).size() > entryIndex)
								{
									ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
									renderRecipe(5+rendered*20, resolution.getScaledHeight()-44, chains.get(chainIndex).getRecipe(entryIndex), i == 0 ? progress : 0, chains.get(chainIndex).isResult(chains.get(chainIndex).getRecipe(entryIndex)));
									rendered++;
									entryIndex++;
								}else{
									chainIndex++;
									entryIndex = 0;
								}
							}else
								break;
						}
					
					
						
						if(progress >= chains.get(0).getCurrentRecipe().time)
						{
							chains.get(0).processRecipe();
							progress = 0;
						}
					}
					
					lastTick = System.currentTimeMillis();
				}
			}else
				if(chains.size() > 0)
					chains.clear();
		}
	}
	
	public static void addRecipeChain(EntityPlayer player, HandRecipe recipe, int ammount)
	{
		ConsumeResult result = recipe.consumeRecipes(player.inventory, ammount);	
		if(result != null)
		{
			chains.add(new RecipeChain(player, recipe, ammount, result));
			PacketHandler.sendPacketToServer(new ConsumeRecipePacket(recipe, ammount));
		}
	}
	
}
