package com.github.euonmyoji.newhonor.data;

import com.google.common.reflect.TypeToken;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author MungSoup
 */
@Getter
public class Honor {
    private ClickEvent.Action clickType;
    private HoverEvent.Action hoverType;
    private String clickValue, hoverValue, text;
    private List<String> displayTexts, suffixes;
    private int intervalTick;
    private TextComponent tellraw;
    private ItemStack icon;

    public Honor(CommentedConfigurationNode cfg, String id) {
        Logger logger = Bukkit.getLogger();
        CommentedConfigurationNode config = cfg.getNode(id);
        String defaultValue = "[default]";
        text = color(config.getNode("value").getString(defaultValue));
        tellraw = new TextComponent(text);
        /* click event */
        {
            CommentedConfigurationNode clickNode = config.getNode("clickValue");
            clickValue = color(clickNode.getNode("value").getString(defaultValue));
            String clickTypeString = clickNode.getNode("type").getString("suggest").toUpperCase();
            try {
                clickType = ClickEvent.Action.valueOf(clickTypeString);
                tellraw.setClickEvent(new ClickEvent(clickType, clickValue));
            } catch (IllegalArgumentException e) {
                logger.log(Level.CONFIG, "Error about honor.conf (clickType may be wrong?)");
            }
        }
        /* hover event */
        {
            CommentedConfigurationNode hoverNode = config.getNode("hoverValue");
            hoverValue = color(hoverNode.getNode("value").getString(defaultValue));
            String hoverTypeString = hoverNode.getNode("type").getString("suggest").toUpperCase();
            try {
                hoverType = HoverEvent.Action.valueOf(hoverTypeString);
                tellraw.setHoverEvent(new HoverEvent(hoverType, new ComponentBuilder(hoverValue).create()));
            } catch (IllegalArgumentException e) {
                logger.log(Level.CONFIG, "Error about honor.conf (hoverType may be wrong?)");
            }
        }
        try {
            displayTexts = config.getNode("displayValue").getList(TypeToken.of(String.class), ArrayList::new);
            suffixes = config.getNode("suffixValue").getList(TypeToken.of(String.class), ArrayList::new);
            intervalTick = config.getNode("intervalTicks").getInt(1);
            icon = config.getNode("item-value").getValue(TypeToken.of(ItemStack.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    private String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
