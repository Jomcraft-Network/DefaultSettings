package net.jomcraft.defaultsettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandSwitchProfile extends CommandBase {

    @Override
    public String getCommandName() {
        return "switchprofile";
    }
    
    @Override
    public String getCommandUsage(ICommandSender sender) {
    	return "/switchprofile [name]";
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
    	if (args.length == 0) {
    		sender.addChatMessage(new ChatComponentText("\u00a7c" + getCommandUsage(sender)));
			return;
		}
		
		String profile = String.join(" ", args);
	    if(new File(FileUtil.getMainFolder(), profile).exists()) {
	    	if(!FileUtil.privateJson.currentProfile.equals(profile)) {
				
				FileUtil.privateJson.targetProfile = profile;
				sender.addChatMessage(new ChatComponentText("\u00a7aThe profile has been queued for change successfully!"));
				
				sender.addChatMessage(new ChatComponentText("\u00a76To begin using the selected profile, you now need"));
				
				sender.addChatMessage(new ChatComponentText("\u00a76to restart your game."));
				
				FileUtil.privateJson.save();
				
			}else {
				sender.addChatMessage(new ChatComponentText("\u00a7cThis profile is already active!"));
			}
	    }else {
	    	sender.addChatMessage(new ChatComponentText("\u00a7cThat profile does not exist!"));
	    }

	}

    @SuppressWarnings("unchecked")
	@Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
    	ArrayList<String> arg = new ArrayList<String>();
    	for(File file : FileUtil.getMainFolder().listFiles()) {
			if(!file.isDirectory() || file.getName().equals("sharedConfigs"))
				continue;

			arg.add(file.getName());

		}

        return getListOfStringsMatchingLastWord(args, arg.toArray(new String[0]));
    }
}