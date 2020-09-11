package ru.dragonestia.dguard.elements;

import cn.nukkit.Server;
import ru.dragonestia.dguard.DGuard;

import java.util.HashMap;

public class Flag {

    public static HashMap<String, Flag> flags = new HashMap<>();

    public static void register(String id, String name, String description, boolean defaultValue){
        flags.put(id.toLowerCase().trim(), new Flag(id.toLowerCase().trim(), name.trim(), description.trim(), defaultValue));
        Server.getInstance().getLogger().info("Флаг '" + id.trim().toLowerCase() + "' был успешно зарегистрирован!");
    }

    private final String id, name, description;
    private final boolean defaultValue;

    private Flag(String id, String name, String description, boolean defaultValue){
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    public boolean getValue(Region region){
        if(!region.isExist()) return true;

        if(DGuard.areas.exists(region.getId() + ".flags." + id))
            return DGuard.areas.getBoolean(region.getId() + ".flags." + id);
        return defaultValue;
    }

}
