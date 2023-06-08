package net.jomcraft.defaultsettings.commands;

import java.io.IOException;
import java.util.ArrayList;
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
import net.jomcraft.defaultsettings.DefaultSettings;
import net.jomcraft.jcplugin.FileUtilNoMC;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigArguments implements ArgumentType<String> {

    private static List<String> ARGUMENTS = Arrays.asList("fml.toml", "forge-client.toml");

    public static ConfigArguments configArguments() {
        return new ConfigArguments();
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        return readQuotedString(reader);
    }

    public String readQuotedString(final StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead()) {
            return "";
        }
        final char next = reader.getString().charAt(reader.getCursor());
        if (!reader.isQuotedStringStart(next)) {

            final int start = reader.getCursor();
            while (reader.canRead()) {
                reader.skip();
            }
            return reader.getString().substring(start, reader.getCursor());
        }
        reader.skip();
        return reader.readStringUntil(next);
    }

    public static String getString(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        try {
            ArrayList<String> filtered = new ArrayList<String>();
            ArrayList<String> prevList = FileUtilNoMC.listConfigFiles();
            for(int i = 0; i < prevList.size(); i++){
                String name = prevList.get(i);
                if(name.contains(" "))
                    name = "\"" + name + "\"";
                filtered.add(name);
            }
            ARGUMENTS = filtered;
        } catch (IOException e) {
            DefaultSettings.log.error(e);
        }
        return SharedSuggestionProvider.suggest(ARGUMENTS, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return ARGUMENTS;
    }

    public static class Info implements ArgumentTypeInfo<ConfigArguments, Info.Template> {
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
        public Template unpack(ConfigArguments argument) {
            return new Template();
        }

        public class Template implements ArgumentTypeInfo.Template<ConfigArguments> {

            Template() {

            }

            @Override
            public ConfigArguments instantiate(CommandBuildContext p_223435_) {
                return new ConfigArguments();
            }

            @Override
            public ArgumentTypeInfo<ConfigArguments, ?> type() {
                return Info.this;
            }
        }
    }
}