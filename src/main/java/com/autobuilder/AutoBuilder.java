package com.autobuilder;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hằng số dùng chung cho toàn bộ AutoBuilder.
 *
 * Đây là mod phía client: class này không đăng ký block/item/entity nào.
 */
public final class AutoBuilder {
    public static final String MOD_ID = "autobuilder";
    public static final String MOD_NAME = "AutoBuilder";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    private AutoBuilder() {
        // Utility class.
    }
}