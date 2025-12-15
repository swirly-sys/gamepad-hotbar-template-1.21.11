package com.swirlysys.gamepadhotbar.config;

import com.swirlysys.gamepadhotbar.util.HotbarPos;
import com.swirlysys.gamepadhotbar.util.HotbarScale;
import net.neoforged.neoforge.common.ModConfigSpec;

public class GamepadHotbarClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue GAMEPAD_HOTBAR_TOGGLE = BUILDER
            .translation("gamepadhotbar.config.gamepad_hotbar_toggle")
            .define("gamepad_hotbar_toggle", true);

    public static final ModConfigSpec.BooleanValue MIRROR_MODE = BUILDER
            .translation("gamepadhotbar.config.mirror_mode")
            .define("mirror_mode", false);

    public static final ModConfigSpec.ConfigValue<HotbarScale> SCALE_X = BUILDER
            .translation("gamepadhotbar.config.scale_hotbar_x")
            .defineEnum("scale_hotbar_x", HotbarScale.TYPE1, HotbarScale.values());

    public static final ModConfigSpec.IntValue PAD_X = BUILDER
            .translation("gamepadhotbar.config.pad_hotbar_x")
            .defineInRange("pad_hotbar_x", 5, -20, 20);

    public static final ModConfigSpec.IntValue PAD_Y = BUILDER
            .translation("gamepadhotbar.config.pad_hotbar_y")
            .defineInRange("pad_hotbar_y", 5, 0, 20);

    public static final ModConfigSpec.ConfigValue<HotbarPos> POS_Y = BUILDER
            .translation("gamepadhotbar.config.pos_hotbar_y")
            .defineEnum("pos_hotbar_y", HotbarPos.BOTTOM, HotbarPos.values());

    public static final ModConfigSpec SPEC = BUILDER.build();
}
