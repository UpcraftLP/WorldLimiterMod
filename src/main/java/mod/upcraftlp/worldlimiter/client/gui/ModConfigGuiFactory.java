package mod.upcraftlp.worldlimiter.client.gui;

import mod.upcraftlp.worldlimiter.Reference;
import mod.upcraftlp.worldlimiter.ModConfig;
import mod.upcraftlp.worldlimiter.util.ConfigUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * (c)2017 UpcraftLP
 */
public class ModConfigGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {
        //NO-OP
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new Gui(parentScreen);
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return Gui.class;
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Nullable
    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

    static class Gui extends GuiConfig {

        public Gui(GuiScreen parentScreen) {
            super(parentScreen, ConfigUtil.getEntries(ModConfig.config), Reference.MODID, false, false, Reference.MODNAME);
        }
    }
}
