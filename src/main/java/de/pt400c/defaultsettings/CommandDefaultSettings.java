package de.pt400c.defaultsettings;

import java.io.File;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

public class CommandDefaultSettings {

	protected static void register(FMLServerStartingEvent event) {
		LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("switchprofile");
		
    	for(File leli : FileUtil.getMainFolder().listFiles()) {
			if(!leli.isDirectory())
				continue;
			
			literalargumentbuilder.then(Commands.argument("pos", ProfileArgument.profileArgument()).executes((command) -> {
				return saveProcess(command.getSource(), MessageArgument.getMessage(command, "pos"));
			}));
		}

		event.getCommandDispatcher().register(literalargumentbuilder);
	}

	private static int saveProcess(CommandSource source, ITextComponent iTextComponent) throws CommandSyntaxException {
		String profile = iTextComponent.getFormattedText();
	    if(new File(FileUtil.getMainFolder(), profile).exists()) {
	    	if(!FileUtil.privateJson.currentProfile.equals(profile)) {
				
				FileUtil.privateJson.targetProfile = profile;
				source.sendFeedback(new TextComponentString("\u00a7aThe profile has been queued for change successfully!"), true);
				
				source.sendFeedback(new TextComponentString("\u00a76To begin using the selected profile, you now need"), true);
				
				source.sendFeedback(new TextComponentString("\u00a76to restart your game."), true);
				
				FileUtil.privateJson.save(new File(FileUtil.privateLocation));
				
			}else {
				source.sendFeedback(new TextComponentString("\u00a7cThis profile is already active!"), true);
			}
	    }else {
	    	source.sendFeedback(new TextComponentString("\u00a7cThat profile does not exist!"), true);
	    }
		return 0;
	}
	
}