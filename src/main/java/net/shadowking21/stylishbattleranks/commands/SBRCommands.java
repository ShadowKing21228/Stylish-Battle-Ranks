package net.shadowking21.stylishbattleranks.commands;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.FMLPaths;
import net.shadowking21.stylishbattleranks.SupportCombatCapability;
import net.shadowking21.stylishbattleranks.api.StylishBattle;
import net.shadowking21.stylishbattleranks.config.SBRConfig;

import java.nio.file.Path;

public class SBRCommands {
    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sbr")
                .then(Commands.literal("styleScore")
                        .then(Commands.argument("value", IntegerArgumentType.integer())
                                .requires(source -> source.hasPermission(2))
                        .executes(ctx -> {
                            ctx.getSource().getPlayer().getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent((capability -> {
                                capability.getStyle().setStyleScore(IntegerArgumentType.getInteger(ctx, "value"));
                                StylishBattle.syncBattleData(ctx.getSource().getPlayer());
                                ctx.getSource().sendSuccess(() -> Component.translatable("sbr.command.set"), false);
                            }));
                            return 0;
                        }))
                )
                .then(Commands.literal("selectTrack")
                    .then(Commands.argument("selectedTrack", StringArgumentType.string())
                    .executes(commandContext -> {
                        SBRConfig.selectedTrack.set(StringArgumentType.getString(commandContext, "selectedTrack"));
                        Path configPath = FMLPaths.CONFIGDIR.get().resolve("stylishbattleranks-client.toml");
                        CommentedFileConfig configData = CommentedFileConfig.of(configPath);
                        configData.load();
                        commandContext.getSource().sendSuccess(() -> Component.literal("Reloaded! Current track is " + StringArgumentType.getString(commandContext, "selectedTrack")), false);
                        return 0;
                    }))
                )
                .then(Commands.literal("changeVolume")
                        .then(Commands.argument("volume", FloatArgumentType.floatArg())
                                .executes(commandContext -> {
                                    SBRConfig.volumeValue.set((double) FloatArgumentType.getFloat(commandContext, "volume"));
                                    Path configPath = FMLPaths.CONFIGDIR.get().resolve("stylishbattleranks-client.toml");
                                    CommentedFileConfig configData = CommentedFileConfig.of(configPath);
                                    configData.load();
                                    commandContext.getSource().sendSuccess(() -> Component.literal("Reloaded! Current volume is " + FloatArgumentType.getFloat(commandContext, "volume")), false);
                                    return 0;
                                }))
                )
        );
    }
    public static void registerCommands(CommandDispatcher<CommandSourceStack> commandSourceStackCommandDispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        registerCommands(commandSourceStackCommandDispatcher);
    }
}
