package ru.dragonestia.dguard.exceptions;

import lombok.Getter;

public class RegionException extends Exception {

    @Getter private final String message;

    public RegionException(String message){
        this.message = message;
    }

}
