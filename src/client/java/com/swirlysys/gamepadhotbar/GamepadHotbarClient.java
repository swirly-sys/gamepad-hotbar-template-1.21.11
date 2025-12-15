package com.swirlysys.gamepadhotbar;

import com.mojang.blaze3d.platform.InputConstants;
import com.swirlysys.gamepadhotbar.config.GamepadHotbarClientConfig;
import com.swirlysys.gamepadhotbar.handler.ClientEventHandler;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.apache.logging.log4j.util.Lazy;

public class GamepadHotbarClient implements ClientModInitializer {
    private static final KeyMapping.Category CYCLE_HOTBAR = new KeyMapping.Category(Identifier.fromNamespaceAndPath(
            "gamepadhotbar", "cycle_hotbar"
    ));
    private static final Lazy<KeyMapping> left = Lazy.value(new KeyMapping("key.gamepadhotbar.cyclehotbar_0",
            InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), CYCLE_HOTBAR
    ));
    private static final Lazy<KeyMapping> up = Lazy.value(new KeyMapping("key.gamepadhotbar.cyclehotbar_1",
            InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), CYCLE_HOTBAR
    ));
    private static final Lazy<KeyMapping> right = Lazy.value(new KeyMapping("key.gamepadhotbar.cyclehotbar_2",
            InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), CYCLE_HOTBAR
    ));
    private static final Lazy<KeyMapping> down = Lazy.value(new KeyMapping("key.gamepadhotbar.cyclehotbar_3",
            InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), CYCLE_HOTBAR
    ));
    private static final Lazy<KeyMapping> weapon = Lazy.value(new KeyMapping("key.gamepadhotbar.cyclehotbar_4",
            InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), CYCLE_HOTBAR
    ));

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ClientEventHandler.register();
        ConfigRegistry.INSTANCE.register(GamepadHotbar.MOD_ID, ModConfig.Type.CLIENT, GamepadHotbarClientConfig.SPEC);
        ConfigScreenFactoryRegistry.INSTANCE.register(GamepadHotbar.MOD_ID, ConfigurationScreen::new);

        KeyMapping.Category.register(CYCLE_HOTBAR.id());
        KeyBindingHelper.registerKeyBinding(left.get());
        KeyBindingHelper.registerKeyBinding(up.get());
        KeyBindingHelper.registerKeyBinding(right.get());
        KeyBindingHelper.registerKeyBinding(down.get());
        KeyBindingHelper.registerKeyBinding(weapon.get());
	}

    public static KeyMapping getLeft() {
        return left.get();
    }
    public static KeyMapping getUp() {
        return up.get();
    }
    public static KeyMapping getRight() {
        return right.get();
    }
    public static KeyMapping getDown() {
        return down.get();
    }
    public static KeyMapping getWeapon() {
        return weapon.get();
    }
}