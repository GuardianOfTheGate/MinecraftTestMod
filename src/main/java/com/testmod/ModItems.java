package com.testmod;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    //creates itemKey for custom item and registers it
    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings){

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TestMod.MOD_ID, name));
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);


        return item;
    }



    public static final  Stripmine STRIPMINE  = register(
            "stripmine",
            Stripmine::new,
            new Item.Properties()
    );
    public static final  Copier COPIER  = register(
            "copier",
            Copier::new,
            new Item.Properties()
    );
    public static void initialize() {
    }
}
