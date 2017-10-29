package mod.upcraftlp.worldlimiter;

import net.minecraft.util.StringUtils;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * @author UpcraftLP
 */
@Mod.EventBusSubscriber
public class ModChecker {

    public static void onWorldJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if(!ModConfig.announceUpdates) return;
        if(event.player.world.isRemote) {

        }
    }

    public static boolean hasUpdate() {
        ForgeVersion.CheckResult result = getResult();
        if(result.status == ForgeVersion.Status.PENDING) {
            Main.getLogger().warn("Cannot check for updates, found status: PENDING!");
            return false;
        }
        return ModConfig.announceBetaUpdates ? result.status.isAnimated() : result.status == ForgeVersion.Status.OUTDATED;
    }

    public static ForgeVersion.CheckResult getResult() {
        return ForgeVersion.getResult(FMLCommonHandler.instance().findContainerFor(Main.INSTANCE));
    }

    public static void notifyServer() {
        if (hasUpdate()) {
            String url = getResult().url;
            Main.getLogger().warn("There's an update available for {}" + (StringUtils.isNullOrEmpty(url) ? "": ", download version {} here: {}"), FMLCommonHandler.instance().findContainerFor(Main.INSTANCE).getName(), getResult().target, url);
        }
    }
}
