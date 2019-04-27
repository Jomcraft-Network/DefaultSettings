package de.pt400c.defaultsettings;

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
import net.minecraft.util.EnumChatFormatting;

public class CommandDefaultSettings_18 extends CommandBase {

	public static final ArrayList<String> arg = new ArrayList<String>() {
		private static final long serialVersionUID = -8897230905576922296L;
	{	add("save");	}};
	
	private ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
	@Override
    public String getName() {
        return "defaultsettings";
    }
	
	@Override
    public String getUsage(ICommandSender sender) {
        return "/defaultsettings [save]";
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
    	if(args.length < 2) {
            return getListOfStringsMatchingLastWord(args, arg.toArray(new String[0]));
        }
		return new ArrayList<String>();
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
	}
    
    public void func_71515_b(final ICommandSender sender, String[] args) throws WrongUsageException {
    	if (args.length == 0 || args.length > 1 || !arg.contains(args[0].toLowerCase()))
			throw new WrongUsageException(getUsage(sender));

		if (tpe.getQueue().size() > 0) {
			sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "Please wait until the last request has been processed!"));
			return;
		}

		MutableBoolean issue = new MutableBoolean(false);

		tpe.execute(new ThreadRunnable(sender, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveKeys();
					sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GREEN + "Successfully saved the key configuration"));
					FileUtil.restoreKeys();
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the key configuration:", e);
					sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "Couldn't save the key configuration!"));
					issue.setBoolean(true);
				}
			}
		});

		tpe.execute(new ThreadRunnable(sender, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveOptions();
					sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GREEN + "Successfully saved the default game options"));
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the default game options:", e);
					sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "Couldn't save the default game options!"));
					issue.setBoolean(true);
				}
			}
		});

		tpe.execute(new ThreadRunnable(sender, issue) {
			
			@Override
			public void run() {
				try {
					FileUtil.saveServers();
					sender.func_145747_a(new ChatComponentText(EnumChatFormatting.GREEN + "Successfully saved the server list"));
				} catch (Exception e) {
					DefaultSettings.log.log(Level.ERROR, "An exception occurred while saving the server list:", e);
					sender.func_145747_a(new ChatComponentText(EnumChatFormatting.RED + "Couldn't save the server list!"));
					issue.setBoolean(true);
				}
				
				if (issue.getBoolean())
					sender.func_145747_a(new ChatComponentText(EnumChatFormatting.YELLOW + "Please inspect the log files for further information!"));
			}
		});

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