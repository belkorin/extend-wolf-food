package com.arcanecitadel.extend_wolf_food;

import net.minecraftforge.common.config.Configuration;

import java.util.Arrays;
import java.util.HashSet;

public class Config {
	private static final String CATEGORY_GENERAL = "general";

	private static final String harvestcraftRawMeat = "listAllmeatraw";
	private static final String harvestcraftCookedMeat = "listAllmeatcooked";

    public static HashSet<String> foodOredictsToAdd = new HashSet<String>();
    public static HashSet<String> specificFoodsToAdd = new HashSet<String>();
    public static HashSet<String> foodOredictsBlacklist = new HashSet<String>();
    public static HashSet<String> specificFoodsBlacklist = new HashSet<String>();
    public static HashSet<String> foodOredictsToRemove = new HashSet<String>();
    public static HashSet<String> specificFoodsToRemove = new HashSet<String>();
    

    public static void readConfig() {
        Configuration cfg = CommonProxy.config;
        try {
            cfg.load();
            initGeneralConfig(cfg);
        } catch (Exception e1) {
            ExtendWolfFood.logger.error("Problem loading config file!", e1);
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    private static void initGeneralConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration. Wolf food removals are processed before additions.");
        
        foodOredictsToAdd = new HashSet<String>(Arrays.asList(cfg.getStringList("foodOredictsToAdd", CATEGORY_GENERAL, new String[] { harvestcraftRawMeat, harvestcraftCookedMeat}, "Oredict names to mark all items as wolf food")));
        specificFoodsToAdd = new HashSet<String>(Arrays.asList(cfg.getStringList("specificFoodsToAdd", CATEGORY_GENERAL, new String[0], "Specific item names (modid:item_name) to be marked as wolf food")));
        foodOredictsBlacklist = new HashSet<String>(Arrays.asList(cfg.getStringList("foodOredictsBlacklist", CATEGORY_GENERAL, new String[0], "Within the OreDictionary names specified to be marked as wolf food, skip any items that are part of these OreDictionaries")));
        specificFoodsBlacklist = new HashSet<String>(Arrays.asList(cfg.getStringList("specificFoodsBlacklist", CATEGORY_GENERAL, new String[0], "Within the Oredictionary names specified to be marked as wolf food, skip these items")));
        foodOredictsToRemove = new HashSet<String>(Arrays.asList(cfg.getStringList("foodOredictsToRemove", CATEGORY_GENERAL, new String[0], "Remove items with this Oredict name from being wolf food")));
        specificFoodsToRemove = new HashSet<String>(Arrays.asList(cfg.getStringList("specificFoodsToRemove", CATEGORY_GENERAL, new String[0], "Remove these specific items from being wolf food")));
        
    }
}
