package com.creativemd.handcraft.collect;

import java.util.ArrayList;

import com.creativemd.handcraft.recipe.HandRecipe;

public abstract class Importer {

	public static ArrayList<Importer> importers = new ArrayList<Importer>();
	
	public static void registerImporter(Importer importer)
	{
		importers.add(importer);
	}
	
	public abstract void processImport(ArrayList<HandRecipe> recipes);
	
}
