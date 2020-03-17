package de.pt400c.defaultsettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class CommandSwitchProfile_19 extends CommandBase {

    public String getName() {
        return "switchprofile";
    }
    
    public String getUsage(ICommandSender sender) {
        return "/defaultsettings [name]";
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

	public void execute(MinecraftServer server, final ICommandSender sender, String[] args) throws WrongUsageException {
		if (args.length == 0) {
			sender.sendMessage(new TextComponentString("\u00a7c" + getUsage(sender)));
			return;
		}
		
		String profile = String.join(" ", args);
	    if(new File(FileUtil.getMainFolder(), profile).exists()) {
	    	if(!FileUtil.privateJson.currentProfile.equals(profile)) {
				
				FileUtil.privateJson.targetProfile = profile;
				sender.sendMessage(new TextComponentString("\u00a7aThe profile has been queued for change successfully!"));
				
				sender.sendMessage(new TextComponentString("\u00a76To begin using the selected profile, you now need"));
				
				sender.sendMessage(new TextComponentString("\u00a76to restart your game."));
				
				FileUtil.privateJson.save(new File(FileUtil.privateLocation));
				
			}else {
				sender.sendMessage(new TextComponentString("\u00a7cThis profile is already active!"));
			}
	    }else {
	    	sender.sendMessage(new TextComponentString("\u00a7cThat profile does not exist!"));
	    }
	}

    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
    	ArrayList<String> arg = new ArrayList<String>();
    	for(File file : FileUtil.getMainFolder().listFiles()) {
			if(!file.isDirectory() || file.getName().equals("sharedConfigs"))
				continue;

			arg.add(file.getName());

		}

        return getListOfStringsMatchingLastWord(args, arg.toArray(new String[0]));

    }
}