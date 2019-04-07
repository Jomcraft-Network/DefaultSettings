package de.pt400c.defaultsettings;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import static org.lwjgl.glfw.GLFW.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import static de.pt400c.defaultsettings.FileUtil.MC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiModList;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class EventHandlers {

	private ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TextComponentString(TextFormatting.RED + "Please wait until the last request has been processed!"));

	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		if ((MC.currentScreen instanceof GuiModList || MC.currentScreen == null) && InputMappings.isKeyDown(GLFW_KEY_F7)
				&& InputMappings.isKeyDown(GLFW_KEY_G))
			
			MC.displayGuiScreen(new GuiConfig(Minecraft.getInstance().currentScreen));
	}

	@SubscribeEvent
	public void serverStarting(FMLServerStartingEvent event) {

		LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("defaultsettings");

		literalargumentbuilder.then(Commands.literal("save").executes((p_198483_1_) -> {
			return saveProcess(p_198483_1_.getSource());
		}));

		event.getCommandDispatcher().register(literalargumentbuilder);
	}

	private int saveProcess(CommandSource source) throws CommandSyntaxException {

		if (tpe.getQueue().size() > 0)
			throw FAILED_EXCEPTION.create();

		MutableBoolean issue = new MutableBoolean(false);

		tpe.execute(new ThreadRunnable(source, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveKeys();
					source.sendFeedback(new TextComponentString(TextFormatting.GREEN + "Successfully saved the key configuration"), true);
					FileUtil.restoreKeys();
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
					source.sendFeedback(new TextComponentString(TextFormatting.RED + "Couldn't save the key configuration!"), true);
					issue.setBoolean(true);
				}
			}
		});

		tpe.execute(new ThreadRunnable(source, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveOptions();
					source.sendFeedback(new TextComponentString(TextFormatting.GREEN + "Successfully saved the default game options"), true);
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
					source.sendFeedback(new TextComponentString(TextFormatting.RED + "Couldn't save the default game options!"), true);
					issue.setBoolean(true);
				}
			}
		});

		tpe.execute(new ThreadRunnable(source, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveServers();
					source.sendFeedback(new TextComponentString(TextFormatting.GREEN + "Successfully saved the server list"), true);
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
					source.sendFeedback(new TextComponentString(TextFormatting.RED + "Couldn't save the server list!"), true);
					issue.setBoolean(true);
				}

				if (issue.getBoolean())
					source.sendFeedback(new TextComponentString(TextFormatting.YELLOW + "Please inspect the log files for further information!"), true);
			}
		});

		return 0;
	}

	abstract private class ThreadRunnable implements Runnable {

		final CommandSource supply;
		final MutableBoolean issue;

		ThreadRunnable(CommandSource supply, MutableBoolean issue) {
			this.supply = supply;
			this.issue = issue;
		}
	}

	private class MutableBoolean {

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
}
