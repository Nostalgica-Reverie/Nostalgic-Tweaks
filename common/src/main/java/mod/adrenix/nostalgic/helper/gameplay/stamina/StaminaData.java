package mod.adrenix.nostalgic.helper.gameplay.stamina;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.tweak.enums.StaminaRegain;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class StaminaData
{
    /* Constants */

    public static final int MAX_STAMINA_LEVEL = 20;

    /* Fields */

    private boolean tickAgain = false;
    private boolean isExhausted = false;
    private int durationInTicks = 0;
    private int rechargeInTicks = 0;
    private int cooldownInTicks = 0;
    private int halfRateInTicks = 0;
    private int waitTimer = 0;
    private int tickTimer = 0;
    private int staminaLevel = MAX_STAMINA_LEVEL;

    /* Constructor */

    public StaminaData()
    {
        this.syncTickTimers();
    }

    /* Methods */

    /**
     * Redefine the duration and recharge timers based on current tweak context.
     */
    public void syncTickTimers()
    {
        int durationFromTweak = GameplayTweak.STAMINA_DURATION.get() * 20;
        int rechargeFromTweak = GameplayTweak.STAMINA_RECHARGE.get() * 20;
        int cooldownFromTweak = GameplayTweak.STAMINA_COOLDOWN.get() * 20;

        if (this.durationInTicks != durationFromTweak)
        {
            this.durationInTicks = durationFromTweak;
            this.tickTimer = durationFromTweak;
            this.waitTimer = 0;
            this.halfRateInTicks = 0;
        }

        if (this.rechargeInTicks != rechargeFromTweak)
            this.rechargeInTicks = rechargeFromTweak;

        if (this.cooldownInTicks != cooldownFromTweak)
            this.cooldownInTicks = cooldownFromTweak;
    }

    /**
     * Update stamina data every tick.
     *
     * @param player The {@link Player} instance this data is associated with.
     */
    public void tick(Player player)
    {
        Difficulty difficulty = player.level().getDifficulty();
        boolean isSprinting = player.isSprinting();
        boolean canTick = player.level().isClientSide() || NostalgicTweaks.isServer();

        if (GameplayTweak.STAMINA_INFINITE_PEACEFUL.get() && difficulty == Difficulty.PEACEFUL)
        {
            this.staminaLevel = 20;
            return;
        }

        this.syncTickTimers();

        if (this.isExhausted)
        {
            if (canTick && this.canRegain(player) && this.isNotHalfRate(player))
                this.tickTimer++;

            if (this.tickTimer >= this.rechargeInTicks)
            {
                this.isExhausted = false;
                this.tickTimer = this.durationInTicks;
                this.waitTimer = 0;
            }

            this.setStaminaLevel(this.isExhausted ? this.rechargeInTicks : this.durationInTicks);
        }
        else if (isSprinting)
        {
            if (canTick)
                this.tickTimer--;

            this.waitTimer = this.cooldownInTicks;

            if (this.tickTimer <= 0)
            {
                this.isExhausted = true;
                this.tickTimer = 0;
            }

            this.setStaminaLevel(this.durationInTicks);
        }
        else
        {
            if (this.tickTimer < this.durationInTicks && canTick)
            {
                if (this.waitTimer <= 0 && this.canRegain(player) && this.isNotHalfRate(player))
                    this.tickTimer++;
                else
                    this.waitTimer--;
            }

            this.setStaminaLevel(this.durationInTicks);
        }

        boolean shouldTickAgain = this.hasPositiveEffect(player);

        if (this.hasNegativeEffect(player) && isSprinting)
            shouldTickAgain = true;

        if (shouldTickAgain && !this.tickAgain)
        {
            this.tickAgain = true;
            this.tick(player);
        }
        else
            this.tickAgain = false;
    }

    /**
     * Set the stamina level based on the current tick count out of the given amount in ticks.
     *
     * @param amountInTicks The "out of" amount in ticks.
     */
    public void setStaminaLevel(int amountInTicks)
    {
        this.staminaLevel = (int) Math.ceil(((double) this.tickTimer / amountInTicks) * 20.0D);
    }

    /**
     * Check if the given player is moving.
     *
     * @param player The {@link Player} instance to check.
     * @return Whether the player is moving.
     */
    public boolean isMoving(Player player)
    {
        double dx = player.getX() - player.xo;
        double dz = player.getZ() - player.zo;

        return dx * dx + dz * dz > 2.5E-7F;
    }

    /**
     * Check if the tick rate is not cut in half.
     *
     * @param player The {@link Player} instance.
     * @return Whether the tick is not in half.
     */
    public boolean isNotHalfRate(Player player)
    {
        boolean isHalfRate = false;
        boolean hasHunger = false;

        if (GameplayTweak.STAMINA_REGAIN_WHEN_MOVING.get() == StaminaRegain.HALF && this.isMoving(player))
            isHalfRate = !player.isSprinting() && this.staminaLevel < MAX_STAMINA_LEVEL;

        if (GameplayTweak.STAMINA_HUNGER_EFFECT.get())
            hasHunger = player.hasEffect(MobEffects.HUNGER);

        if (isHalfRate || hasHunger)
        {
            if (this.halfRateInTicks >= 1)
                this.halfRateInTicks = 0;
            else
            {
                this.halfRateInTicks++;
                return false;
            }
        }
        else
            this.halfRateInTicks = 0;

        return true;
    }

    /**
     * Check if the given player can regain their stamina.
     *
     * @param player The {@link Player} instance to check.
     * @return Whether the player can regain stamina.
     */
    public boolean canRegain(Player player)
    {
        if (GameplayTweak.STAMINA_REGAIN_WHEN_MOVING.get() != StaminaRegain.NONE)
            return true;

        return !this.isMoving(player);
    }

    /**
     * Check if the given player cannot regain their stamina.
     *
     * @param player The {@link Player} instance to check.
     * @return Whether the player cannot regain stamina.
     */
    public boolean cannotRegain(Player player)
    {
        return !this.canRegain(player) && !player.isSprinting() && this.staminaLevel < MAX_STAMINA_LEVEL;
    }

    /**
     * Check if the given player has a positive effect that impacts their stamina.
     *
     * @param player The {@link Player} instance to check.
     * @return Whether the player has a positive effect that affects their stamina.
     */
    public boolean hasPositiveEffect(Player player)
    {
        if (GameplayTweak.STAMINA_SATURATION_EFFECT.get() && StaminaHelper.isActiveFor(player))
            return player.hasEffect(MobEffects.SATURATION) && !player.isSprinting();

        return false;
    }

    /**
     * Check if the given player has a negative effect that impacts their stamina.
     *
     * @param player The {@link Player} instance to check.
     * @return Whether the player has a negative effect that affects their stamina.
     */
    public boolean hasNegativeEffect(Player player)
    {
        if (GameplayTweak.STAMINA_HUNGER_EFFECT.get() && StaminaHelper.isActiveFor(player))
            return player.hasEffect(MobEffects.HUNGER);

        return false;
    }

    /**
     * @return Get the amount of stamina the player has.
     */
    public int getStaminaLevel()
    {
        return this.staminaLevel;
    }

    /**
     * @return Get whether the player is cooling down before their stamina begins to increase.
     */
    public boolean isCooldown()
    {
        if (!GameplayTweak.STAMINA_SPRINT.get())
            return false;

        return this.waitTimer > 0 && this.waitTimer < this.cooldownInTicks;
    }

    /**
     * @return Whether the player is considered exhausted.
     */
    public boolean isExhausted()
    {
        if (!GameplayTweak.STAMINA_SPRINT.get())
            return false;

        return this.isExhausted;
    }
}
