package mod.upcraftlp.worldlimiter.util;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * (c)2017 UpcraftLP
 */
public class ConfigUtil {

    /**
     * get a List of config elements for display in the Forge config gui
     */
    public static List<IConfigElement> getEntries(Configuration config) {
        List<IConfigElement> entries = new ArrayList<>();
        if(config != null) {
            Set<String> categories = config.getCategoryNames();
            categories.forEach((String categoryName) -> {
                ConfigCategory category = config.getCategory(categoryName);
                entries.addAll(new ConfigElement(category).getChildElements());
            });
        }
        return entries;
    }
}
