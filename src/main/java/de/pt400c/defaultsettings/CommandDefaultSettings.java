package de.pt400c.defaultsettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

public class CommandDefaultSettings extends CommandBase {

	public static final ArrayList<String> arg = new ArrayList<String>() {
		private static final long serialVersionUID = 9131616853614902481L;
	{	add("save");	add("export-mode"); }};
	private ThreadPoolExecutor tpe = new ThreadPoolExecutor(1, 3, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
    @Override
    public String getCommandName() {
        return "defaultsettings";
    }
    
    @Override
    public List<String> getCommandAliases() {
    	return new ArrayList<String>() {
			private static final long serialVersionUID = -6975657557521097820L;
		{	add("ds");	}};
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
    	return "/defaultsettings [save / export-mode]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
    @Override
    public int compareTo(Object o) {
    	return 0;
    }

    @Override
    public void processCommand(final ICommandSender sender, String[] args) {
    	if (args.length == 0 || args.length > 2 || !arg.contains(args[0].toLowerCase())) {
    		sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.RED + getCommandUsage(sender)));
			return;	
    	}

		if (tpe.getQueue().size() > 0) {
			sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.RED + "Please wait until the last request has been processed!"));
			return;
		}
		
		if (args[0].toLowerCase().equals("save")) {
		
		if((FileUtil.keysFileExist() || FileUtil.optionsFilesExist() || FileUtil.serversFileExists()) && (args.length == 1 || (args.length == 2 && !args[1].equals("-o")))) {
			sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.RED + "The intended files already exist! If you want to"));
			sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.RED + "overwrite them, add the '-o' argument"));
			return;
		}

		MutableBoolean issue = new MutableBoolean(false);

		tpe.execute(new ThreadRunnable(sender, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveKeys();
					sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.GREEN + "Successfully saved the key configuration"));
					FileUtil.restoreKeys();
				} catch (Exception e) {
					DefaultSettings.log.log(Level.SEVERE, "An exception occurred while saving the key configuration:", e);
					sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.RED + "Couldn't save the key configuration!"));
					issue.setBoolean(true);
				}
			}
		});

		tpe.execute(new ThreadRunnable(sender, issue) {

			@Override
			public void run() {
				try {
					FileUtil.saveOptions();
					sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.GREEN + "Successfully saved the default game options"));
				} catch (Exception e) {
					DefaultSettings.log.log(Level.SEVERE, "An exception occurred while saving the default game options:", e);
					sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.RED + "Couldn't save the default game options!"));
					issue.setBoolean(true);
				}
			}
		});

		tpe.execute(new ThreadRunnable(sender, issue) {
			
			@Override
			public void run() {
				try {
					FileUtil.saveServers();
					sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.GREEN + "Successfully saved the server list"));
				} catch (Exception e) {
					DefaultSettings.log.log(Level.SEVERE, "An exception occurred while saving the server list:", e);
					sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.RED + "Couldn't save the server list!"));
					issue.setBoolean(true);
				}
				
				if (issue.getBoolean())
					sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.YELLOW + "Please inspect the log files for further information!"));
			}
		});
		}else {
			final boolean exportMode = FileUtil.exportMode();
			tpe.execute(new ThreadRunnable(sender, null) {

				@SuppressWarnings("static-access")
				@Override
				public void run() {
					try {
						if (exportMode) {
							FileUtil.restoreConfigs();
							sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.GREEN + "The export-mode has been disabled successfully"));
						} else {
							FileUtil.moveAllConfigs();
							sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.GREEN + "Successfully activated the export-mode"));
						}
					} catch (IOException e) {
						DefaultSettings.getInstance().log.log(Level.SEVERE, "An exception occurred while trying to move the configs:", e);
						sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.RED + "Couldn't switch the export-mode"));
					}
				}
			});

		}

	}

    @SuppressWarnings("unchecked")
	@Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
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