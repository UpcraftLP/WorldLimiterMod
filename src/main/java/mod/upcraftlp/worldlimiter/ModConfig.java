package mod.upcraftlp.worldlimiter;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Reference.MODID, name = "craftdevmods/" + Reference.MODID)
public class ModConfig {

	@Config.Comment("en/disable update notifications on world join")
	public static boolean announceUpdates = true;

	@Config.Comment("also announce beta updates when available")
	public static boolean announceBetaUpdates = false;

	@Config.Comment("defines how many blocks a player can travel before being teleported, set to 0 to disable")
	@Config.RangeInt(min = 0)
	public static int radius = 10000;

	@Config.Comment("enable disable notification of players when close to the world border")
	public static boolean enableNotification = true;

	@Config.Comment("defines at which distance to the world border a player is notified")
	@Config.RangeInt(min = 0, max = 32)
	public static int notificationRange = 10;

	@Config.Comment("if enabled, change the size of the world border to match the teleportation radius")
	public static boolean enableWorldBorder = true;

	@Config.Comment("comma-separated list of dimension IDs that are affected by the mod")
	public static int[] affectedDimensions = new int[]{0};

    @Mod.EventBusSubscriber
    public static class Handler {

    	@SubscribeEvent
		public static void configChanged(ConfigChangedEvent event) {
			if(event.getModID().equals(Reference.MODID)) {
				ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
			}
		}
	}

}
