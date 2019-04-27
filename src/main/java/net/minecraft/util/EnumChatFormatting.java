package net.minecraft.util;

public enum EnumChatFormatting
{
    BLACK("BLACK", '0', 0),
    DARK_BLUE("DARK_BLUE", '1', 1),
    DARK_GREEN("DARK_GREEN", '2', 2),
    DARK_AQUA("DARK_AQUA", '3', 3),
    DARK_RED("DARK_RED", '4', 4),
    DARK_PURPLE("DARK_PURPLE", '5', 5),
    GOLD("GOLD", '6', 6),
    GRAY("GRAY", '7', 7),
    DARK_GRAY("DARK_GRAY", '8', 8),
    BLUE("BLUE", '9', 9),
    GREEN("GREEN", 'a', 10),
    AQUA("AQUA", 'b', 11),
    RED("RED", 'c', 12),
    LIGHT_PURPLE("LIGHT_PURPLE", 'd', 13),
    YELLOW("YELLOW", 'e', 14),
    WHITE("WHITE", 'f', 15),
    OBFUSCATED("OBFUSCATED", 'k', true),
    BOLD("BOLD", 'l', true),
    STRIKETHROUGH("STRIKETHROUGH", 'm', true),
    UNDERLINE("UNDERLINE", 'n', true),
    ITALIC("ITALIC", 'o', true),
    RESET("RESET", 'r', -1);

    private final String controlString;

    private EnumChatFormatting(String formattingName, char formattingCodeIn, int colorIndex)
    {
        this(formattingName, formattingCodeIn);
    }

    private EnumChatFormatting(String formattingName, char formattingCodeIn, boolean fancyStylingIn)
    {
        this(formattingName, formattingCodeIn);
    }

    private EnumChatFormatting(String formattingName, char formattingCodeIn)
    {
        this.controlString = "\u00a7" + formattingCodeIn;
    }
    
    public String toString()
    {
        return this.controlString;
    }
}