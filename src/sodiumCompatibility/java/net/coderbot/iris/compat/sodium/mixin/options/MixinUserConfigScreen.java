package net.coderbot.iris.compat.sodium.mixin.options;

import com.google.common.collect.ImmutableList;
import me.jellysquid.mods.sodium.config.user.options.OptionPage;
import me.jellysquid.mods.sodium.gui.screen.UserConfigScreen;
import net.coderbot.iris.gui.screen.ShaderPackScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Adds our Shader Packs button to the Sodium options GUI.
 */
@Mixin(UserConfigScreen.class)
public class MixinUserConfigScreen extends Screen {
    @Shadow(remap = false)
    @Final
    private List<OptionPage> pages;

    @Unique
    private OptionPage shaderPacks;

    // make compiler happy
    protected MixinUserConfigScreen(Component title) {
        super(title);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void iris$onInit(Screen prevScreen, CallbackInfo ci) {
        TranslatableComponent shaderPacksTranslated = new TranslatableComponent("options.iris.shaderPackSelection");
        shaderPacks = new OptionPage(shaderPacksTranslated, ImmutableList.of());
        pages.add(shaderPacks);
    }

    @Inject(method = "setPage", at = @At("HEAD"), remap = false, cancellable = true)
    private void iris$onSetPage(OptionPage page, CallbackInfo ci) {
        if (page == shaderPacks) {
            minecraft.setScreen(new ShaderPackScreen(this));
            ci.cancel();
        }
    }
}