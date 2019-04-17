package de.pt400c.defaultsettings;

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
import net.minecraft.util.text.TextFormatting;

public class CommandDefaultSettings extends CommandBase {

	public static final ArrayList<String> arg = new ArrayList<String>() {
		private static final long serialVersionUID = 9131616853614902481L;
	{	add("save");	}};
	private ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
    @Override
    public String getName() {
        return "defaultsettings";
    }
    
    @Override
    public List<String> getAliases() {
    	return new ArrayList<String>() {
			private static final long serialVersionUID = -6975657557521097820L;
		{	add("ds");	}};
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/defaultsettings [save]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, final ICommandSender sender, String[] args) throws WrongUsageException {
    	if (args.length == 0 || args.length > 1 || !arg.contains(args[0].toLowerCase()))
			throw new WrongUsageException(getUsage(sender));

		if (tpe.getQueue().size() > 0) {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Please wait until the last request has been processed!"));
			return;
		}

		MutableBoolean issue = new MutableBoolean(false);

		tpe.execute(new ThreadRunnable(sender, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveKeys();
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully saved the key configuration"));
					FileUtil.restoreKeys();
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Couldn't save the key configuration!"));
					issue.setBoolean(true);
				}
			}
		});

		tpe.execute(new ThreadRunnable(sender, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveOptions();
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully saved the default game options"));
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Couldn't save the default game options!"));
					issue.setBoolean(true);
				}
			}
		});

		tpe.execute(new ThreadRunnable(sender, issue) {
			
			@Override
			public void run() {
				try {
					FileUtil.saveServers();
					sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully saved the server list"));
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Couldn't save the server list!"));
					issue.setBoolean(true);
				}
				
				if (issue.getBoolean())
					sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Please inspect the log files for further information!"));
			}
		});

	}
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
    	if(args.length < 2) {
            return getListOfStringsMatchingLastWord(args, arg.toArray(new String[0]));
        }
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