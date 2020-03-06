package ru.dragonestia.dguard.elements;

public enum Role {

    Nobody(0),
    Guest(1),
    Member(2),
    Owner(3);

    private int id;

    Role(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public static Role get(int id){
        return Role.values()[id];
    }

}
