package net.snofox.minecraft.weedwacker;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
}
