package com.extendedclip.deluxemenus.requirement;

import com.extendedclip.deluxemenus.DeluxeMenus;
import com.extendedclip.deluxemenus.hooks.ItemHook;
import com.extendedclip.deluxemenus.menu.MenuHolder;
import com.extendedclip.deluxemenus.requirement.wrappers.ItemWrapper;
import com.extendedclip.deluxemenus.utils.StringUtils;
import com.extendedclip.deluxemenus.utils.VersionHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HasItemRequirement extends Requirement {

  private final DeluxeMenus plugin;
  private final ItemWrapper wrapper;
  private final boolean invert;

  public HasItemRequirement(final DeluxeMenus plugin, final ItemWrapper wrapper, final boolean invert) {
    this.plugin = plugin;
    this.wrapper = wrapper;
    this.invert = invert;
  }

  @Override
  public boolean evaluate(MenuHolder holder) {
    String materialName = holder.setPlaceholdersAndArguments(wrapper.getMaterial());
    Material material = DeluxeMenus.MATERIALS.get(materialName.toUpperCase());
    ItemHook pluginHook = null;
    if (material == null) {
      pluginHook = plugin.getItemHooks().values()
              .stream()
              .filter(x -> materialName.toLowerCase().startsWith(x.getPrefix()))
              .findFirst()
              .orElse(null);
      if (pluginHook == null) return invert;
    }

    if (material == Material.AIR) return invert == (holder.getViewer().getInventory().firstEmpty() == -1);

    ItemStack[] armor = wrapper.checkArmor() ? holder.getViewer().getInventory().getArmorContents() : null;
    ItemStack[] offHand = wrapper.checkOffhand() ? holder.getViewer().getInventory().getExtraContents() : null;
    ItemStack[] inventory = holder.getViewer().getInventory().getStorageContents();

    int total = 0;
    for (ItemStack itemToCheck: inventory) {
      if (isNotRequiredItem(itemToCheck, holder, material, pluginHook)) continue;
      total += itemToCheck.getAmount();
    }

    if (offHand != null) {
      for (ItemStack itemToCheck: offHand) {
        if (isNotRequiredItem(itemToCheck, holder, material, pluginHook)) continue;
        total += itemToCheck.getAmount();
      }
    }

    if (armor != null) {
      for (ItemStack itemToCheck: armor) {
        if (isNotRequiredItem(itemToCheck, holder, material, pluginHook)) continue;
        total += itemToCheck.getAmount();
      }
    }

    return invert == (total < wrapper.getAmount());
  }

  private boolean isNotRequiredItem(ItemStack itemToCheck, MenuHolder holder, Material material, ItemHook pluginHook) {
    if (itemToCheck == null || itemToCheck.getType() == Material.AIR) return true;
    ItemMeta meta = itemToCheck.hasItemMeta() ? itemToCheck.getItemMeta() : null;
    if (pluginHook != null) {
      if (!pluginHook.itemMatchesIdentifiers(itemToCheck, holder.setPlaceholdersAndArguments(wrapper.getMaterial().substring(pluginHook.getPrefix().length()))))
        return true;
    } else if (wrapper.getMaterial() != null && itemToCheck.getType() != material) return true;
    if (meta != null) {
      Damageable damageable = meta instanceof Damageable ? (Damageable) meta : null;
      Integer durability = ((damageable != null) && damageable.hasDamage()) ? damageable.getDamage() : null;
      if (wrapper.hasData() && (durability != null && durability != wrapper.getData())) return true;
    }

    if (wrapper.isStrict()) {
      if (meta != null) {
        if (VersionHelper.IS_CUSTOM_MODEL_DATA) {
          if (meta.hasCustomModelData()) return true;
        }
        if (meta.hasLore()) return true;
        return meta.hasDisplayName();
      }

    } else {
      if ((wrapper.getCustomData() != 0 || wrapper.getName() != null || wrapper.getLore() != null) && meta == null)
        return true;

      if (wrapper.getCustomData() != 0) {
        if (VersionHelper.IS_CUSTOM_MODEL_DATA) {
          if (meta != null && !meta.hasCustomModelData()) return true;
          if (meta != null && meta.getCustomModelData() != wrapper.getCustomData()) return true;
        }
      }

      if (meta != null && wrapper.getName() != null) {
        if (!meta.hasDisplayName()) return true;

        String name = MiniMessage.miniMessage().stripTags(holder.setPlaceholdersAndArguments(wrapper.getName()));
        String nameToCheck = meta.hasDisplayName()
                ? MiniMessage.miniMessage().stripTags(
                holder.setPlaceholdersAndArguments(
                        PlainTextComponentSerializer.plainText().serialize(
                                Objects.requireNonNull(meta.displayName()))))
                : itemToCheck.getType().name();

        if (wrapper.checkNameContains() && wrapper.checkNameIgnoreCase()) {
          if (!org.apache.commons.lang3.StringUtils.containsIgnoreCase(nameToCheck, name)) return true;
        }
        else if (wrapper.checkNameContains()) {
          if (!nameToCheck.contains(name)) return true;
        }
        else if (wrapper.checkNameIgnoreCase()) {
          if (!nameToCheck.equalsIgnoreCase(name)) return true;
        }
        else if (!nameToCheck.equals(name)) {
          return true;
        }
      }

      List<Component> loreX;
      if (wrapper.getLoreList() != null) {
        loreX = meta != null && meta.hasLore() ? meta.lore() : null;
        if (loreX == null) return true;


        List<Component> lore = wrapper.getLoreList();
        List<Component> loreToCheck = loreX;

        if (wrapper.checkLoreContains() && wrapper.checkLoreIgnoreCase()) {
          if (loreToCheck.stream().noneMatch(lore::contains)) return true;
        }
        else if (wrapper.checkLoreContains()) {
          if (lore.isEmpty()) return true;
          if (loreToCheck.stream().noneMatch(lore::contains)) return true;
        }

        else if (wrapper.checkLoreIgnoreCase()) {
          if (loreToCheck.stream().noneMatch(lore::contains)) return true;
        }
        else if (!loreToCheck.equals(lore)) {
          return true;
        }
      }

      if (wrapper.getLore() != null) {
        loreX = meta != null && meta.hasLore() ? meta.lore() : null;
        if (loreX == null) return true;

        List<Component> lore = wrapper.getLoreList().stream()
                .map(loreLine -> transformLore(MiniMessage.miniMessage().serialize(loreLine).toLowerCase(), holder))
                .collect(Collectors.toList());

        List<Component> loreToCheck = loreX.stream()
                .map(loreLine -> transformLore(MiniMessage.miniMessage().serialize(loreLine).toLowerCase(), holder))
                .collect(Collectors.toList());

        if (wrapper.checkLoreContains() && wrapper.checkLoreIgnoreCase()) {
          return loreToCheck.stream().noneMatch(lore::contains);
        }
        else if (wrapper.checkLoreContains()) {
          return loreToCheck.stream().noneMatch(lore::contains);
        }
        else if (wrapper.checkLoreIgnoreCase()) {
          return loreToCheck.stream().noneMatch(lore::contains);
        } else return !loreToCheck.equals(lore);
      }
    }
    return false;
  }

  private Component transformLore(String loreString, MenuHolder holder) {
    String processedString = holder.setPlaceholdersAndArguments(loreString);
    return StringUtils.color(processedString);
  }

}
