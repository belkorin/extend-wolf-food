package com.arcanecitadel.extend_wolf_food;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import com.arcanecitadel.extend_wolf_food.Config;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.stream.IntStream;

@EventBusSubscriber
public class OreDictionaryHelper {
	
	final boolean developerEnvironment = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	private static Field field = null;
	
	private static HashSet<Integer> whitelistOredictIDs = null;
	private static HashSet<Integer> blacklistOredictIDs = null;
	private static HashSet<Integer> removeOredictIDs = null;
	private static HashSet<Item> whitelistSpecificItems = null;
	private static HashSet<Item> blacklistSpecificItems = null;
	private static HashSet<Item> removeSpecificItems = null;
	
	public void scanForWolfood()
	{
		init();

		for(int id : removeOredictIDs)
		{
			
			NonNullList<ItemStack> items = OreDictionary.getOres(OreDictionary.getOreName(id));
			
			for(ItemStack i : items)
			{
				Item item = i.getItem();
				
				if(!(item instanceof ItemFood))
					continue;
				
				ExtendWolfFood.logger.info("Found wolf food removed via oredict: "+i.getDisplayName());
				
				setIsMeat((ItemFood)item, false);
			}
		}
		
		for(Item item : removeSpecificItems)
		{
			if(!(item instanceof ItemFood))
				continue;
			
			ItemStack i = new ItemStack(item);
			ExtendWolfFood.logger.info("Found wolf food removed via specific item: "+i.getDisplayName());
			
			setIsMeat((ItemFood)item, false);
		}
		
		for(int id : whitelistOredictIDs)
		{
			NonNullList<ItemStack> items = OreDictionary.getOres(OreDictionary.getOreName(id));
			
			for(ItemStack i : items)
			{
				Item item = i.getItem();

				int[] oreIDs = OreDictionary.getOreIDs(i);

				boolean isOredictBlacklist = IntStream.of(oreIDs).anyMatch(x -> blacklistOredictIDs.contains(x));
				
				if(isOredictBlacklist)
					continue;
				
				if(!(item instanceof ItemFood))
					continue;
				
				if(blacklistSpecificItems.contains(item))
					continue;
				
				ExtendWolfFood.logger.info("Found wolf food added via oredict: "+i.getDisplayName());
				
				setIsMeat((ItemFood)item, true);
			}
		}
		
		for(Item item : whitelistSpecificItems)
		{
			
			if(!(item instanceof ItemFood))
				continue;
			
			if(blacklistSpecificItems.contains(item))
				continue;
			
			ItemStack i = new ItemStack(item);
			ExtendWolfFood.logger.info("Found wolf food added via specific item: "+i.getDisplayName());
			
			setIsMeat((ItemFood)item, true);
		}
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL)
    public void onEvent(OreRegisterEvent event)
    {
		ItemStack i = event.getOre();

		Item item = i.getItem();
		
		if(!(item instanceof ItemFood)) {
			return;
		}
		
		int[] oreIDs = OreDictionary.getOreIDs(i);

		init();
		
		boolean isOredictWhitelist = IntStream.of(oreIDs).anyMatch(x -> whitelistOredictIDs.contains(x));
		boolean isOredictBlacklist = IntStream.of(oreIDs).anyMatch(x -> blacklistOredictIDs.contains(x));
		boolean isRemovedOredict = IntStream.of(oreIDs).anyMatch(x -> blacklistOredictIDs.contains(x));
		boolean isWhitelistItem = whitelistSpecificItems.contains(item);
		boolean isBlacklistItem = blacklistSpecificItems.contains(item);
		boolean isRemovedItem = removeSpecificItems.contains(item);
		
		boolean turnOnIsMeat = isWhitelistItem || (isOredictWhitelist && !isOredictBlacklist && !isBlacklistItem);
		boolean turnOffIsMeat = isRemovedOredict || isRemovedItem;
		
		if(turnOnIsMeat) {
			ExtendWolfFood.logger.info("Found wolf food: "+i.getDisplayName());
			
			setIsMeat((ItemFood)item, true);
		}
		else if(turnOffIsMeat) {
			ExtendWolfFood.logger.info("Found food to remove from wolf food: "+i.getDisplayName());
			
			setIsMeat((ItemFood)item, false);
		}
		
    } 
	
	private void init()
	{
		ExtendWolfFood.logger.info("scanning oredicts");
		
		if(whitelistOredictIDs != null) {
			return;
		}
		
		whitelistOredictIDs = new HashSet<Integer>();
		for(String oreDictName : Config.foodOredictsToAdd) {
			ExtendWolfFood.logger.info("wolf food oredict: "+oreDictName);
			
			if(OreDictionary.doesOreNameExist(oreDictName))
				whitelistOredictIDs.add(OreDictionary.getOreID(oreDictName));
			else
				ExtendWolfFood.logger.info("does not exist");
				
		}
		
		blacklistOredictIDs = new HashSet<Integer>();
		for(String oreDictName : Config.foodOredictsBlacklist) {
			ExtendWolfFood.logger.info("wolf food oredict blacklist: "+oreDictName);
			if(OreDictionary.doesOreNameExist(oreDictName))
				blacklistOredictIDs.add(OreDictionary.getOreID(oreDictName));
			else
				ExtendWolfFood.logger.info("does not exist");
		}
		
		removeOredictIDs = new HashSet<Integer>();
		for(String oreDictName : Config.foodOredictsToRemove) {
			if(OreDictionary.doesOreNameExist(oreDictName))
				removeOredictIDs.add(OreDictionary.getOreID(oreDictName));
		}
		
		whitelistSpecificItems = new HashSet<Item>();
		for(String itemName : Config.specificFoodsToAdd) {
			ResourceLocation rl = new ResourceLocation(itemName);
			if(Item.REGISTRY.containsKey(rl))
				whitelistSpecificItems.add(Item.REGISTRY.getObject(rl));
		}
		
		blacklistSpecificItems = new HashSet<Item>();
		for(String itemName : Config.specificFoodsBlacklist) {
			ResourceLocation rl = new ResourceLocation(itemName);
			if(Item.REGISTRY.containsKey(rl))
				blacklistSpecificItems.add(Item.REGISTRY.getObject(rl));
		}
		
		removeSpecificItems = new HashSet<Item>();
		for(String itemName : Config.specificFoodsToRemove) {
			ResourceLocation rl = new ResourceLocation(itemName);
			if(Item.REGISTRY.containsKey(rl))
				removeSpecificItems.add(Item.REGISTRY.getObject(rl));
		}
		
		
	}
	
	private void setIsMeat(ItemFood item, boolean isMeat) {
		try {
			
			if(field == null) {
				Class<ItemFood> clazz = ItemFood.class;
				String fieldName = developerEnvironment ? "isWolfsFavoriteMeat" : "field_77856_bY";
				
				field = clazz.getDeclaredField(fieldName);
				
				field.setAccessible( true );
			}
			
			field.setBoolean( item, isMeat );
			
		} catch (Exception e) {
			ExtendWolfFood.logger.error("Something went wrong: "+e.toString(), e);
		}
	}
}
