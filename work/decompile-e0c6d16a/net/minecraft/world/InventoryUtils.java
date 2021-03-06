package net.minecraft.world;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class InventoryUtils {

    private static final Random RANDOM = new Random();

    public InventoryUtils() {}

    public static void dropContents(World world, BlockPosition blockposition, IInventory iinventory) {
        dropContents(world, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), iinventory);
    }

    public static void dropContents(World world, Entity entity, IInventory iinventory) {
        dropContents(world, entity.getX(), entity.getY(), entity.getZ(), iinventory);
    }

    private static void dropContents(World world, double d0, double d1, double d2, IInventory iinventory) {
        for (int i = 0; i < iinventory.getContainerSize(); ++i) {
            dropItemStack(world, d0, d1, d2, iinventory.getItem(i));
        }

    }

    public static void dropContents(World world, BlockPosition blockposition, NonNullList<ItemStack> nonnulllist) {
        nonnulllist.forEach((itemstack) -> {
            dropItemStack(world, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), itemstack);
        });
    }

    public static void dropItemStack(World world, double d0, double d1, double d2, ItemStack itemstack) {
        double d3 = (double) EntityTypes.ITEM.getWidth();
        double d4 = 1.0D - d3;
        double d5 = d3 / 2.0D;
        double d6 = Math.floor(d0) + InventoryUtils.RANDOM.nextDouble() * d4 + d5;
        double d7 = Math.floor(d1) + InventoryUtils.RANDOM.nextDouble() * d4;
        double d8 = Math.floor(d2) + InventoryUtils.RANDOM.nextDouble() * d4 + d5;

        while (!itemstack.isEmpty()) {
            EntityItem entityitem = new EntityItem(world, d6, d7, d8, itemstack.split(InventoryUtils.RANDOM.nextInt(21) + 10));
            float f = 0.05F;

            entityitem.setDeltaMovement(InventoryUtils.RANDOM.nextGaussian() * 0.05000000074505806D, InventoryUtils.RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D, InventoryUtils.RANDOM.nextGaussian() * 0.05000000074505806D);
            world.addFreshEntity(entityitem);
        }

    }
}
