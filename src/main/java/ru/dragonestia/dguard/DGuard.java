package ru.dragonestia.dguard;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.dragonestia.dguard.commands.RegionCommand;
import ru.dragonestia.dguard.custom.CustomMethods;
import ru.dragonestia.dguard.listeners.BlockListener;
import ru.dragonestia.dguard.listeners.PlayerListener;
import ru.dragonestia.dguard.region.Flag;
import ru.dragonestia.dguard.region.Region;
import ru.dragonestia.dguard.util.Point;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Objects;

public class DGuard extends PluginBase {

    public static final Gson gson = new Gson();
    @Getter private static DGuard instance;

    public static final HashMap<String, HashMap<Integer, Region>> regions = new HashMap<>();
    public static final HashMap<Long, Region> cachedRegion = new HashMap<>();
    private final HashMap<String, Flag> flags = new HashMap<>();
    @Getter private Settings settings;
    @Getter private CustomMethods customMethods;
    @Getter private Forms forms;
    @Getter private HashMap<Long, Point> firstPoints;
    @Getter private HashMap<Long, Point> secondPoints;

    @Override
    public void onLoad() {
        instance = this;

        firstPoints = new HashMap<>();
        secondPoints = new HashMap<>();

        settings = new Settings(this);
        settings.init();

        customMethods = new CustomMethods(this);
        customMethods.init();

        forms = new Forms(this);
    }

    @Override
    public void onEnable() {
        getServer().getCommandMap().register("", new RegionCommand(this));

        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        registerFlag("doors","Открытие дверей, люков и калиток", "Возможность другим свободно открывать свободно двери, люки и калитки", false);
        registerFlag("chests", "Открытие сундуков", "Возможность другим игрокам взаимодействовать с сундуками", false);
        registerFlag("furnace", "Открытие печек", "Возможность другим игрокам свободно взаимодействовать с печками", false);
        registerFlag("pvp", "Режим PvP", "Возможность драться игрокам с другими игроками", false);
        registerFlag("redstone", "Механизмы", "Возможность использовать механизмы", false);

        loadRegions();
    }

    @Override
    public void onDisable() {
        Config config = new Config("plugins/DGuard/increment", Config.YAML);
        config.set("i", Region.freeId);
        config.save();
    }

    @SneakyThrows
    private void loadRegions(){
        File regionsDir = new File("plugins/DGuard/regions");
        regionsDir.mkdir();

        Config config = new Config("plugins/DGuard/increment", Config.YAML);
        if(!config.exists("i")) config.set("i", 1);
        config.save();
        Region.freeId = config.getInt("i");

        for(File regionFile: Objects.requireNonNull(regionsDir.listFiles())){
            if(!regionFile.isFile()) continue;

            Region region = gson.fromJson(new JsonReader(new FileReader(regionFile)), Region.class);
            region.init(this);
        }
    }

    public void registerFlag(String id, String name, String description, boolean defaultValue){
        flags.put(id.toLowerCase().trim(), new Flag(id.toLowerCase().trim(), name.trim(), description.trim(), defaultValue));
    }

    public HashMap<String, Flag> getFlags() {
        return flags;
    }

}
