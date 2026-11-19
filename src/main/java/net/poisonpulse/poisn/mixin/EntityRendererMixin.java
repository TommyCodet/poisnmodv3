package net.poisonpulse.poisn.mixin;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.poisonpulse.poisn.nametag.Members;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Replaces the name tag of known members with our custom version
 * (rank prefix + optional second line).
 */
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    /**
     * Intercepts the Component that will be rendered as the name tag.
     * Works on recent Minecraft versions that pass the name as a local.
     */
    @ModifyVariable(
            method = "renderNameTag",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private Component poisn$modifyNameTag(Component original, T entity) {
        if (!(entity instanceof Player player)) {
            return original;
        }

        String name = player.getGameProfile().getName();
        if (!Members.isMember(name)) {
            return original;
        }

        return Members.buildNameTag(name);
    }
}
