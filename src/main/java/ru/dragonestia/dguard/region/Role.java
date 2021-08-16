package ru.dragonestia.dguard.region;

public enum Role {

    Nobody(0, "Никто"),
    Guest(1, "Гость"),
    Member(2, "Житель"),
    Owner(3, "Владелец");

    private final int id;

    private final String name;

    Role(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public static Role get(int id){
        return Role.values()[id];
    }

}
