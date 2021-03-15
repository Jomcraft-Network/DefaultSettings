package net.jomcraft.defaultsettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class CommandSwitchProfile_18 extends CommandBase {

	@Override
    public String getName() {
        return "switchprofile";
    }
	
	@Override
    public String getUsage(ICommandSender sender) {
        return "/switchprofile [name]";
    }
	
	@Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
    public List<String> func_180525_a(ICommandSender sender, String[] args, BlockPos pos) {
    	ArrayList<String> arg = new ArrayList<String>();
    	for(File file : FileUtil.getMainFolder().listFiles()) {
			if(!file.isDirectory() || file.getName().equals("sharedConfigs"))
				continue;

			arg.add(file.getName());

		}

        return getListOfStringsMatchingLastWord(args, arg.toArray(new String[0]));

    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
	}
    
	public void func_71515_b(final ICommandSender sender, String[] args) throws WrongUsageException {
		if (args.length == 0) {
			sender.func_145747_a(new ChatComponentText("\u00a7c" + getUsage(sender)));
			return;
		}
		
		String profile = String.join(" ", args);
	    if(new File(FileUtil.getMainFolder(), profile).exists()) {
	    	if(!FileUtil.privateJson.currentProfile.equals(profile)) {
				
				FileUtil.privateJson.targetProfile = profile;
				sender.func_145747_a(new ChatComponentText("\u00a7aThe profile has been queued for change successfully!"));
				
				sender.func_145747_a(new ChatComponentText("\u00a76To begin using the selected profile, you now need"));
				
				sender.func_145747_a(new ChatComponentText("\u00a76to restart your game."));
				
				FileUtil.privateJson.save();
				
			}else {
				sender.func_145747_a(new ChatComponentText("\u00a7cThis profile is already active!"));
			}
	    }else {
	    	sender.func_145747_a(new ChatComponentText("\u00a7cThat profile does not exist!"));
	    }
	}
}