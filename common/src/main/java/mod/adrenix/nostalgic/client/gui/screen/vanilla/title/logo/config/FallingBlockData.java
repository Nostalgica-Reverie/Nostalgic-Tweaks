package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config;

import java.util.ArrayList;

public class FallingBlockData
{
    public float scale = 1.0F;
    public final ArrayList<Block> blocks;

    public FallingBlockData()
    {
        this.blocks = new ArrayList<>();
    }

    public float getScale()
    {
        return this.scale;
    }

    public void setScale(float scale)
    {
        this.scale = scale;
    }

    public void copyTo(FallingBlockData other)
    {
        other.scale = this.scale;
        other.blocks.clear();

        this.blocks.forEach(block -> other.blocks.add(block.copy()));
    }

    public FallingBlockData copy()
    {
        FallingBlockData data = new FallingBlockData();

        data.scale = this.scale;

        this.blocks.forEach(block -> data.blocks.add(block.copy()));

        return data;
    }

    public static class Block
    {
        final static Block EMPTY = new Block(-1, -1, "minecraft:air", "#00000000", false);

        int x;
        int y;
        String blockId;
        String shadowColor;
        boolean sound;

        public Block()
        {
        }

        public Block(int x, int y, String blockId, String shadowColor, boolean sound)
        {
            this.x = x;
            this.y = y;
            this.blockId = blockId;
            this.shadowColor = shadowColor;
            this.sound = sound;
        }

        public void trimX(int by)
        {
            this.x -= by;
        }

        public void trimY(int by)
        {
            this.y -= by;
        }

        public int getX()
        {
            return Math.abs(this.x);
        }

        public int getY()
        {
            return Math.abs(this.y);
        }

        public boolean at(int x, int y)
        {
            return this.x == x && this.y == y;
        }

        public boolean hasSound()
        {
            return this.sound;
        }

        public void setSound(boolean sound)
        {
            this.sound = sound;
        }

        public String getShadowColor()
        {
            return this.shadowColor;
        }

        public void setShadowColor(String color)
        {
            this.shadowColor = color;
        }

        public String getBlockId()
        {
            return this.blockId;
        }

        public void setBlockId(String blockId)
        {
            this.blockId = blockId;
        }

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
