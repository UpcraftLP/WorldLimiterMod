package mod.upcraftlp.worldlimiter.init;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModConfig {

	public static boolean enableUpdateChecker;
	public static int maxWorldSize;
	public static boolean enableNotification;
	public static int notificationRange;

	public static void init(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		/** Configuration Start **/
			enableUpdateChecker = config.getBoolean("enableUpdateChecker", Configuration.CATEGORY_GENERAL, true, "enable/disable Update Checker");
			maxWorldSize = config.getInt("maxWorldSize", Configuration.CATEGORY_GENERAL, 10000, 0, Integer.MAX_VALUE, "defines how far a player can travel before being teleported, set to 0 to disable");
			enableNotification = config.getBoolean("enableNotification", Configuration.CATEGORY_GENERAL, true, "enable disable notification of players when close to the world border");
			notificationRange = config.getInt("notificationRange", Configuration.CATEGORY_GENERAL, 10, 10, 32, "defines at which distance to the world border a player is notified");
		/** Configuration End **/
		config.save();
	}
}
