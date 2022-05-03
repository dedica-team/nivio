package de.bonndan.nivio.appuser;

import java.util.Objects;

public enum AppUserRole {
    USER,
    ADMIN;

    // declare your defaults with constant values
    private static final AppUserRole defaultValue = USER;

    // `of` as a substitute for `valueOf` handling the default value
    public static AppUserRole of(String value) {
        if (Objects.isNull(value)) {
            return defaultValue;
        }
        else if (!ADMIN.name().equalsIgnoreCase(value)){
            return defaultValue;
        }
        else return AppUserRole.valueOf(value);
    }

}
