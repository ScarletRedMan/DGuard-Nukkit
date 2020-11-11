package ru.dragonestia.dguard;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import ru.dragonestia.dguard.commands.RegionCommand;
import ru.dragonestia.dguard.custom.CustomMethods;
import ru.dragonestia.dguard.elements.Flag;
import ru.dragonestia.dguard.elements.Region;
import ru.dragonestia.dguard.listeners.BlockListener;
import ru.dragonestia.dguard.listeners.PlayerListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class DGuard extends PluginBase {

    private Config config;

    public static CustomMethods customMethods;

    public static final HashMap<String, Region> regions = new HashMap<>();

    private final HashMap<String, Flag> flags = new HashMap<>();

    private int regionMaxCount, regionMaxSize;

    private boolean canBuildOutRegion;

    private RegionsTag regionsTag;

    private final Forms forms = new Forms(this);

    @Override
    public void onLoad() {
        regionsTag = new RegionsTag(this);
        config = new Config("plugins/DGuard/config.yml", Config.YAML);
        customMethods = new CustomMethods(this);

        if(!config.exists("max-count")) config.set("max-count", 2);
        if(!config.exists("max-size")) config.set("max-size", 10000);
        if(!config.exists("can-build-out-region")) config.set("can-build-out-region", true);
        config.save();

        regionMaxCount = config.getInt("max-count");
        regionMaxSize = config.getInt("max-size");
        canBuildOutRegion = config.getBoolean("can-build-out-region");
    }

    @Override
    public void onEnable() {
        getServer().getCommandMap().register("", new RegionCommand(this));

        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        registerFlag("doors","Свободное открытие дверей, люков и калиток", "Возможность другим открывать свободно двери, люки и калитки", false);
        registerFlag("chests", "Свободное открытие сундуков", "Возможность другим игрокоам взаимодействовать с сундуками", false);
        registerFlag("furnace", "Свободное открытие печек", "Возможность другим игрокам свободно взаимодействовать с печками", false);
        registerFlag("pvp", "Режим PvP", "Возможность драться с другими игроками", false);
        registerFlag("redstone", "Использование кнопок", "Возможность нажимать кнопки", false);

        loadRegions();
    }

    public void loadRegions(){
        Region region;
        for(File file: regionsTag.getFiles()){
            try {
                region = Region.read(regionsTag.readFile(file), this);
                regions.put(region.getId(), region);
            } catch (IOException ex){
                getLogger().error("Не удалось загрузить регион из файла " + file.getName());
                ex.printStackTrace();
            }
        }
    }

    public CustomMethods getCustomMethods() {
        return customMethods;
    }

    public Config getSettingsConfig() {
        return config;
    }

    public boolean isCanBuildOutRegion() {
        return canBuildOutRegion;
    }

    public int getRegionMaxCount() {
        return regionMaxCount;
    }

    public int getRegionMaxSize() {
        return regionMaxSize;
    }

    public void registerFlag(String id, String name, String description, boolean defaultValue){
        flags.put(id.toLowerCase().trim(), new Flag(id.toLowerCase().trim(), name.trim(), description.trim(), defaultValue));
    }

    public Forms getForms(){
        return forms;
    }

    public HashMap<String, Flag> getFlags() {
        return flags;
    }

    public RegionsTag getRegionsTag() {
        return regionsTag;
    }

}
