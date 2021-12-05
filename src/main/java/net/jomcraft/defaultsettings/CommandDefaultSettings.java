package net.jomcraft.defaultsettings;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.server.ServerStartingEvent;

public class CommandDefaultSettings {

	private static ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponent(ChatFormatting.RED + "Please wait until the last request has finished"));

	protected static void register(ServerStartingEvent event) {
		LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("defaultsettings");

		literalargumentbuilder.then(Commands.literal("save").executes((command) -> {
			return saveProcess(command.getSource(), null);
		}).then(Commands.argument("argument", StringArgumentType.string()).executes((command) -> {
			return saveProcess(command.getSource(), StringArgumentType.getString(command, "argument"));
		}))).then(Commands.literal("saveconfigs").executes((command) -> {
			return saveProcessConfigs(command.getSource(), null);
		}).then(Commands.argument("argument", StringArgumentType.string()).executes((command) -> {
			return saveProcessConfigs(command.getSource(), StringArgumentType.getString(command, "argument"));
		})));

		LiteralCommandNode<CommandSourceStack> node = event.getServer().getCommands().getDispatcher().register(literalargumentbuilder);

		event.getServer().getCommands().getDispatcher().register(Commands.literal("ds").redirect(node));
	}

	private static int saveProcessConfigs(CommandSourceStack source, String argument) throws CommandSyntaxException {

		if (tpe.getQueue().size() > 0)
			throw FAILED_EXCEPTION.create();

		MutableBoolean issue = new MutableBoolean(false);

		tpe.execute(new ThreadRunnable(source, issue) {

			@Override
			public void run() {
				try {
					boolean somethingChanged = FileUtil.checkChangedConfig();

					if (somethingChanged && (argument == null || !argument.equals("-of"))) {
						source.sendSuccess(new TextComponent(ChatFormatting.GOLD + "\n\n"), true);
						source.sendSuccess(new TextComponent(ChatFormatting.GOLD + "You seem to have updated certain config files!"), true);
						source.sendSuccess(new TextComponent(ChatFormatting.GOLD + "Users who already play your pack won't (!) receive those changes.\n"), true);
						source.sendSuccess(new TextComponent(ChatFormatting.GOLD + "If you want to ship the new configs to those players too,"), true);
						source.sendSuccess(new TextComponent(ChatFormatting.GOLD + "append the '-of' argument"), true);
					}
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
					source.sendSuccess(new TextComponent(ChatFormatting.RED + "Couldn't save the config files!"), true);
					issue.setBoolean(true);
				}

				if (issue.getBoolean())
					source.sendSuccess(new TextComponent(ChatFormatting.YELLOW + "Please inspect the log files for further information!"), true);
				else
					try {
						source.sendSuccess(new TextComponent(ChatFormatting.GREEN + "Successfully saved your mod configuration files"), true);
						boolean updateExisting = argument != null && argument.equals("-of");
						FileUtil.checkMD5(updateExisting, true);
						FileUtil.copyAndHashPrivate();
					} catch (IOException e) {
						DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving your configuration:", e);
					}
			}
		});

		return 0;
	}

	private static int saveProcess(CommandSourceStack source, String argument) throws CommandSyntaxException {

		if (tpe.getQueue().size() > 0)
			throw FAILED_EXCEPTION.create();

		if ((FileUtil.keysFileExist() || FileUtil.optionsFilesExist() || FileUtil.serversFileExists()) && (argument == null || (!argument.equals("-o") && !argument.equals("-of")))) {
			source.sendSuccess(new TextComponent(ChatFormatting.GOLD + "These files already exist! If you want to overwrite"), true);
			source.sendSuccess(new TextComponent(ChatFormatting.GOLD + "them, add the '-o' argument"), true);
			return 0;
		}

		MutableBoolean issue = new MutableBoolean(false);

		tpe.execute(new ThreadRunnable(source, issue) {

			@Override
			public void run() {
				try {
					boolean somethingChanged = FileUtil.checkChanged();

					if (somethingChanged && !argument.equals("-of")) {
						source.sendSuccess(new TextComponent(ChatFormatting.GOLD + "\n\n"), true);
						source.sendSuccess(new TextComponent(ChatFormatting.GOLD + "You seem to have updated certain config files!"), true);
						source.sendSuccess(new TextComponent(ChatFormatting.GOLD + "Users who already play your pack won't (!) receive those changes.\n"), true);
						source.sendSuccess(new TextComponent(ChatFormatting.GOLD + "If you want to ship the new configs to those players too,"), true);
						source.sendSuccess(new TextComponent(ChatFormatting.GOLD + "append the '-of' argument instead of '-o'"), true);
					}
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
				}
			}
		});

		tpe.execute(new ThreadRunnable(source, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveKeys();
					source.sendSuccess(new TextComponent(ChatFormatting.GREEN + "Successfully saved the key configuration"), true);
					FileUtil.restoreKeys(true, false);
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
					source.sendSuccess(new TextComponent(ChatFormatting.RED + "Couldn't save the key configuration!"), true);
					issue.setBoolean(true);
				}
			}
		});

		tpe.execute(new ThreadRunnable(source, issue) {

			@Override
			public void run() {
				try {
					boolean optifine = FileUtil.saveOptions();
					source.sendSuccess(new TextComponent(ChatFormatting.GREEN + "Successfully saved the default game options" + (optifine ? " (+ Optifine)" : "")), true);
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
					source.sendSuccess(new TextComponent(ChatFormatting.RED + "Couldn't save the default game options!"), true);
					issue.setBoolean(true);
				}
			}
		});

		tpe.execute(new ThreadRunnable(source, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveServers();
					source.sendSuccess(new TextComponent(ChatFormatting.GREEN + "Successfully saved the server list"), true);
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
					source.sendSuccess(new TextComponent(ChatFormatting.RED + "Couldn't save the server list!"), true);
					issue.setBoolean(true);
				}

				if (issue.getBoolean())
					source.sendSuccess(new TextComponent(ChatFormatting.YELLOW + "Please inspect the log files for further information!"), true);
				else
					try {
						boolean updateExisting = argument != null && argument.equals("-of");
						FileUtil.checkMD5(updateExisting, false);
						FileUtil.copyAndHashPrivate();
					} catch (IOException e) {
						DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving your configuration:", e);
					}
			}
		});

		return 0;
	}

}

abstract class ThreadRunnable implements Runnable {

	final CommandSourceStack supply;
	final MutableBoolean issue;

	ThreadRunnable(CommandSourceStack supply, MutableBoolean issue) {
		this.supply = supply;
		this.issue = issue;
	}
}

class MutableBoolean {

	private boolean bool;

	public MutableBoolean(boolean bool) {
		this.bool = bool;
	}

	public boolean getBoolean() {
		return this.bool;
	}

	public void setBoolean(boolean bool) {
		this.bool = bool;
	}

}