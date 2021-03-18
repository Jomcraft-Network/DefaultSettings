package de.pt400c.defaultsettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class CommandDefaultSettings_18 extends CommandBase {

	public static final ArrayList<String> arg = new ArrayList<String>() {
		private static final long serialVersionUID = -8897230905576922296L;
	{	add("save");	add("export-mode"); }};
	
	private ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
	@Override
    public String getName() {
        return "defaultsettings";
    }
	
	@Override
    public String getUsage(ICommandSender sender) {
        return "/defaultsettings [save / export-mode]";
    }
	
	@Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
    public List<String> func_71514_a() {
    	return new ArrayList<String>() {
			private static final long serialVersionUID = -4946223027905825078L;
		{	add("ds");	}};
    }
    
    public List<String> func_180525_a(ICommandSender sender, String[] args, BlockPos pos) {
    	if(args.length < 2) 
            return getListOfStringsMatchingLastWord(args, arg.toArray(new String[0]));
        
		return new ArrayList<String>();
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
	}
    
	public void func_71515_b(final ICommandSender sender, String[] args) throws WrongUsageException {
		if (args.length == 0 || args.length > 2 || !arg.contains(args[0].toLowerCase()))
			throw new WrongUsageException(getUsage(sender));

		if (tpe.getQueue().size() > 0) {
			sender.func_145747_a(new ChatComponentText("\u00a7cPlease wait until the last request has finished"));
			return;
		}

		if (args[0].toLowerCase().equals("save")) {

			if ((FileUtil.keysFileExist() || FileUtil.optionsFilesExist() || FileUtil.serversFileExists()) && (args.length == 1 || (args.length == 2 && !args[1].equals("-o") && !args[1].equals("-of")))) {
				sender.func_145747_a(new ChatComponentText("\u00a76These files already exist! If you want to overwrite"));
				sender.func_145747_a(new ChatComponentText("\u00a76them, add the '-o' argument"));
				return;
			}

			MutableBoolean issue = new MutableBoolean(false);
			
			tpe.execute(new ThreadRunnable(sender, issue) {

				@Override
				public void run() {
					try {
						boolean somethingChanged = FileUtil.checkChanged();

						if(somethingChanged && !args[1].equals("-of")) {
							sender.func_145747_a(new ChatComponentText("\u00a76"));
							sender.func_145747_a(new ChatComponentText("\u00a76"));
							sender.func_145747_a(new ChatComponentText("\u00a76"));
							sender.func_145747_a(new ChatComponentText("\u00a76You seem to have updated certain config files!"));
							sender.func_145747_a(new ChatComponentText("\u00a76Users who already play your pack won't (!)"));
							sender.func_145747_a(new ChatComponentText("\u00a76receive those changes."));
							sender.func_145747_a(new ChatComponentText("\u00a76"));
							sender.func_145747_a(new ChatComponentText("\u00a76If you want to ship the new configs to those players too,"));
							sender.func_145747_a(new ChatComponentText("\u00a76append the '-of' argument instead of '-o'"));
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
						sender.func_145747_a(new ChatComponentText("\u00a7aSuccessfully saved the key configuration"));
						FileUtil.restoreKeys();
					} catch (Exception e) {
						DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
						sender.func_145747_a(new ChatComponentText("\u00a7cCouldn't save the key configuration!"));
						issue.setBoolean(true);
					}
				}
			});

			tpe.execute(new ThreadRunnable(sender, issue) {

				@Override
				public void run() {
					try {
						FileUtil.saveOptions();
						sender.func_145747_a(new ChatComponentText("\u00a7aSuccessfully saved the default game options"));
					} catch (Exception e) {
						DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
						sender.func_145747_a(new ChatComponentText("\u00a7cCouldn't save the default game options!"));
						issue.setBoolean(true);
					}
				}
			});

			tpe.execute(new ThreadRunnable(sender, issue) {

				@Override
				public void run() {
					try {
						FileUtil.saveServers();
						sender.func_145747_a(new ChatComponentText("\u00a7aSuccessfully saved the server list"));
					} catch (Exception e) {
						DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
						sender.func_145747_a(new ChatComponentText("\u00a7cCouldn't save the server list!"));
						issue.setBoolean(true);
					}

					if (issue.getBoolean())
						sender.func_145747_a(new ChatComponentText("\u00a7ePlease inspect the log files for further information!"));
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

				@SuppressWarnings("static-access")
				@Override
				public void run() {
					try {
						if (exportMode) {
							FileUtil.restoreConfigs();
							sender.func_145747_a(new ChatComponentText("\u00a7aSuccessfully deactivated the export-mode"));
						} else {
							FileUtil.moveAllConfigs();
							sender.func_145747_a(new ChatComponentText("\u00a7aSuccessfully activated the export-mode"));
						}
					} catch (IOException e) {
						DefaultSettings.getInstance().log.log(Level.ERROR, "An exception occurred while trying to move the configs:", e);
						sender.func_145747_a(new ChatComponentText("\u00a7cCouldn't switch the export-mode"));
					}
				}
			});
		}

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