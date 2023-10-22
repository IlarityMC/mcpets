package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
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

        // upgrade button
        ItemStack item = Items.UPGRADE.getItem();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("§7Upgrade the pet.");

        PetLevel level = pet.getPetStats().getNextLevel();

        if (!level.equals(pet.getPetStats().getCurrentLevel())) {
            lore.add("");
            lore.add("§7Next level: " + level.getLevelName());
            lore.add("§7Cost: " + Utils.hex("#fcae05") + String.format("%,d", (int) level.getExpThreshold()).replace(",", ".") + " §fꐒ");
            lore.add("");
            lore.add(Utils.hex("#1b9ef5→ Click to upgrade!"));
        } else {
            lore.add("");
            lore.add(Utils.hex("#f01800Max level reached!"));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        inventory.setItem(15, item);
        // end upgrade button

        if (GlobalConfig.getInstance().isNameable())
        {
            inventory.setItem(16, Items.RENAME.getItem());
        }
        if (GlobalConfig.getInstance().isMountable() && pet.isMountable())
        {
            inventory.setItem(5, Items.MOUNT.getItem());
        }
        if (!pet.getSignals().isEmpty() && pet.isEnableSignalStickFromMenu())
            inventory.setItem(6, pet.getSignalStick());
        if (pet.getInventorySize() > 0)
        {
            inventory.setItem(10, Items.INVENTORY.getItem());
            inventory.setItem(11, Items.INVENTORY.getItem());
        }

        ItemStack icon = pet.buildItem(Items.petInfo(pet), false, null, null, null, null, 0, null);
        ItemMeta iconMeta = icon.getItemMeta();
        List<String> metaLore = iconMeta.getLore();

        metaLore.set(2, "§7Level: %levelname% §8(§7%damagemodifier%%§8)"
                        .replace("%levelname%", pet.getPetStats().getCurrentLevel().getLevelName())
                        .replace("%damagemodifier%", "+" + (int) (100 * (pet.getPetStats().getCurrentLevel().getDamageModifier() - 1))));

        iconMeta.setLore(metaLore);
        icon.setItemMeta(iconMeta);
        inventory.setItem(13, icon);

        inventory.setItem(8, Items.DESPAWN.getItem());
    }

    public void open(Player p) {
        if(inventory != null)
            p.openInventory(inventory);
    }

}
