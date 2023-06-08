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

public class OperationArguments implements ArgumentType<String> {

	private static final List<String> ARGUMENTS = Arrays.asList("override", "forceOverride");
	private static final List<String> ARGUMENTS_LIMITED = Arrays.asList("forceOverride");
	private final boolean limited;

	public OperationArguments(boolean limited) {
		this.limited = limited;
	}

	public static OperationArguments operationArguments(boolean limited) {
		return new OperationArguments(limited);
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
		return SharedSuggestionProvider.suggest(this.limited ? ARGUMENTS_LIMITED : ARGUMENTS, builder);
	}

	@Override
	public Collection<String> getExamples() {
		return ARGUMENTS;
	}

	public static class Info implements ArgumentTypeInfo<OperationArguments, Info.Template> {
		@Override
		public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
			buffer.writeBoolean(template.limited);
		}

		@Override
		public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
			boolean limited = buffer.readBoolean();
			return new Template(limited);
		}

		@Override
		public void serializeToJson(Template template, JsonObject json) {
			json.addProperty("limited", template.limited);
		}

		@Override
		public Template unpack(OperationArguments argument) {
			return new Template(argument.limited);
		}

		public class Template implements ArgumentTypeInfo.Template<OperationArguments> {
			final boolean limited;

			Template(boolean limited) {
				this.limited = limited;
			}

			@Override
			public OperationArguments instantiate(CommandBuildContext p_223435_) {
				return new OperationArguments(this.limited);
			}

			@Override
			public ArgumentTypeInfo<OperationArguments, ?> type() {
				return Info.this;
			}
		}
	}
}