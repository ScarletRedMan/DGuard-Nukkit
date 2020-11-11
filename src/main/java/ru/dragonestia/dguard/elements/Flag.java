package ru.dragonestia.dguard.elements;

import cn.nukkit.nbt.tag.CompoundTag;

public class Flag {

    private final String id, name, description;

    private final boolean defaultValue;

    public Flag(String id, String name, String description, boolean defaultValue){
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
        CompoundTag flags = region.getFlags();

        return flags.exist(id)? flags.getBoolean(id) : defaultValue;
    }

}
