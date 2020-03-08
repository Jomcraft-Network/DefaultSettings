package de.pt400c.defaultsettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.google.common.base.Joiner;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;
public class CommandSwitchProfiles extends CommandBase {

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
			sender.sendChatToPlayer(ChatMessageComponent.createFromText(EnumChatFormatting.RED + getCommandUsage(sender)));
			return;
		}
		
		String profile = Joiner.on(" ").join(args);
	    if(new File(FileUtil.getMainFolder(), profile).exists()) {
	    	if(!FileUtil.privateJson.currentProfile.equals(profile)) {
				
				FileUtil.privateJson.targetProfile = profile;
				sender.sendChatToPlayer(ChatMessageComponent.createFromText("\u00a7aThe profile has been queued for change successfully!"));
				
				sender.sendChatToPlayer(ChatMessageComponent.createFromText("\u00a76To begin using the selected profile, you now need"));
				
				sender.sendChatToPlayer(ChatMessageComponent.createFromText("\u00a76to restart your game."));
				
				FileUtil.privateJson.save(new File(FileUtil.privateLocation));
				
			}else {
				sender.sendChatToPlayer(ChatMessageComponent.createFromText("\u00a7cThis profile is already active!"));
			}
	    }else {
	    	sender.sendChatToPlayer(ChatMessageComponent.createFromText("\u00a7cThat profile does not exist!"));
	    }
	}

    @SuppressWarnings("unchecked")
	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
    	ArrayList<String> arg = new ArrayList<String>();
    	for(File leli : FileUtil.getMainFolder().listFiles()) {
			if(!leli.isDirectory())
				continue;

			arg.add(leli.getName());

		}

        return getListOfStringsMatchingLastWord(args, arg.toArray(new String[0]));
    }
}