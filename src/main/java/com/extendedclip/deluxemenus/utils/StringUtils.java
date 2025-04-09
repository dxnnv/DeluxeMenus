package com.extendedclip.deluxemenus.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public class StringUtils {

    private static final GsonComponentSerializer gson = GsonComponentSerializer.gson();
    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand().toBuilder().hexColors().build();

    /**
     * Converts the provided input string containing MiniMessage formatted text into a deserialized {@link Component}.
     *
     * @param input The input string containing MiniMessage formatted text. Must not be null.
     * @return The deserialized {@link Component} representation of the input string.
     */
    @NotNull
    public static Component color(@NotNull String input) {
        return miniMessage().deserialize(input).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Serializes a string containing text into the MiniMessage format,
     * then deserializes the component back into a MiniMessage string.
     *
     * @param input The text input to be serialized into MiniMessage. Must not be null.
     * @return A MiniMessage-formatted string representation of the input text.
     */
    public static String serializeMiniMessage(String input) {
        TextComponent component = legacySerializer.deserialize(input);
        return miniMessage().serialize(component);
    }

    /**
     * Sends a JSON-formatted message to the given {@link CommandSender}.
     *
     * @param sender The {@link CommandSender} to whom the JSON-formatted message should be sent.
     * @param json The JSON-formatted string message to be sent.
     */
    public static void sendJson(CommandSender sender, String json) {
        sender.sendMessage(fromJson(json));
    }

    /**
     * Deserializes the given JSON-formatted string into a {@link Component} object.
     *
     * @param json The JSON-formatted string to deserialize. Must not be null.
     * @return The deserialized {@link Component} object represented by the input JSON string.
     */
    public static Component fromJson(String json) {
        return gson.deserialize(json);
    }

    @NotNull
    public static String replacePlaceholdersAndArguments(@NotNull String input, final @Nullable Map<String, String> arguments,
                                                         final @Nullable Player player,
                                                         final boolean parsePlaceholdersInsideArguments,
                                                         final boolean parsePlaceholdersAfterArguments) {
        if (player == null) {
            return replaceArguments(input, arguments, null, parsePlaceholdersInsideArguments);
        }

        if (parsePlaceholdersAfterArguments) {
            return replacePlaceholders(replaceArguments(input, arguments, player, parsePlaceholdersInsideArguments), player);
        }

        return replaceArguments(replacePlaceholders(input, player), arguments, player, parsePlaceholdersInsideArguments);
    }

    @NotNull
    public static String replacePlaceholders(final @NotNull String input, final @NotNull Player player) {
        return PlaceholderAPI.setPlaceholders(player, input);
    }

    @NotNull
    public static String replaceArguments(@NotNull String input, final @Nullable Map<String, String> arguments,
                                          final @Nullable Player player, boolean parsePlaceholdersInsideArguments) {
        if (arguments == null || arguments.isEmpty()) {
            return input;
        }

        for (final Map.Entry<String, String> entry : arguments.entrySet()) {
            final String value = player != null && parsePlaceholdersInsideArguments
                    ? replacePlaceholders(entry.getValue(), player)
                    : entry.getValue();
            input = input.replace("{" + entry.getKey() + "}", value);
        }

        return input;
    }
}
