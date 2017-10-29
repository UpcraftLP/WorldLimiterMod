package mod.upcraftlp.worldlimiter;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author UpcraftLP
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MODID, value = {Side.CLIENT})
public class ClientUpdateHandler {

    private static boolean hasShown = false;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.ClientTickEvent event) {
        if(hasShown || Minecraft.getMinecraft().player == null) return;
        if (ModChecker.hasUpdate()) {
            String url = ModChecker.getResult().url;
            String targetVersion = ModChecker.getResult().target.toString();
            ITextComponent link = new TextComponentString("here").setStyle(new Style().setColor(TextFormatting.BLUE).setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(url).setStyle(new Style().setColor(TextFormatting.AQUA).setItalic(true)))));
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Update available for " + Reference.MODNAME + ", download v" + targetVersion + " ").appendSibling(link));
        }
        hasShown = true;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerJoinServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        hasShown = false;
    }
}
