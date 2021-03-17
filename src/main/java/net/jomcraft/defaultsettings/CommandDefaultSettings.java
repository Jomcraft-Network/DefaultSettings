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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class CommandDefaultSettings {
	
	private static ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new StringTextComponent(TextFormatting.RED + "Please wait until the last request has finished"));

	protected static void register(FMLServerStartingEvent event) {
		LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("defaultsettings");
		
		literalargumentbuilder.then(Commands.literal("save").executes((command) -> {
	         return saveProcess(command.getSource(), null);
	      }).then(Commands.argument("argument", StringArgumentType.string()).executes((command) -> {
	         return saveProcess(command.getSource(), StringArgumentType.getString(command, "argument"));
	      }))).then(Commands.literal("export-mode").executes((command) -> {
		         return exportMode(command.getSource(), null);
		      }));

		event.getServer().getCommandManager().getDispatcher().register(literalargumentbuilder);
	}
	
	private static int exportMode(CommandSource source, String argument) throws CommandSyntaxException {
		
		if (tpe.getQueue().size() > 0)
			throw FAILED_EXCEPTION.create();
		
		boolean exportMode = FileUtil.exportMode();
		tpe.execute(new ThreadRunnable(source, null) {

			@SuppressWarnings("static-access")
			@Override
			public void run() {
				try {
					if (exportMode) {
						FileUtil.restoreConfigs();
						source.sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Successfully deactivated the export-mode"), true);
					} else {
						FileUtil.moveAllConfigs();
						source.sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Successfully activated the export-mode"), true);
					}
				} catch (IOException e) {
					DefaultSettings.getInstance().log.log(Level.ERROR, "An exception occurred while trying to move the configs:", e);
					source.sendFeedback(new StringTextComponent(TextFormatting.RED + "Couldn't switch the export-mode"), true);
				}
			}
		});
		return 0;

	}
	
	private static int saveProcess(CommandSource source, String argument) throws CommandSyntaxException {

		if (tpe.getQueue().size() > 0)
			throw FAILED_EXCEPTION.create();
		
		if((FileUtil.keysFileExist() || FileUtil.optionsFilesExist() || FileUtil.serversFileExists()) && (argument == null || !argument.equals("-o"))) {
			source.sendFeedback(new StringTextComponent(TextFormatting.GOLD + "These files already exist! If you want to overwrite"), true);
			source.sendFeedback(new StringTextComponent(TextFormatting.GOLD + "them, add the '-o' argument"), true);
			return 0;
		}

		MutableBoolean issue = new MutableBoolean(false);

		tpe.execute(new ThreadRunnable(source, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveKeys();
					source.sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Successfully saved the key configuration"), true);
					FileUtil.restoreKeys();
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
					source.sendFeedback(new StringTextComponent(TextFormatting.RED + "Couldn't save the key configuration!"), true);
					issue.setBoolean(true);
				}
			}
		});

		tpe.execute(new ThreadRunnable(source, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveOptions();
					source.sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Successfully saved the default game options"), true);
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
					source.sendFeedback(new StringTextComponent(TextFormatting.RED + "Couldn't save the default game options!"), true);
					issue.setBoolean(true);
				}
			}
		});

		tpe.execute(new ThreadRunnable(source, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveServers();
					source.sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Successfully saved the server list"), true);
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
					source.sendFeedback(new StringTextComponent(TextFormatting.RED + "Couldn't save the server list!"), true);
					issue.setBoolean(true);
				}

				if (issue.getBoolean())
					source.sendFeedback(new StringTextComponent(TextFormatting.YELLOW + "Please inspect the log files for further information!"), true);
			}
		});

		return 0;
	}
	
}

abstract class ThreadRunnable implements Runnable {

	final CommandSource supply;
	final MutableBoolean issue;

	ThreadRunnable(CommandSource supply, MutableBoolean issue) {
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