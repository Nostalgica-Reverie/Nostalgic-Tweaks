package mod.adrenix.nostalgic.tweak.config;

import mod.adrenix.nostalgic.tweak.TweakAlert;
import mod.adrenix.nostalgic.tweak.container.group.AnimationGroup;
import mod.adrenix.nostalgic.tweak.factory.TweakFlag;
import mod.adrenix.nostalgic.tweak.factory.TweakNumber;
import mod.adrenix.nostalgic.tweak.gui.SliderType;

// @formatter:off
public interface AnimationTweak
{
    // Arm

    TweakFlag PREVENT_ARM_SWAY = TweakFlag.client(true, AnimationGroup.ARM_SWAY).build();
    TweakFlag ARM_SWAY_MIRROR = TweakFlag.client(false, AnimationGroup.ARM_SWAY).alert(TweakAlert.ARM_SWAY_CONFLICT).build();
    TweakNumber<Integer> ARM_SWAY_INTENSITY = TweakNumber.client(100, AnimationGroup.ARM_SWAY).alert(TweakAlert.ARM_SWAY_CONFLICT).slider(0, 300, 10, SliderType.INTENSITY).build();

    TweakFlag OLD_SWING = TweakFlag.client(true, AnimationGroup.ARM_SWING).build();
    TweakFlag OLD_SWING_INTERRUPT = TweakFlag.client(true, AnimationGroup.ARM_SWING).build();
    TweakFlag OLD_SWING_DROPPING = TweakFlag.client(true, AnimationGroup.ARM_SWING).build();
    TweakFlag OLD_CLASSIC_ATTACK_SWING = TweakFlag.client(false, AnimationGroup.ARM_SWING).build();
    TweakFlag OLD_CLASSIC_USE_SWING = TweakFlag.client(false, AnimationGroup.ARM_SWING).build();

    // Item

    TweakFlag OLD_ITEM_COOLDOWN = TweakFlag.client(true, AnimationGroup.ITEM).build();
    TweakFlag OLD_ITEM_REEQUIP = TweakFlag.client(true, AnimationGroup.ITEM).build();
    TweakFlag OLD_TOOL_EXPLOSION = TweakFlag.client(true, AnimationGroup.ITEM).build();

    // Mob

    TweakFlag OLD_ZOMBIE_ARMS = TweakFlag.client(true, AnimationGroup.MOB).build();
    TweakFlag OLD_SKELETON_ARMS = TweakFlag.client(true, AnimationGroup.MOB).build();
    TweakFlag OLD_GHAST_CHARGING = TweakFlag.client(true, AnimationGroup.MOB).build();
    TweakFlag OLD_MOB_DEATH_TOPPLE = TweakFlag.client(true, AnimationGroup.MOB).build();
    TweakFlag OLD_MOB_HEAD_BODY_TURN = TweakFlag.client(true, AnimationGroup.MOB).build();

    // Boat

    TweakFlag HIDE_BOAT_ROWING = TweakFlag.client(true, AnimationGroup.BOAT).build();

    // Player

    TweakFlag OLD_CLASSIC_WALK_BOBBING = TweakFlag.client(false, AnimationGroup.PLAYER).build();
    TweakFlag OLD_CLASSIC_WALK_ARMS = TweakFlag.client(false, AnimationGroup.PLAYER).build();
    TweakFlag OLD_BACKWARD_WALKING = TweakFlag.client(true, AnimationGroup.PLAYER).build();
    TweakFlag OLD_VERTICAL_BOBBING = TweakFlag.client(true, AnimationGroup.PLAYER).build();
    TweakFlag OLD_CREATIVE_CROUCH = TweakFlag.dynamic(true, AnimationGroup.PLAYER).build();
    TweakFlag OLD_SNEAKING = TweakFlag.client(true, AnimationGroup.PLAYER).build();
    TweakFlag OLD_RANDOM_DAMAGE = TweakFlag.client(true, AnimationGroup.PLAYER).build();
    TweakFlag PREVENT_DEATH_TOPPLE = TweakFlag.client(true, AnimationGroup.PLAYER).build();
}
