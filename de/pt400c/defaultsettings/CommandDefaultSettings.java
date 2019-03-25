package de.pt400c.defaultsettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandDefaultSettings extends CommandBase {

	public static final ArrayList<String> arg = new ArrayList<String>() {{	add("save");	}};
	private ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
    @Override
    public String getCommandName() {
        return "defaultsettings";
    }
    
    @Override
    public List<String> getCommandAliases() {
    	return new ArrayList<String>() {{	add("ds");	}};
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/defaultsettings [save]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

	@Override
	public void processCommand(final ICommandSender sender, String[] args) {
		if (args.length > 1 || !arg.contains(args[0].toLowerCase()))
			throw new WrongUsageException(getCommandUsage(sender));

		if (tpe.getQueue().size() > 0) {
			sender.sendChatToPlayer(ColorEnum.RED + "Please wait until the last request has been processed!");
			return;
		}

		Boolean issue = false;

		tpe.execute(new ThreadRunnable(sender, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveKeys();
					sender.sendChatToPlayer(ColorEnum.GREEN + "Successfully saved the key configuration");
					FileUtil.restoreKeys();
				} catch (Exception e) {
					DefaultSettings.log.log(Level.SEVERE, "An exception occurred while saving the key configuration:", e);
					sender.sendChatToPlayer(ColorEnum.RED + "Couldn't save the key configuration!");
					issue = true;
				}
			}
		});

		tpe.execute(new ThreadRunnable(sender, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveOptions();
					sender.sendChatToPlayer(ColorEnum.GREEN + "Successfully saved the default game options");
				} catch (Exception e) {
					DefaultSettings.log.log(Level.SEVERE, "An exception occurred while saving the default game options:", e);
					sender.sendChatToPlayer(ColorEnum.RED + "Couldn't save the default game options!");
					issue = true;
				}
			}
		});

		tpe.execute(new ThreadRunnable(sender, issue) {
			
			@Override
			public void run() {
				try {
					FileUtil.saveServers();
					sender.sendChatToPlayer(ColorEnum.GREEN + "Successfully saved the server list");
				} catch (Exception e) {
					DefaultSettings.log.log(Level.SEVERE, "An exception occurred while saving the server list:", e);
					sender.sendChatToPlayer(ColorEnum.RED + "Couldn't save the server list!");
					issue = true;
				}
			}
		});

		if (issue)
			sender.sendChatToPlayer(ColorEnum.YELLOW + "Please inspect the log files for further information!");

	}

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if(args.length < 2) {
            return getListOfStringsMatchingLastWord(args, arg.toArray(new String[0]));
        }
		return new ArrayList<String>();
    }
    
    abstract private class ThreadRunnable implements Runnable {
    	   
        ICommandSender supply;
        Boolean issue = false;

        ThreadRunnable(ICommandSender supply, Boolean issue) {
            this.supply = supply;
            this.issue = issue;
        }
       
    }

}