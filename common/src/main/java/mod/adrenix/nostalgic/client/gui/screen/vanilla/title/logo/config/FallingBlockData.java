package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.math.Rectangle;

import java.util.ArrayList;

/**
 * This class represents the config structure Gson will use to make the falling logo config files. All data that needs
 * stored must be declared in this class; otherwise, it will never be saved.
 */
public class FallingBlockData
{
    /* Fields */

    public float scale = 1.0F;
    public final ArrayList<Block> blocks;

    /* Constructor */

    public FallingBlockData()
    {
        this.blocks = new ArrayList<>();
    }

    /* Methods */

    /**
     * @return The scale defined by this config.
     */
    public float getScale()
    {
        return this.scale;
    }

    /**
     * Change the scaling amount.
     *
     * @param scale The new scale amount.
     */
    public void setScale(float scale)
    {
        this.scale = scale;
    }

    /**
     * Copy data from this instance to the given instance. This is useful in situations where memory locations need
     * decoupled to prevent cache and history errors.
     *
     * @param other The other {@link FallingBlockData} to copy to.
     */
    public void copyTo(FallingBlockData other)
    {
        other.scale = this.scale;
        other.blocks.clear();

        this.blocks.forEach(block -> other.blocks.add(block.copy()));
    }

    /**
     * @return A new {@link FallingBlockData} instance that is decoupled from this instance, including its blocks.
     */
    public FallingBlockData copy()
    {
        FallingBlockData data = new FallingBlockData();

        data.scale = this.scale;

        this.blocks.forEach(block -> data.blocks.add(block.copy()));

        return data;
    }

    /**
     * Check if two falling block data sets are different.
     *
     * @param other The other {@link FallingBlockData} instance to check against.
     * @return Whether the two falling block array lists are the same size and contain equivalent elements.
     */
    @PublicAPI
    public boolean areBlocksDifferent(FallingBlockData other)
    {
        if (this.blocks.isEmpty() && other.blocks.isEmpty())
            return false;

        if (this.blocks.size() != other.blocks.size())
            return true;

        Rectangle border = Rectangle.fromCollection(this.blocks, Block::getX, Block::getY);

        for (int x = border.startX(); x < border.endX(); x++)
        {
            for (int y = border.startY(); y < border.endY(); y++)
            {
                final int relX = x;
                final int relY = y;

                Block first = this.blocks.stream()
                    .filter(block -> block.at(relX, relY))
                    .findFirst()
                    .orElse(Block.EMPTY);

                Block second = other.blocks.stream()
                    .filter(block -> block.at(relX, relY))
                    .findFirst()
                    .orElse(Block.EMPTY);

                if (first == Block.EMPTY && second == Block.EMPTY)
                    continue;

                if (!first.equals(second))
                    return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object)
    {
        if (object instanceof FallingBlockData data)
        {
            boolean sameBlocks = !this.areBlocksDifferent(data);
            boolean sameScale = ((Float) this.scale).equals(data.scale);

            return sameBlocks && sameScale;
        }

        return false;
    }

    /* Block Data */

    /**
     * This class represents the data stored in each block within the {@link FallingBlockData} config file. Each block
     * must have an x-pos greater than or equal to zero and a y-pos greater than or equal to zero. Each block can have
     * its own block identifier, color, and falling sound.
     */
    public static class Block
    {
        /**
         * Represents an empty block instance. Use by the config to substitute areas of the logo that do not contain
         * block data.
         */
        final static Block EMPTY = new Block(-1, -1, "minecraft:air", "#00000000", false);

        /* Fields */

        int x;
        int y;
        String blockId;
        String shadowColor;
        boolean sound;

        /* Constructors */

        /**
         * An empty constructor is required for Gson.
         */
        public Block()
        {
        }

        /**
         * Make a new falling block instance.
         *
         * @param x           The x-pos relative to neighboring blocks.
         * @param y           The y-pos relative to neighboring blocks.
         * @param blockId     The in-game block resource identifier.
         * @param shadowColor The hex color of the block shadow.
         * @param sound       Whether to play the block's placing sound after it falls.
         */
        public Block(int x, int y, String blockId, String shadowColor, boolean sound)
        {
            this.x = x;
            this.y = y;
            this.blockId = blockId;
            this.shadowColor = shadowColor;
            this.sound = sound;
        }

        /**
         * Trim the relative x-pos by the given amount.
         *
         * @param by The amount to subtract the current x-pos by.
         */
        public void trimX(int by)
        {
            this.x -= by;
        }

        /**
         * Trim the relative y-pos by the given amount.
         *
         * @param by The amount to subtract the current y-pos by.
         */
        public void trimY(int by)
        {
            this.y -= by;
        }

        /**
         * The current relative x-pos. Guaranteed to be positive.
         *
         * @return The absolute relative x-position.
         */
        public int getX()
        {
            return Math.abs(this.x);
        }

        /**
         * The current relative y-pos. Guaranteed to be positive.
         *
         * @return The absolute relative y-position.
         */
        public int getY()
        {
            return Math.abs(this.y);
        }

        /**
         * Check if the given relative coordinate matches this block.
         *
         * @param x The relative x-pos.
         * @param y The relative y-pos.
         * @return Whether this block is at the given relative coordinate.
         */
        public boolean at(int x, int y)
        {
            return this.x == x && this.y == y;
        }

        /**
         * @return Whether this block will play its placing sound after it has fallen.
         */
        public boolean hasSound()
        {
            return this.sound;
        }

        /**
         * Change whether to play a sound after the block falls.
         *
         * @param sound The new sound flag.
         */
        public void setSound(boolean sound)
        {
            this.sound = sound;
        }

        /**
         * @return The hex (e.g., "#00FF554422" [RGBA]) of the block's shadow color.
         */
        public String getShadowColor()
        {
            return this.shadowColor;
        }

        /**
         * Change the hex of the block's shadow color.
         *
         * @param color The new hex color of the shadow (e.g., "#00FF554422" [RGBA]).
         */
        public void setShadowColor(String color)
        {
            this.shadowColor = color;
        }

        /**
         * @return The block's in-game identifier to get block data from.
         */
        public String getBlockId()
        {
            return this.blockId;
        }

        /**
         * Change this block's in-game block identifier.
         *
         * @param blockId The new block identifier.
         */
        public void setBlockId(String blockId)
        {
            this.blockId = blockId;
        }

        /**
         * Make a distinct copy of this block that is completely decoupled from this block in memory.
         *
         * @return A new decoupled {@link Block} instance.
         */
        public Block copy()
        {
            Block block = new Block();

            block.x = this.x;
            block.y = this.y;
            block.blockId = this.blockId;
            block.shadowColor = this.shadowColor;
            block.sound = this.sound;

            return block;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object object)
        {
            if (!(object instanceof Block))
                return false;

            boolean sameX = ((Block) object).getX() == this.x;
            boolean sameY = ((Block) object).getY() == this.y;
            boolean sameSound = ((Block) object).hasSound() == this.sound;
            boolean sameColor = ((Block) object).getShadowColor().equals(this.shadowColor);
            boolean sameBlock = ((Block) object).getBlockId().equals(this.blockId);

            return sameX && sameY && sameSound && sameColor && sameBlock;
        }
    }
}
