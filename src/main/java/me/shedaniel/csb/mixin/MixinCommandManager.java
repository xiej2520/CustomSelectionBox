package me.shedaniel.csb.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.handler.CommandManager;
import net.minecraft.server.command.handler.CommandRegistry;
import net.minecraft.server.command.source.CommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public abstract class MixinCommandManager {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onRegister(MinecraftServer server, CallbackInfo ci) {
        ((CommandRegistry) (Object) this).register(new AbstractCommand() {
            @Override
            public String getName() {
                return "csbconfig";
            }

            @Override
            public String getUsage(CommandSource source) {
                return "csbconfig";
            }

            @Override
            public void run(MinecraftServer server, CommandSource source, String[] args) throws CommandException {

            }
        });
    }

}
