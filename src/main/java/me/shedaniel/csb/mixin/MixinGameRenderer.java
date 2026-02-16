package me.shedaniel.csb.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.shedaniel.csb.CSBConfig;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @WrapOperation(method = "render(IFJ)V",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/entity/Entity;isSubmergedIn(Lnet/minecraft/block/material/Material;)Z"
            )
    )
    public boolean ignoredSubmergedCheck(Entity instance, Material liquid, Operation<Boolean> original) {
        if (CSBConfig.isEnabled()) {
            return false;
        }
        return original.call(instance, liquid);
    }
}
