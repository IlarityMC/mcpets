package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class PetInteractionMenu {

    @Getter
    private static final String title = Language.INVENTORY_PETS_MENU_INTERACTIONS.getMessage();

    @Getter
    private final Inventory inventory;

    public PetInteractionMenu(Pet pet, UUID owner) {
        // If the taming is incomplete then there is no pet menu available
        if(pet.getTamingProgress() < 1)
        {
            inventory = null;
            return;
        }
        pet.setOwner(owner);
        inventory = Bukkit.createInventory(null, 27, title);

        if (GlobalConfig.getInstance().isActivateBackMenuIcon())
            inventory.setItem(0, Items.PETMENU.getItem());
        if (pet.hasSkins())
            inventory.setItem(2, Items.SKINS.getItem());
        if (GlobalConfig.getInstance().isNameable())
        {
            inventory.setItem(10, Items.RENAME.getItem());
            inventory.setItem(11, Items.RENAME.getItem());
        }
        if (GlobalConfig.getInstance().isMountable() && pet.isMountable())
        {
            inventory.setItem(5, Items.MOUNT.getItem());
        }
        if (!pet.getSignals().isEmpty() && pet.isEnableSignalStickFromMenu())
            inventory.setItem(6, pet.getSignalStick());
        if (pet.getInventorySize() > 0)
        {
            inventory.setItem(15, Items.INVENTORY.getItem());
            inventory.setItem(16, Items.INVENTORY.getItem());
        }

        inventory.setItem(13, pet.buildItem(Items.petInfo(pet), true, null, null, null, null, 0, null));

        inventory.setItem(8, Items.DESPAWN.getItem());
    }

    public void open(Player p) {
        if(inventory != null)
            p.openInventory(inventory);
    }

}
