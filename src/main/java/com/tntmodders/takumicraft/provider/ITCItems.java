package com.tntmodders.takumicraft.provider;

public interface ITCItems extends ITCTranslator {

    default EnumTCItemModelType getItemModelType() {
        return EnumTCItemModelType.SIMPLE;
    }

    enum EnumTCItemModelType {
        SIMPLE,
        SHIELD,
        SPAWN_EGG,
        NONE
    }
}
