package net.jomcraft.defaultsettings.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.jomcraft.defaultsettings.CoreUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandDefaultSettings {

    public static void register(FMLServerStartingEvent event) {
        LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("defaultsettings");

        literalargumentbuilder.then(Commands.literal("save").executes((command) -> {
            return saveProcess(command.getSource(), null, null);
        }).then(Commands.argument("operation", OperationArguments.operationArguments(false)).executes((command) -> {
            return saveProcess(command.getSource(), OperationArguments.getString(command, "operation"), null);
        }).then(Commands.argument("type", TypeArguments.typeArguments()).executes((command) -> {
            return saveProcess(command.getSource(), OperationArguments.getString(command, "operation"), TypeArguments.getString(command, "type"));
        })))).then(Commands.literal("saveconfigs").executes((command) -> {
            return saveProcessConfigs(command.getSource(), null, null);
        }).then(Commands.argument("operation", OperationArguments.operationArguments(true)).executes((command) -> {
            return saveProcessConfigs(command.getSource(), OperationArguments.getString(command, "operation"), null);
        }).then(Commands.argument("config", ConfigArguments.configArguments()).executes((command) -> {
            return saveProcessConfigs(command.getSource(), OperationArguments.getString(command, "operation"), ConfigArguments.getString(command, "config"));
        }))));

        LiteralCommandNode<CommandSource> node = event.getServer().getCommands().getDispatcher().register(literalargumentbuilder);
        event.getServer().getCommands().getDispatcher().register(Commands.literal("ds").redirect(node));
    }

    private static int saveProcessConfigs(CommandSource source, String argument, String argument2) throws CommandSyntaxException {
        try {
            return CoreUtil.saveProcessConfigs(source, argument, argument2);
        } catch (Exception e) {
            if (e instanceof CommandSyntaxException) throw (CommandSyntaxException) e;
            return 0;
        }
    }

    private static int saveProcess(CommandSource source, String argument, String argument2) throws CommandSyntaxException {
        try {
            return CoreUtil.saveProcess(source, argument, argument2);
        } catch (Exception e) {
            if (e instanceof CommandSyntaxException) throw (CommandSyntaxException) e;
            return 0;
        }
    }
}