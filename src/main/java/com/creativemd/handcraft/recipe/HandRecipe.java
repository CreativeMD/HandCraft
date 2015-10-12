package com.creativemd.handcraft.recipe;

import java.util.ArrayList;
import java.util.Arrays;

import com.creativemd.creativecore.common.recipe.Recipe;
import com.creativemd.creativecore.common.utils.InventoryUtils;
import com.creativemd.creativecore.common.utils.stack.StackInfo;
import com.creativemd.handcraft.HandCraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import scala.collection.immutable.Stream.Cons;

public class HandRecipe {
	
	public StackInfo[] input;
	
	public ItemStack output;
	
	public int time;
	
	public HandRecipe(StackInfo[] input, ItemStack output, int time)
	{
		this.input = input;
		this.output = output;
		this.time = time;
	}
	
	public int countPossibleRecipes(IInventory inventory)
	{
		InventoryBasic basic = new InventoryBasic("basic", false, inventory.getSizeInventory());
		for (int i = 0; i < basic.getSizeInventory(); i++) 
			if(inventory.getStackInSlot(i) != null)
				basic.setInventorySlotContents(i, inventory.getStackInSlot(i).copy());
		
		searched.clear();
		
		ArrayList<RecipeOverflow> overflow = new ArrayList<RecipeOverflow>();
		int ammount = 0;
		ConsumeResult result = consumeRecipe(basic, input, overflow);
		while(result != null)
		{
			overflow.addAll(result.overflow);
			ammount++;
			result = consumeRecipe(basic, input, overflow);
			searched.clear();
		}
		
		searched.clear();
		
		return ammount;
	}
	
	public ConsumeResult consumeRecipes(IInventory inventory, int ammount)
	{
		searched.clear();
		int maxProcessCount = countPossibleRecipes(inventory);
		int processCount = Math.min(ammount, maxProcessCount);
		if(ammount < 0)
			processCount = maxProcessCount;
		
		ConsumeResult result = new ConsumeResult();
		
		for (int i = 0; i < processCount; i++) 
		{
			ConsumeResult temp = consumeRecipe(inventory, input, result.overflow);
			if(temp == null)
			{
				searched.clear();
				return null;
			}
			result.addResult(temp);
		}	
		
		for (int i = 0; i < ammount; i++)
			result.addRecipe(this);	
		
		searched.clear();
		
		InventoryUtils.cleanInventory(inventory);
		
		return result;
	}
	
	public ConsumeResult consumeRecipe(IInventory inventory, StackInfo[] infos, ArrayList<RecipeOverflow> overflow)
	{
		ConsumeResult result = new ConsumeResult();
		for (int i = 0; i < infos.length; i++) {
			ConsumeResult temp = consumeStackInfo(inventory, infos[i], overflow);
			if(temp == null)
				return null;
			result.addResult(temp);
		}
		return result;
	}
	
	public static ArrayList<HandRecipe> searched = new ArrayList<HandRecipe>();
	
