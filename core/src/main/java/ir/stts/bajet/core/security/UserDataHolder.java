package ir.stts.bajet.core.security;

public class UserDataHolder {

    private static final ThreadLocal<LegacyUserData> userDataThreadLocal = new ThreadLocal<>();

    public static LegacyUserData get() {
        return userDataThreadLocal.get();
    }

    public static void set(LegacyUserData legacyUserData) {
        userDataThreadLocal.set(legacyUserData);
    }

    public static void clear() {
        userDataThreadLocal.remove();
    }
}