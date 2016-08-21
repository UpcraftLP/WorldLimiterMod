package mod.upcraftlp.worldlimiter.proxy;

import mod.upcraftlp.worldlimiter.init.ModConfig;
import mod.upcraftlp.worldlimiter.init.WorldBorderEvents;
import mod.upcraftlp.worldlimiter.util.ModUpdate;
import mod.upcraftlp.worldlimiter.util.UpdateEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		ModConfig.init(event);
		ModUpdate.init();
	}
	
	public void init(FMLInitializationEvent event) {
		UpdateEvent.init();
		WorldBorderEvents.init();
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
}