	public ConsumeResult consumeStackInfo(IInventory inventory, StackInfo info, ArrayList<RecipeOverflow> overflow)
	{
		ConsumeResult result = new ConsumeResult();
		ArrayList<ItemStack> consumed = new ArrayList<ItemStack>();
		
		int stackSize = info.stackSize;
		
		for (int i = 0; i < overflow.size(); i++) {
			if(info.isInstanceIgnoreSize(overflow.get(i).stack))
			{
				int used = Math.min(stackSize, overflow.get(i).stack.stackSize);
				stackSize -= used;
				overflow.get(i).stack.stackSize -= used;
				
			}
		}
		int j = 0; 
		while (j < overflow.size())
		{
			if(overflow.get(j).stack.stackSize == 0)
				overflow.remove(j);
			else
				j++;
		}
		
		if(stackSize > 0)
		{
			StackInfo newInfo = info.copy();
			newInfo.stackSize = stackSize;
			stackSize = InventoryUtils.consumeStackInfo(newInfo, inventory, consumed);
			result.stacks.addAll(consumed);
		}
		
		if(stackSize > 0)
		{
			ArrayList<HandRecipe> searchedInside = new ArrayList<HandRecipe>();
			for (int i = 0; i < HandCraft.recipes.size(); i++) {
				HandRecipe recipe = HandCraft.recipes.get(i);
				if(info.isInstanceIgnoreSize(recipe.output) && !searched.contains(recipe) && !isEqual(input, recipe.input))
				{
					int needed = (int) Math.ceil(stackSize/(double)recipe.output.stackSize);
					int ammount = 0;
					
					searched.add(recipe);
					searchedInside.add(recipe);
					
					//ArrayList<ConsumeResult> newResult = new ArrayList<ConsumeResult>();
					ConsumeResult tempResult = consumeRecipe(inventory, recipe.input, overflow);
					while(tempResult != null && ammount < needed)
					{
						result.addRecipe(recipe);
						result.addResult(tempResult);
						ammount++;
						if(ammount < needed)
							tempResult = consumeRecipe(inventory, recipe.input, overflow);
					}
					
					int crafted = ammount*recipe.output.stackSize;
					ItemStack recipeOutput = recipe.output.copy();
					recipeOutput.stackSize = crafted-stackSize;
					
					if(recipeOutput.stackSize > 0)
					{
						result.addOverflow(new RecipeOverflow(recipeOutput, 0));
					}
					stackSize -= crafted;
				}
				if(stackSize <= 0)
					break;
			}
			for (int i = 0; i < searchedInside.size(); i++) {
				searched.remove(searchedInside.get(i));
			}
		}
		
		if(stackSize > 0)
			return null;
			
		return result;
	}
	
	public static HandRecipe getRecipe(StackInfo[] info, ItemStack result)
	{
		for (int i = 0; i < HandCraft.recipes.size(); i++) {
			if(isEqual(info, HandCraft.recipes.get(i).input) && InventoryUtils.isItemStackEqual(result, HandCraft.recipes.get(i).output))
				return HandCraft.recipes.get(i);
		}
		return null;
	}
	
	public static boolean isEqual(StackInfo[] info, StackInfo[] info2)
	{
		if(info.length != info2.length)
			return false;
		for (int i = 0; i < info.length; i++) {
			if(!info[i].equals(info2[i]))
				return false;
		}
		return true;
	}
	
	public static class ConsumeResult {
		
		public ArrayList<RecipeOverflow> overflow = new ArrayList<RecipeOverflow>();
		
		public ArrayList<HandRecipe> recipes = new ArrayList<HandRecipe>();
		
		public ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		
		public ConsumeResult()
		{
			
		}
		
		public ConsumeResult(HandRecipe recipe, RecipeOverflow overflow, ArrayList<ItemStack> stacks)
		{
			this.recipes.add(recipe);
			this.overflow.add(overflow);
			this.stacks.addAll(stacks);
		}
		
		public void addRecipe(HandRecipe recipe, RecipeOverflow overflow)
		{
			this.recipes.add(recipe);
			this.overflow.add(overflow);
		}
		
		public void addOverflow(RecipeOverflow overflow)
		{
			overflow.id = recipes.size()-1;
			this.overflow.add(overflow);
		}
		
		public void addRecipe(HandRecipe recipe)
		{
			this.recipes.add(recipe);
		}
		
		public void addResult(ConsumeResult result)
		{
			for (int i = 0; i < result.overflow.size(); i++) {
				result.overflow.get(i).id += recipes.size();
				this.overflow.add(result.overflow.get(i));
			}
			recipes.addAll(result.recipes);
			//overflow.addAll(result.overflow);
			this.stacks.addAll(result.stacks);
		}
		
	}
	
	public static class RecipeOverflow {
		
		public int id;
		public ItemStack stack;
		
		public RecipeOverflow(ItemStack stack, int id)
		{
			this.id = id;
			this.stack = stack;
		}
		
	}
	
}
