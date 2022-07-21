package net.jomcraft.defaultsettings.commands;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class TypeArguments implements ArgumentType<String> {

	private static final List<String> ARGUMENTS = Arrays.asList("options", "keybinds", "servers");

	public static TypeArguments typeArguments() {
		return new TypeArguments();
	}

	@Override
	public String parse(final StringReader reader) throws CommandSyntaxException {
		return reader.readUnquotedString();
	}

	public static String getString(final CommandContext<?> context, final String name) {
		return context.getArgument(name, String.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(ARGUMENTS, builder);
	}

	@Override
	public Collection<String> getExamples() {
		return ARGUMENTS;
	}

	public static class Info implements ArgumentTypeInfo<TypeArguments, Info.Template> {
		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {

		}

		@Override
		public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			return new Template();
		}

		@Override
		public void serializeToJson(Template template, JsonObject json) {
		}

		@Override
		public Template unpack(TypeArguments argument) {
			return new Template();
		}

		public class Template implements ArgumentTypeInfo.Template<TypeArguments> {

			Template() {

			}

			@Override
			public TypeArguments instantiate(CommandBuildContext p_223435_) {
				return new TypeArguments();
			}

			@Override
			public ArgumentTypeInfo<TypeArguments, ?> type() {
				return Info.this;
			}
		}
	}
}