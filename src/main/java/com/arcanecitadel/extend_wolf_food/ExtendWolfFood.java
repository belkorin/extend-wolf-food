package com.arcanecitadel.extend_wolf_food;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import com.arcanecitadel.extend_wolf_food.OreDictionaryHelper;

import org.apache.logging.log4j.Logger;

@Mod(modid = ExtendWolfFood.MODID, name = ExtendWolfFood.NAME, version = ExtendWolfFood.VERSION)
public class ExtendWolfFood
{
    public static final String MODID = "extend_wolf_food";
    public static final String NAME = "Extend Wolf Food";
    public static final String VERSION = "1.0";

    public static Logger logger;

    public static CommonProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	proxy = new CommonProxy();
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("Woof woof om nom nom");
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	OreDictionaryHelper helper = new OreDictionaryHelper();
    	
    	helper.scanForWolfood();
    	
        MinecraftForge.EVENT_BUS.register(helper);

        proxy.postInit(event);
    }
}
