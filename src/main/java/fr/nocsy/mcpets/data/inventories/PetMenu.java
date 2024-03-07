package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.sql.PlayerData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PetMenu {

    @Getter
    private static final String title = Language.INVENTORY_PETS_MENU.getMessage();

    @Getter
    private final Inventory inventory;

    @Getter
    private final UUID owner;

    public PetMenu(Player p, int page, boolean addPager) {
        // Load the data from the player
        // Mainly for the pet stats
        PlayerData.get(p.getUniqueId());
        owner = p.getUniqueId();

        List<Pet> availablePets = Pet.getAvailablePets(p);

        List<Pet> allPets = Pet.getObjectPets();
        List<Pet> lockedPets = new ArrayList<>();

        for (Pet pet : allPets) {
            if (!pet.has(p)) {
                lockedPets.add(pet);
            }
        }

        System.out.println("available pets: " + availablePets.size());
        System.out.println("locked pets: " + lockedPets.size());

        int invSize = GlobalConfig.getInstance().getAdaptiveInventory();

        inventory = Bukkit.createInventory(null, invSize, title);

        for (int i = page * 53; i < invSize + page * 53; i++) {
            if (i >= availablePets.size()) {
                System.out.println("i: " + i + " available size: " + availablePets.size());
                // add locked icon, check if enough
                int lockedIndex = i - availablePets.size();
                if(lockedIndex >= lockedPets.size())
                    continue;
                System.out.println("locked index: " + lockedIndex);
                Pet pet = lockedPets.get(lockedIndex);

                ItemStack item = pet.buildItem(pet.getIcon(), false, null, null, null, null, 0, null);

                ItemStack lock = new ItemStack(Material.PAPER);
                ItemMeta lockMeta = lock.getItemMeta();
                lockMeta.setCustomModelData(1000);
                lockMeta.setDisplayName(item.getItemMeta().getDisplayName());
                List<String> lore = item.getItemMeta().getLore();
                lore.removeIf(line -> line.contains("%level%"));
                lockMeta.setLore(lore);
                lock.setItemMeta(lockMeta);

                inventory.setItem(inventory.firstEmpty(), lock);
                continue;
            }
            Pet pet = availablePets.get(i);

            ItemStack item = pet.buildItem(pet.getIcon(), false, null, null, null, null, 0, null);
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            lore.replaceAll(line -> {
                return line.replace("%damagemodifier%", "+" + (int) (100 * (pet.getPetStats().getCurrentLevel().getDamageModifier() - 1)))
                        .replace("%level%", pet.getPetStats().getCurrentLevel().getLevelName());
            });
            meta.setLore(lore);
            item.setItemMeta(meta);

            inventory.addItem(item);

        }

        if (addPager) {
            inventory.setItem(invSize - 1, Items.page(page, p));
        }

    }

    public void open(Player p) {
        if(p.getUniqueId().equals(owner) && Category.getCategories().size() > 0)
        {
            CategoriesMenu.open(p);
            return;
        }
        p.openInventory(inventory);
    }

}
