package com.tntmodders.takumicraft.provider;

public interface ITCItems extends ITCTranslator {
    EnumTCItemModelType getItemModelType();

    enum EnumTCItemModelType {
        SIMPLE,
        SHIELD,
        NONE
    }
}
