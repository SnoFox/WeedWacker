package net.snofox.minecraft.weedwacker;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class WeedWacker extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockSpread(final BlockSpreadEvent ev) {
        switch(ev.getNewState().getType()) {
            case VINE:
            case WEEPING_VINES:
            case KELP:
            case TWISTING_VINES:
                ev.setCancelled(!isBoneBase(ev.getNewState()));
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent ev) {
        if(!(ev.getAction().equals(Action.RIGHT_CLICK_BLOCK) && Objects.requireNonNull(ev.getHand()).equals(EquipmentSlot.HAND))) return;
        final ItemStack handItem = ev.getItem();
        if(handItem == null) return;
        if(!handItem.getType().equals(Material.BONE_MEAL)) return;
        final Block clickedBlock = ev.getClickedBlock();
        if(!Objects.requireNonNull(clickedBlock).getType().equals(Material.VINE)) return;
        if(growVine(clickedBlock)) {
            updateInventory(ev.getPlayer(), handItem);
            clickedBlock.getWorld().playEffect(clickedBlock.getLocation(), Effect.VILLAGER_PLANT_GROW, 15);
        }
    }

    private void updateInventory(final Player player, final ItemStack hand) {
        if(player.getGameMode().equals(GameMode.CREATIVE)) return;
        if(hand.getAmount() == 1) hand.setType(Material.AIR);
        else hand.setAmount(hand.getAmount()-1);
        player.getInventory().setItemInMainHand(hand);
    }

    private boolean isBoneBase(final BlockState block) {
        switch(block.getType()) {
            case VINE:
            case WEEPING_VINES:
                return findBone(block, BlockFace.UP);
            case KELP:
            case TWISTING_VINES:
                return findBone(block, BlockFace.DOWN);
        }
        return true;
    }

    private boolean findBone(final BlockState block, final BlockFace searchDir) {
        final Material growingType = block.getType();
        final Location searchLoc = block.getLocation();
        for(;;) {
            searchLoc.add(searchDir.getDirection());
            final Material foundType = searchLoc.getBlock().getType();
            if(foundType.equals(growingType)) continue;
            return foundType.equals(Material.BONE_BLOCK);
        }
    }

    private boolean growVine(final Block block) {
        for(int searchY = 1; searchY <= block.getY() - block.getWorld().getMinHeight(); ++searchY) {
            final Block searchBlock = block.getRelative(0, -searchY, 0);
            if(searchBlock.getType().equals(Material.VINE)) continue;
            if(searchBlock.isEmpty()) {
                searchBlock.setType(Material.VINE);
                searchBlock.setBlockData(searchBlock.getRelative(BlockFace.UP).getBlockData());
                return true;
            }
            break;
        }
        return false;
    }

}
