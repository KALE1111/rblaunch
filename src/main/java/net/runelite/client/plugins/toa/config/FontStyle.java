package net.runelite.client.plugins.toa.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

public @Getter
@AllArgsConstructor
enum FontStyle
{
    BOLD("Bold", Font.BOLD),
    ITALIC("Italic", Font.ITALIC),
    PLAIN("Plain", Font.PLAIN);

    private final String name;
    private final int font;

    @Override
    public String toString()
    {
        return name;
    }
}
