package de.pt400c.defaultsettings;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.util.text.ITextComponent;

public class ProfileArgument implements ArgumentType<MessageArgument.Message> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Default", "This is a profile");

   public static ProfileArgument profileArgument() {
      return new ProfileArgument();
   }

   public MessageArgument.Message parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return MessageArgument.Message.parse(p_parse_1_, true);
   }
   
   public static ITextComponent getMessage(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
	      return context.getArgument(name, MessageArgument.Message.class).toComponent(context.getSource(), context.getSource().hasPermissionLevel(2));
	   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (!(p_listSuggestions_1_.getSource() instanceof ISuggestionProvider)) {
         return Suggestions.empty();
      } else {

    	  ArrayList<String> add = new ArrayList<String>();
    	  for(File file : FileUtil.getMainFolder().listFiles()) {
  			if(!file.isDirectory())
  				continue;
  			add.add(file.getName());
    	  }
         return suggestIterable((Iterable<String>) add, p_listSuggestions_2_);
      }
   }
   
   static CompletableFuture<Suggestions> suggestIterable(Iterable<String> p_197014_0_, SuggestionsBuilder p_197014_1_) {
	      String s = p_197014_1_.getRemaining().toLowerCase(Locale.ROOT);
	      func_210512_a(p_197014_0_, s, (p_210517_0_) -> {
	         return p_210517_0_;
	      }, (p_210513_1_) -> {
	         p_197014_1_.suggest(p_210513_1_.toString());
	      });
	      return p_197014_1_.buildFuture();
	   }
   
   static <T> void func_210512_a(Iterable<T> p_210512_0_, String p_210512_1_, Function<T, String> p_210512_2_, Consumer<T> p_210512_3_) {
	      for(T t : p_210512_0_) {
	         String s = p_210512_2_.apply(t);
	         if (s.startsWith(p_210512_1_))
	        	 p_210512_3_.accept(t);
	         
	      }

   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}