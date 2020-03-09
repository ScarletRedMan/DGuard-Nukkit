package ru.dragonestia.dguard;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import ru.dragonestia.dguard.commands.RegionCommand;
import ru.dragonestia.dguard.custom.CanDoAllCondition;
import ru.dragonestia.dguard.custom.RegionCommandCondition;
import ru.dragonestia.dguard.custom.RegionCountChecker;
import ru.dragonestia.dguard.custom.RegionSizeChecker;
import ru.dragonestia.dguard.elements.Flag;
import ru.dragonestia.dguard.listeners.BlockListener;
import ru.dragonestia.dguard.listeners.PlayerListener;

public class DGuard extends PluginBase {

    public static Config config, areas;

    public static RegionCommandCondition regionCommandCondition = (player) -> true;

    public static RegionCountChecker regionCountChecker = (player, count) -> config.getInt("max-count") > count;

    public static RegionSizeChecker regionSizeChecker = (player, size) -> config.getLong("max-size") > size;

    public static CanDoAllCondition canDoAllCondition = Player::isOp;

    @Override
    public void onLoad() {
        config = new Config("plugins/DGuard/config.yml", Config.YAML);
        areas = new Config("plugins/DGuard/areas.yml", Config.YAML);

        //Основной конфиг
        if(config.exists("max-count")) config.set("max-count", 2);
        if(config.exists("max-size")) config.set("max-size", 10000);
        if(config.exists("can-build-out-region")) config.set("can-build-out-region", true);
    }

    @Override
    public void onEnable() {
        //Регистрация команд
        getServer().getCommandMap().register("", new RegionCommand());

        //Регистрация эвентов
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        //Регистрация флагов
        Flag.register("doors","Свободное открытие дверей", "Возможность другим открывать свободно двери, люки и калитки", false);
        Flag.register("chests", "Свободное открытие сундуков", "Возможность другим игрокоам взаимодействовать с сундуками", false);
        Flag.register("furnace", "Свободжное открытие печек", "Возможность другим игрокам свободно взаимодействовать с печками", false);
        Flag.register("pvp", "Режим PvP", "Возможность драться с другими игроками", false);
    }

}
