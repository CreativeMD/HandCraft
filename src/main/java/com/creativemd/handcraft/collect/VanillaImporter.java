package com.creativemd.handcraft.collect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.creativemd.creativecore.common.recipe.IRecipeInfo;
import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.handcraft.recipe.HandRecipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import scala.collection.immutable.Stack;

public class VanillaImporter extends Importer{
	
	public int caculateTime(int length, ItemStack output)
	{
		return length*200;
	}
	
	public Object[] getInput(IRecipe recipe)
	{
		try {
			Method m = recipe.getClass().getMethod("denyHandCraftAccess");
			if(m != null)
				if((Boolean)m.invoke(recipe))
					return null;
		} catch(Exception e){
			
		}
		
		Object[] input = IRecipeInfo.getInput(recipe);
		
		ItemStack output = recipe.getRecipeOutput();
		if(output != null && output.getItem() != null && output.getItem().getUnlocalizedName().contains("ic2.itemToolPainter") && input.length == 1)
			return null;
		
		return input;
	}
	
	public HandRecipe proccessRecipe(IRecipe recipe)
	{
		Object[] input = getInput(recipe);
		if(input != null && recipe.getRecipeOutput() != null)
		{
			ArrayList<StackInfo> infos = new ArrayList<StackInfo>();
			for (int i = 0; i < input.length; i++) {
				StackInfo info = StackInfo.parseObject(input[i]);
				if(info != null)
				{
					info.stackSize = 1;
					int index = info.indexOf(infos);
					if(index == -1)
						infos.add(info);
					else
						infos.get(index).stackSize += info.stackSize;
				}
			}
			if(infos.size() > 0)
				return new HandRecipe(infos.toArray(new StackInfo[0]), recipe.getRecipeOutput().copy(), caculateTime(infos.size(), recipe.getRecipeOutput()));
		}
		return null;
	}

	@Override
	public void processImport(ArrayList<HandRecipe> recipes) {
		List crafting = CraftingManager.getInstance().getRecipeList();
		for (int i = 0; i < crafting.size(); i++) {
			HandRecipe recipe = proccessRecipe((IRecipe) crafting.get(i));
			if(recipe != null)
				recipes.add(recipe);
		}
	}
	
	
	
}
