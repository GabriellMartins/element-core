package dev.github.gabrielmartins.utils.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Universal ItemCreator, compatible with latest Bukkit versions (1.17+).
 */
@Getter
public class ItemCreator {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemCreator(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemCreator(Material material, String displayName) {
        if (material == Material.PLAYER_HEAD) {
            this.item = new ItemStack(Material.PLAYER_HEAD);
        } else {
            this.item = new ItemStack(material);
        }
        this.meta = item.getItemMeta();
        this.meta.setDisplayName(displayName);
    }

    public ItemCreator setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemCreator setName(String name) {
        this.meta.setDisplayName(name);
        return this;
    }

    public ItemCreator setLore(String... lore) {
        this.meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemCreator setLore(List<String> lore) {
        this.meta.setLore(lore);
        return this;
    }

    public ItemCreator addEnchant(Enchantment enchantment, int level) {
        this.meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemCreator addItemFlags(ItemFlag... flags) {
        this.meta.addItemFlags(flags);
        return this;
    }

    public ItemCreator setDurability(short durability) {
        item.setDurability(durability);
        return this;
    }

    public ItemCreator setSkullOwner(String owner) {
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwner(owner);
            item.setItemMeta(skullMeta);
        }
        return this;
    }

    public ItemCreator setSkullTexture(String textureURL) {
        if (!(meta instanceof SkullMeta skullMeta)) return this;

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        String value = Base64.getEncoder().encodeToString((
                "{textures:{SKIN:{url:\"" + textureURL + "\"}}}"
        ).getBytes());

        profile.getProperties().put("textures", new Property("textures", value));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
            item.setItemMeta(skullMeta);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return this;
    }

    public ItemCreator setSkinProperty(Property property) {
        if (!(meta instanceof SkullMeta skullMeta)) return this;

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", property);

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
            item.setItemMeta(skullMeta);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
