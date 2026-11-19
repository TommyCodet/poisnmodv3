package net.poisonpulse.poisn.price;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.poisonpulse.poisn.util.Fmt;

import java.util.List;

public final class PriceTooltip {
    private PriceTooltip() {}

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> append(stack, lines));
    }

    private static void append(ItemStack stack, List<Component> lines) {
        if (stack == null || stack.isEmpty() || lines == null) return;

        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        Double[] prices = PriceManager.get(path);

        // Also try without namespace for safety
        if (prices == null) {
            String full = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            if (full.startsWith("minecraft:")) {
                prices = PriceManager.get(full.substring("minecraft:".length()));
            }
        }

        if (prices == null) return;

        Double ah = prices.length > 0 ? prices[0] : null;
        Double orders = prices.length > 1 ? prices[1] : null;
        int count = Math.max(1, stack.getCount());

        if (ah != null) {
            lines.add(makeLine("AH: ", ah, count));
        }
        if (orders != null) {
            lines.add(makeLine("Orders: ", orders, count));
        }
    }

    private static MutableComponent makeLine(String label, double unit, int count) {
        MutableComponent c = Component.literal(label).withStyle(ChatFormatting.GRAY);
        c.append(Component.literal(Fmt.money(unit)).withStyle(ChatFormatting.GOLD));
        if (count > 1) {
            c.append(Component.literal(" (" + Fmt.money(unit * count) + " total)")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        return c;
    }
}
