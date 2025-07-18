package mod.adrenix.nostalgic.neoforge.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.helper.candy.ItemHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.ArrayList;

@EventBusSubscriber(modid = NostalgicTweaks.MOD_ID)
public abstract class CommonEventHandler
{
    /**
     * Handles splitting up item drops from blocks based on tweak context. Priority is set to highest so other mods
     * later on will get the updated drops list.
     *
     * @param event The {@link BlockDropsEvent} instance.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void splitBlockDrops(BlockDropsEvent event)
    {
        if (!CandyTweak.OLD_ITEM_MERGING.get())
            return;

        ArrayList<ItemEntity> drops = new ArrayList<>();

        for (ItemEntity entity : event.getDrops())
            ItemHelper.splitEntity(event.getLevel(), entity, drops::add);

        event.getDrops().addAll(drops);
    }
}
