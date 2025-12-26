package com.lightmap;

public interface ModApiBridge {
    default boolean isModEnabled(String modID) {
        return false;
    }
}