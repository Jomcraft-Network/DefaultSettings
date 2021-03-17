package net.jomcraft.defaultsettings;

import java.io.File;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class CommandSwitchProfile {

	protected static void register(FMLServerStartingEvent event) {
		LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("switchprofile");
		
    	for(File file : FileUtil.getMainFolder().listFiles()) {
    		if(!file.isDirectory() || file.getName().equals("sharedConfigs"))
				continue;
			
			literalargumentbuilder.then(Commands.argument("profile", ProfileArgument.profileArgument()).executes((command) -> {
				return saveProcess(command.getSource(), MessageArgument.getMessage(command, "profile"));
			}));
		}

    	event.getServer().getCommandManager().getDispatcher().register(literalargumentbuilder);
	}

	private static int saveProcess(CommandSource source, ITextComponent iTextComponent) throws CommandSyntaxException {
		String profile = iTextComponent.getString();
	    if(new File(FileUtil.getMainFolder(), profile).exists()) {
	    	if(!FileUtil.privateJson.currentProfile.equals(profile)) {
				
				FileUtil.privateJson.targetProfile = profile;
				source.sendFeedback(new StringTextComponent("\u00a7aThe profile has been queued for change successfully!"), true);
				
				source.sendFeedback(new StringTextComponent("\u00a76To begin using the selected profile, you now need"), true);
				
				source.sendFeedback(new StringTextComponent("\u00a76to restart your game."), true);
				
				FileUtil.privateJson.save();
				
			}else {
				source.sendFeedback(new StringTextComponent("\u00a7cThis profile is already active!"), true);
			}
	    }else {
	    	source.sendFeedback(new StringTextComponent("\u00a7cThat profile does not exist!"), true);
	    }
		return 0;
	}
	
}