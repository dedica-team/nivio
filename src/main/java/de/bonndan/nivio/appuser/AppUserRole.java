package de.bonndan.nivio.appuser;

public enum AppUserRole {
    USER,
    ADMIN;


    // declare your defaults with constant values
    private static final AppUserRole defaultValue = USER;

    // `of` as a substitute for `valueOf` handling the default value
    public static AppUserRole of(String value) {

        if(!value.equals("ADMIN")) return defaultValue;
        return AppUserRole.valueOf(value);
    }

    // `defaultOr` for handling default value for null
    public static AppUserRole defaultOr(AppUserRole value) {
        return value != null ? value : defaultValue;
    }

}
