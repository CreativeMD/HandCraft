package com.creativemd.handcraft;

import java.util.ArrayList;

import com.creativemd.creativecore.common.gui.GuiHandler;
import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.handcraft.collect.Importer;
import com.creativemd.handcraft.collect.VanillaImporter;
import com.creativemd.handcraft.config.HandCraftConfigLoader;
import com.creativemd.handcraft.gui.HandGuiHandler;
import com.creativemd.handcraft.packets.ConsumeRecipePacket;
import com.creativemd.handcraft.packets.FinishRecipePacket;
import com.creativemd.handcraft.recipe.HandRecipe;
import com.creativemd.handcraft.tick.ClientTick;
import com.creativemd.handcraft.tick.ServerTick;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = HandCraft.modid, version = HandCraft.version, name = "HandCraft")
public class HandCraft {
	
	public static final String modid = "handcraft";
	public static final String version = "0.1";
	
	public static ArrayList<HandRecipe> recipes = new ArrayList<HandRecipe>();
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		maxLevelPerInfo = config.get("handrecipe", "depthlimit", 10, "").getInt();
		vanillaOnly = config.get("handrecipe", "vanillaOnly", false).getBoolean();
		config.save();
    }
	
	public static final String guiID = "HC";
	public static int maxLevelPerInfo = 10;
	public static boolean vanillaOnly = false;
	
	@SideOnly(Side.CLIENT)
	public static void initClient()
	{
		ClientTick.registerKey();
		ClientTick tick = new ClientTick();
		MinecraftForge.EVENT_BUS.register(tick);
		FMLCommonHandler.instance().bus().register(tick);
	}
	
	@EventHandler
    public void Init(FMLInitializationEvent event)
    {
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
			initClient();
		
		ServerTick tick = new ServerTick();
		MinecraftForge.EVENT_BUS.register(tick);
		FMLCommonHandler.instance().bus().register(tick);
		
		GuiHandler.registerGuiHandler(guiID, new HandGuiHandler());
		
		CreativeCorePacket.registerPacket(ConsumeRecipePacket.class, "HCConsume");
		CreativeCorePacket.registerPacket(FinishRecipePacket.class, "HCFinishRecipe");
		
		Importer.registerImporter(new VanillaImporter());
		
		if(Loader.isModLoaded("ingameconfigmanager"))
			HandCraftConfigLoader.loadConfig();
    }
	
	@EventHandler
    public void postInit(FMLLoadCompleteEvent event)
    {
		for (int i = 0; i < Importer.importers.size(); i++) {
			Importer.importers.get(i).processImport(recipes);
		}
    }
	
}
