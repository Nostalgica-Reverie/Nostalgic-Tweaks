package mod.adrenix.nostalgic.mixin.tweak.gameplay.combat_item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.gameplay.combat.DamageHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(DiggerItem.class)
public abstract class DiggerItemMixin extends TieredItem
{
    /* Fake Constructor */

    private DiggerItemMixin(Tier tier, Properties properties)
    {
        super(tier, properties);
    }

    /* Unique & Shadows */

    @Shadow @Final @Mutable private Multimap<Attribute, AttributeModifier> defaultModifiers;
    @Unique private Multimap<Attribute, AttributeModifier> nt$defaultModifiers;
    @Unique private boolean nt$isModifierAllowed;

    /* Injections */

    /**
     * Initializes an old damage modifier map to later modify a digger item's damage attribute modifier.
     */
    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void nt_combat_item$onInitDiggerItem(float attackDamageModifier, float attackSpeedModifier, Tier tier, TagKey<Block> blocks, Properties properties, CallbackInfo callback)
    {
        HashMultimap<Attribute, AttributeModifier> builder = HashMultimap.create();

        for (Map.Entry<Attribute, AttributeModifier> entry : this.defaultModifiers.entries())
        {
            if (!entry.getKey().equals(Attributes.ATTACK_DAMAGE))
                builder.put(entry.getKey(), entry.getValue());
        }

        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", DamageHelper.get(this), AttributeModifier.Operation.ADDITION));

        this.nt$defaultModifiers = builder;
        this.nt$isModifierAllowed = DamageHelper.isApplicable(this);
    }

    /**
     * Changes the digger item's default attribute modifier map, so it can use old damage values.
     */
    @ModifyReturnValue(
        method = "getDefaultAttributeModifiers",
        at = @At("RETURN")
    )
    private Multimap<Attribute, AttributeModifier> nt_combat_item$modifyDiggerItemDefaultAttributeModifiers(Multimap<Attribute, AttributeModifier> defaultModifiers, EquipmentSlot slot)
    {
        // @formatter:off
        if (GameplayTweak.OLD_DAMAGE_VALUES.get() && EquipmentSlot.MAINHAND == slot && this.nt$isModifierAllowed && !GameplayTweak.IGNORED_OLD_DAMAGE_ITEMS.get().containsItem(this))
            return this.nt$defaultModifiers;

        // @formatter:on
        return defaultModifiers;
    }
}
