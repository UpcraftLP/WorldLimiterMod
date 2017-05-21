package mod.upcraftlp.worldlimiter;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import mod.upcraftlp.worldlimiter.Reference;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber
public class ModConfig {

	public static int radius;
	public static boolean enableNotification;
	public static int notificationRange;
	public static boolean enableWorldBorder;
	public static List<Integer> dimensions = Lists.newArrayList();
	public static boolean isDirty = false;

	public static Configuration config;

	public static void init(FMLPreInitializationEvent event) {
		config = new Configuration(new File(event.getModConfigurationDirectory(), "craftdevmods/" + Reference.MODID + ".cfg"));
		config.load();
		syncConfig();
	}

	public static void syncConfig() {
        /** Configuration Start **/

        radius = config.getInt("radius", Configuration.CATEGORY_GENERAL, 10000, 0, Integer.MAX_VALUE, "defines how many blocks a player can travel before being teleported, set to 0 to disable");
        enableNotification = config.getBoolean("enableNotification", Configuration.CATEGORY_GENERAL, true, "enable disable notification of players when close to the world border");
        notificationRange = config.getInt("notificationRange", Configuration.CATEGORY_GENERAL, 10, 1, 32, "defines at which distance to the world border a player is notified");
        dimensions = Ints.asList(config.get(Configuration.CATEGORY_GENERAL, "affectedDimensions", new int[]{0}, "comma-separated list of dimension IDs that are affected by the mod").getIntList());
        enableWorldBorder = config.getBoolean("enableWorldBorder", Configuration.CATEGORY_GENERAL, true, "if enabled, change the size of the world border to match the teleportation radius");

        /** Configuration End **/
        if(config.hasChanged()) {
            config.save();
            isDirty = true;
        }
    }

    @SubscribeEvent
    public static void configChanged(ConfigChangedEvent event) {
	    if(event.getModID().equals(Reference.MODID)) {
	        syncConfig();
        }
    }

}
