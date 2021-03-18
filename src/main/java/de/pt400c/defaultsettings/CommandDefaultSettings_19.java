package de.pt400c.defaultsettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class CommandDefaultSettings_19 extends CommandBase {

	public static final ArrayList<String> arg = new ArrayList<String>() {
		private static final long serialVersionUID = 9131616853614902481L;
	{	add("save");	add("export-mode"); }};
	
	private ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
    public String getName() {
        return "defaultsettings";
    }
    
    public List<String> getAliases() {
    	return new ArrayList<String>() {
			private static final long serialVersionUID = -6975657557521097820L;
		{	add("ds");	}};
    }
    
    public String getUsage(ICommandSender sender) {
        return "/defaultsettings [save / export-mode]";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

	@SuppressWarnings("static-access")
	public void execute(MinecraftServer server, final ICommandSender sender, String[] args) throws WrongUsageException {
		if (args.length == 0 || args.length > 2 || !arg.contains(args[0].toLowerCase()))
			throw new WrongUsageException(getUsage(sender));

		if (tpe.getQueue().size() > 0) {
			sender.sendMessage(new TextComponentString("\u00a7cPlease wait until the last request has finished"));
			return;
		}

		if (args[0].toLowerCase().equals("save")) {

			if ((FileUtil.keysFileExist() || FileUtil.optionsFilesExist() || FileUtil.serversFileExists()) && (args.length == 1 || (args.length == 2 && !args[1].equals("-o") && !args[1].equals("-of")))) {
				sender.sendMessage(new TextComponentString("\u00a76These files already exist! If you want to overwrite"));
				sender.sendMessage(new TextComponentString("\u00a76them, add the '-o' argument"));
				return;
			}

			MutableBoolean issue = new MutableBoolean(false);
			
			tpe.execute(new ThreadRunnable(sender, issue) {

				@Override
				public void run() {
					try {
						boolean somethingChanged = FileUtil.checkChanged();

						if(somethingChanged && !args[1].equals("-of")) {
							sender.sendMessage(new TextComponentString("\u00a76"));
							sender.sendMessage(new TextComponentString("\u00a76"));
							sender.sendMessage(new TextComponentString("\u00a76"));
							sender.sendMessage(new TextComponentString("\u00a76You seem to have updated certain config files!"));
							sender.sendMessage(new TextComponentString("\u00a76Users who already play your pack won't (!)"));
							sender.sendMessage(new TextComponentString("\u00a76receive those changes."));
							sender.sendMessage(new TextComponentString("\u00a76"));
							sender.sendMessage(new TextComponentString("\u00a76If you want to ship the new configs to those players too,"));
							sender.sendMessage(new TextComponentString("\u00a76append the '-of' argument instead of '-o'"));
						}
					} catch (Exception e) {
						DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
					}
				}
			});

			tpe.execute(new ThreadRunnable(sender, issue) {

				@Override
				public void run() {
					try {
						FileUtil.saveKeys();
						sender.sendMessage(new TextComponentString("\u00a7aSuccessfully saved the key configuration"));
						FileUtil.restoreKeys();
					} catch (Exception e) {
						DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
						sender.sendMessage(new TextComponentString("\u00a7cCouldn't save the key configuration!"));
						issue.setBoolean(true);
					}
				}
			});

			tpe.execute(new ThreadRunnable(sender, issue) {

				@Override
				public void run() {
					try {
						FileUtil.saveOptions();
						sender.sendMessage(new TextComponentString("\u00a7aSuccessfully saved the default game options"));
					} catch (Exception e) {
						DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
						sender.sendMessage(new TextComponentString("\u00a7cCouldn't save the default game options!"));
						issue.setBoolean(true);
					}
				}
			});

			tpe.execute(new ThreadRunnable(sender, issue) {

				@Override
				public void run() {
					try {
						FileUtil.saveServers();
						sender.sendMessage(new TextComponentString("\u00a7aSuccessfully saved the server list"));
					} catch (Exception e) {
						DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
						sender.sendMessage(new TextComponentString("\u00a7cCouldn't save the server list!"));
						issue.setBoolean(true);
					}

					if (issue.getBoolean())
						sender.sendMessage(new TextComponentString("\u00a7ePlease inspect the log files for further information!"));
					else
						try {
							boolean updateExisting = args.length > 1 && args[1].equals("-of");
							FileUtil.checkMD5(updateExisting, false);
						} catch (IOException e) {
							DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving your configuration:", e);
						}
				}
			});
		} else {
			boolean exportMode = FileUtil.exportMode();
			tpe.execute(new ThreadRunnable(sender, null) {

				@Override
				public void run() {
					try {
						if (exportMode) {
							FileUtil.restoreConfigs();
							sender.sendMessage(new TextComponentString("\u00a7aSuccessfully deactivated the export-mode"));
						} else {
							sender.sendMessage(new TextComponentString("\u00a7aSuccessfully activated the export-mode"));
						}
					} catch (IOException e) {
						DefaultSettings.getInstance().log.log(Level.ERROR, "An exception occurred while trying to move the configs:", e);
						sender.sendMessage(new TextComponentString("\u00a7cCouldn't switch the export-mode"));
					}
				}
			});
		}
	}

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
    	if(args.length < 2) 
            return getListOfStringsMatchingLastWord(args, arg.toArray(new String[0]));
        
		return new ArrayList<String>();
    }
    
    abstract private class ThreadRunnable implements Runnable {
 	   
        final ICommandSender supply;
        final MutableBoolean issue;

        ThreadRunnable(ICommandSender supply, MutableBoolean issue) {
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