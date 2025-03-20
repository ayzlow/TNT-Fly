package fr.ayzlow.tntfly.utils;

import org.bukkit.Bukkit;

public class VersionChecker {
    private static final boolean IS_1_8 = isVersion1_8();
    private static final boolean IS_1_13 = isVersion1_13();

    private static boolean isVersion1_8() {
        return Bukkit.getVersion().contains("1.8");
    }

    private static boolean isVersion1_13() {
        return Bukkit.getVersion().contains("1.13");
    }

    public static void someMethod() {
        if (IS_1_8) {
            System.out.println("Minecraft 1.8");
        } else if (IS_1_13) {
            System.out.println("Minecraft 1.13");
        }
    }
}