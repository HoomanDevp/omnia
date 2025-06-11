package com.omnia.core.setting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SettingSpec {

    private String key;
    private String developerDescription;

    @Override
    public String toString() {
        return getKey();
    }
}