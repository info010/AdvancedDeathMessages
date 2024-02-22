package org.info_0.advanceddeathmessages.chat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HoverTransforms {
    private final HoverTransformers transformers;
    private final String original;
    private final ItemStack item;

    public HoverTransforms(String original, ItemStack item) {
        this.transformers = HoverTransformers.getTransforms(item);
        this.original = original;
        this.item = item;
    }

    public BaseComponent[] transform(String message) {
        return transformers.transform(message, original, item);
    }
}

interface Transform {
    BaseComponent[] transform(String message, String original, ItemStack item);
}

enum HoverTransformers {
    NONE((message, original, item) -> createBaseComponent(message)),
    @SuppressWarnings("deprecation")
    ORIGINAL_ON_HOVER((message, original, item) -> {
        BaseComponent[] component = createBaseComponent(message);
        setHoverEvent(component, new HoverEvent(HoverEvent.Action.SHOW_TEXT, createBaseComponent(original)));
        return component;
    }),
    ITEM_ON_HOVER((message, original, item) -> populateKillWeapon(message, null, item)),
    ORIGINAL_AND_ITEM_ON_HOVER(HoverTransformers::populateKillWeapon);

    @SuppressWarnings("deprecation") // Backwards compatibility with old BaseComponent API
    private static BaseComponent[] populateKillWeapon(String message, String original, ItemStack item) {
        List<BaseComponent> component = new ArrayList<>();
        BaseComponent[] hoverItem = null;
        BaseComponent[] originalHover = original == null ? null : createBaseComponent(original);
        String[] split = message.split("%weapon%");
        boolean endWithItem = message.endsWith("%weapon%");


        for (int i = 0; i < split.length; i++) {
            BaseComponent[] chunk = createBaseComponent(split[i]);

            if (originalHover != null) {
                setHoverEvent(chunk, new HoverEvent(HoverEvent.Action.SHOW_TEXT, originalHover));
            }

            addExtra(component, chunk);

            if (i != split.length - 1 || endWithItem) {
                if (hoverItem == null) {
                    hoverItem = ItemSerializer.serializeItemStack(item);
                }

                addExtra(component, hoverItem);
            }
        }

        return createBaseComponent(component);
    }

    private final Transform transform;

    HoverTransformers(Transform transform) {
        this.transform = transform;
    }

    public BaseComponent[] transform(String message, String original, ItemStack item) {
        return transform.transform(message, original, item);
    }

    public static HoverTransformers getTransforms(ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            return HoverTransformers.ITEM_ON_HOVER;
        } else {
            return HoverTransformers.NONE;
        }
    }

    public static BaseComponent[] createBaseComponent(String message) {
        return TextComponent.fromLegacyText(message);
    }

    public static void setHoverEvent(BaseComponent[] component, HoverEvent hoverEvent) {
        for (BaseComponent baseComponent : component) {
            baseComponent.setHoverEvent(hoverEvent);
        }
    }

    private static BaseComponent[] createBaseComponent(List<BaseComponent> component) {
        return component.toArray(new BaseComponent[0]);
    }

    private static void addExtra(List<BaseComponent> component, BaseComponent[] extra) {
        component.addAll(Arrays.asList(extra));
    }
}
