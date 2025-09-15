package com.omnia.core.setting.constant;

import com.omnia.core.constant.OmniaConstants;
import com.omnia.core.setting.entity.Setting;
import com.omnia.core.setting.model.SettingSpec;

public interface ISettingCode {
    static Setting getSetting(SettingSpec settingSpec) {
        return OmniaConstants.SETTINGS.get(settingSpec.getKey());
    }

    static long getLongSetting(SettingSpec setting) {
        return getSetting(setting).getValue().asLong();
    }

    static int getIntSetting(SettingSpec setting) {
        return getSetting(setting).getValue().asInt();
    }

    static String getTextSetting(SettingSpec setting) {
        return getSetting(setting).getValue().asText();
    }

    static double getDoubleSetting(SettingSpec setting) {
        return getSetting(setting).getValue().asDouble();
    }

    static boolean getBoolSetting(SettingSpec setting) {
        return getSetting(setting).getValue().asBoolean();
    }
}