package org.info_0.advanceddeathmessages.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.info_0.advanceddeathmessages.util.MessagesUtil;

public class CreateGui {

    private Player player;

    public CreateGui(Player player){
        this.player = player;
    }

//    private ItemStack guiItem(){
//
//    }

//    private Inventory newGui(){
//        Inventory inv = Bukkit.createInventory(null,36,"ADM Gui");
//
//    }

    private void initializeItems(){
        for(String string : MessagesUtil.getPaths()){

        }
    }

}
