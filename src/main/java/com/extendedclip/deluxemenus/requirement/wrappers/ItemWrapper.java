package com.extendedclip.deluxemenus.requirement.wrappers;

import net.kyori.adventure.text.Component;

import java.util.List;

public class ItemWrapper {

    private String material = null;
    private String name = null;
    private String lore = null;
    private List<Component> loreList = null;

    private short data = 0;
    private boolean hasData = false;
    private int customData = 0;
    private int amount = 1;

    private boolean strict = false;
    private boolean armor = false;
    private boolean offhand = false;
    private boolean nameContains = false;
    private boolean nameIgnoreCase = false;
    private boolean loreContains = false;
    private boolean loreIgnoreCase = false;

    public ItemWrapper() {}

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public List<Component> getLoreList() {
        return loreList;
    }

    public void setLoreList(List<Component> loreList) {
        this.loreList = loreList;
    }

    public short getData() {
        return data;
    }

    public void setData(short data) {
        this.data = data;
    }

    public boolean hasData() {
        return hasData;
    }

    public void hasData(boolean hasData) {
        this.hasData = hasData;
    }

    public int getCustomData() {
        return customData;
    }

    public void setCustomData(int customData) {
        this.customData = customData;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public boolean checkArmor() {
        return armor;
    }

    public void setArmor(boolean armor) {
        this.armor = armor;
    }

    public boolean checkOffhand() {
        return offhand;
    }

    public void setOffhand(boolean offhand) {
        this.offhand = offhand;
    }

    public boolean checkNameContains() {
        return nameContains;
    }

    public void setNameContains(boolean nameContains) {
        this.nameContains = nameContains;
    }

    public boolean checkNameIgnoreCase() {
        return nameIgnoreCase;
    }

    public void setNameIgnoreCase(boolean nameIgnoreCase) {
        this.nameIgnoreCase = nameIgnoreCase;
    }

    public boolean checkLoreContains() {
        return loreContains;
    }

    public void setLoreContains(boolean loreContains) {
        this.loreContains = loreContains;
    }

    public boolean checkLoreIgnoreCase() {
        return loreIgnoreCase;
    }

    public void setLoreIgnoreCase(boolean loreIgnoreCase) {
        this.loreIgnoreCase = loreIgnoreCase;
    }
}
