package me.cuft.portalcodes;

import java.util.List;

public class Code
{
    private final List<String> blocks;
    private final String color;
    private final String title;
    private final String subtitle;
    private final List<String> commands;

    public Code(List<String> blocks, String color, String title, String subtitle, List<String> commands)
    {
        this.blocks = blocks;
        this.color = color;
        this.title = title;
        this.subtitle = subtitle;
        this.commands = commands;
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public String getColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public List<String> getCommands() {
        return commands;
    }
}
