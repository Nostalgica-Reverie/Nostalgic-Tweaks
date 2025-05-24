package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.math.Rectangle;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class FallingBlockConfig
{
    /* Static */

    /**
     * The maximum width limit of the falling block logo.
     */
    public static final int MAX_WIDTH = 64;

    /**
     * The maximum height limit of the falling block logo.
     */
    public static final int MAX_HEIGHT = 12;

    /**
     * The filename of the block data for the falling blocks logo animation.
     */
    public static final String FILENAME = "falling_blocks.json";

    /**
     * The filename for a copy of the block data for the falling blocks logo animation.
     */
    public static final String COPY_NAME = "falling_blocks_copy.json";

    /**
     * Tracks if the custom falling block logo has been changed during runtime.
     */
    public static final FlagHolder LOGO_CHANGED = FlagHolder.off();

    /**
     * A {@link NullableHolder} that may, or may not contain, a properly read {@link FallingBlockData} instance from
     * disk. If this is empty, then this means the mod was unable to create and read a data file.
     */
    public static final NullableHolder<FallingBlockData> BLOCK_DATA = NullableHolder.empty();

    /* Methods */

    /**
     * Run initialization instructions associated with {@link FallingBlockConfig} files.
     */
    public static void init()
    {
        CandyTweak.OLD_ALPHA_LOGO.whenChanged(LOGO_CHANGED::enable);
        CandyTweak.USE_CUSTOM_FALLING_LOGO.whenChanged(LOGO_CHANGED::enable);
    }

    /**
     * Check if the given block is out of logo bounds.
     *
     * @param block The {@link FallingBlockData.Block} instance to check.
     * @return Whether the block is out of bounds.
     */
    public static boolean isBlockOutOfBounds(FallingBlockData.Block block)
    {
        return block.x < 0 || block.x > MAX_WIDTH || block.y < 0 || block.y > MAX_HEIGHT;
    }

    /**
     * Upload blocks from a falling blocks logo config file to give the given blocks list.
     *
     * @param config A {@link File} to read from.
     * @param blocks The {@link ArrayList} of {@link FallingBlockData.Block} to upload to.
     * @throws JsonIOException     If there was a problem reading from the file reader.
     * @throws JsonSyntaxException If JSON is not a valid representation of {@link FallingBlockData}.
     */
    public static void upload(File config, ArrayList<FallingBlockData.Block> blocks) throws JsonIOException, JsonSyntaxException
    {
        try (FileReader reader = new FileReader(config))
        {
            FallingBlockData data = new Gson().fromJson(reader, FallingBlockData.class);

            if (data != null)
            {
                data.blocks.removeIf(FallingBlockConfig::isBlockOutOfBounds);
                blocks.addAll(data.blocks);
            }
        }
        catch (IOException exception)
        {
            NostalgicTweaks.LOGGER.error("[Falling Blocks] An error occurred when trying to read uploaded config file\n%s", exception);
        }
    }

    /**
     * Read (or reread) the falling block data config file from disk.
     *
     * @return Whether the config file was successfully read.
     */
    public static boolean read()
    {
        boolean hasException = false;

        try
        {
            Files.createDirectories(PathUtil.getLogoPath());
            Path config = PathUtil.getLogoPath().resolve(FILENAME);

            if (!Files.exists(config))
                Files.createFile(config);

            NostalgicTweaks.LOGGER.debug("[Falling Blocks] Successfully created or read config file");
        }
        catch (IOException exception)
        {
            NostalgicTweaks.LOGGER.error("[Falling Blocks] An error occurred when trying to create config file\n%s", exception);

            hasException = true;
        }

        try
        {
            File config = PathUtil.getLogoPath().resolve(FILENAME).toFile();

            try (FileReader reader = new FileReader(config))
            {
                BLOCK_DATA.set(new Gson().fromJson(reader, FallingBlockData.class));

                if (BLOCK_DATA.isPresent())
                    BLOCK_DATA.getOrThrow().blocks.removeIf(FallingBlockConfig::isBlockOutOfBounds);
                else
                    BLOCK_DATA.set(new FallingBlockData());
            }
            catch (IOException | JsonIOException | JsonSyntaxException exception)
            {
                if (BLOCK_DATA.isEmpty())
                    BLOCK_DATA.set(new FallingBlockData());

                hasException = true;

                NostalgicTweaks.LOGGER.error("[Falling Blocks] An error occurred when trying to read config file\n%s", exception);
            }
        }
        catch (InvalidPathException | UnsupportedOperationException exception)
        {
            NostalgicTweaks.LOGGER.error("[Falling Blocks] An error occurred when trying to get config file\n%s", exception);

            hasException = true;
        }

        return !hasException;
    }

    /**
     * Write the given data in the given file.
     *
     * @param data The {@link FallingBlockData} instance to write to disk.
     * @param file The {@link File} to write the data to.
     */
    public static void write(FallingBlockData data, File file)
    {
        try
        {
            try (FileWriter writer = new FileWriter(file))
            {
                new GsonBuilder().setPrettyPrinting().create().toJson(data, writer);
            }
            catch (IOException | JsonIOException exception)
            {
                NostalgicTweaks.LOGGER.error("[Falling Blocks] An error occurred when trying to write file to disk\n%s", exception);
            }
        }
        catch (InvalidPathException | UnsupportedOperationException exception)
        {
            NostalgicTweaks.LOGGER.error("[Falling Blocks] An error occurred when trying to read file for writing\n%s", exception);
        }
    }

    /**
     * Save the current block data content to disk.
     */
    public static void save()
    {
        if (BLOCK_DATA.isEmpty())
        {
            NostalgicTweaks.LOGGER.warn("[Falling Blocks] Tried writing empty block data to disk. This shouldn't happen!");
            return;
        }

        try
        {
            write(BLOCK_DATA.getOrThrow(), PathUtil.getLogoPath().resolve(FILENAME).toFile());

            LOGO_CHANGED.enable();
        }
        catch (InvalidPathException | UnsupportedOperationException exception)
        {
            NostalgicTweaks.LOGGER.error("[Falling Blocks] An error occurred when trying to save config file\n%s", exception);
        }
    }

    /**
     * Creates a backup of the current file saved on disk.
     *
     * @param prefixTimestamp Define a prefix that comes before the timestamp in the backup filename.
     * @return Whether the backup was successful.
     */
    public static boolean backup(String prefixTimestamp)
    {
        String filename = FILENAME.replace(".json", "");
        String backup = String.format("%s_%s_%d.json", filename, prefixTimestamp, Instant.now().toEpochMilli());

        Path source = PathUtil.getLogoPath().resolve(FILENAME);
        Path target = PathUtil.getLogoPath().resolve(backup);

        try
        {
            Path copy = Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            NostalgicTweaks.LOGGER.info("[Falling Block] Made backup of config file at %s", copy.toString());
        }
        catch (IOException exception)
        {
            NostalgicTweaks.LOGGER.error("[Falling Block] (I/O) Error: Could not create backup of config. Aborting reset!\n%s", exception);
            return false;
        }

        return true;
    }

    /**
     * Resets the current block data content back to its default state and saves that reset to disk.
     *
     * @param withBackup      Whether a backup of the current file on disk is made before resetting.
     * @param prefixTimestamp Define a prefix that comes before the timestamp in the backup filename if
     *                        {@code withBackup} is {@code true}. Or use {@code null} if {@code withBackup} is
     *                        {@code false}.
     */
    public static void reset(boolean withBackup, @Nullable String prefixTimestamp)
    {
        if (withBackup)
        {
            String prefix = prefixTimestamp == null ? "backup" : prefixTimestamp;

            if (!backup(prefix))
                return;
        }

        setBlockDataToDefault();
        save();
    }

    /**
     * Set block data to the default {@code MINECRAFT} layout.
     */
    public static void setBlockDataToDefault()
    {
        FallingBlockData data = new FallingBlockData();
        ArrayList<List<Integer>> rows = new ArrayList<>();

        rows.add(List.of(0, 4, 6, 8, 12, 14, 15, 16, 18, 19, 20, 22, 23, 24, 26, 27, 28, 30, 31, 32, 34, 35, 36));
        rows.add(List.of(0, 1, 3, 4, 6, 8, 9, 12, 14, 18, 22, 24, 26, 28, 30, 35));
        rows.add(List.of(0, 2, 4, 6, 8, 10, 12, 14, 15, 18, 22, 23, 26, 28, 27, 30, 31, 35));
        rows.add(List.of(0, 4, 6, 8, 11, 12, 14, 18, 22, 24, 26, 28, 30, 35));
        rows.add(List.of(0, 4, 6, 8, 12, 14, 15, 16, 18, 19, 20, 22, 24, 26, 28, 30, 35));

        for (int y = 0; y < rows.size(); y++)
        {
            for (int x : rows.get(y))
                data.blocks.add(new FallingBlockData.Block(x, y, "minecraft:stone", "#000000FF", false));
        }

        BLOCK_DATA.set(data);
    }

    /**
     * @return An empty {@link ArrayList} if the config is not available or an {@link ArrayList} of
     * {@link FallingBlockData.Block}.
     */
    public static ArrayList<FallingBlockData.Block> getBlockData()
    {
        if (isNotAvailable())
        {
            if (read() && BLOCK_DATA.isPresent())
                return BLOCK_DATA.getOrThrow().blocks;

            return new ArrayList<>();
        }
        else
        {
            return BLOCK_DATA.getOrThrow().blocks;
        }
    }

    /**
     * Check if two falling block data sets are different.
     *
     * @param initial      The initial {@link ArrayList} of {@link FallingBlockData.Block}.
     * @param maybeChanged The possibly modified {@link ArrayList} of {@link FallingBlockData.Block}.
     * @return Whether the two array lists are the same size and contain equal elements.
     */
    public static boolean isDataChanged(ArrayList<FallingBlockData.Block> initial, ArrayList<FallingBlockData.Block> maybeChanged)
    {
        if (initial.isEmpty() && maybeChanged.isEmpty())
            return false;

        if (initial.size() != maybeChanged.size())
            return true;

        Rectangle border = Rectangle.fromCollection(initial, FallingBlockData.Block::getX, FallingBlockData.Block::getY);

        for (int x = border.startX(); x <= border.endX(); x++)
        {
            for (int y = border.startY(); y <= border.endY(); y++)
            {
                final int pX = x;
                final int pY = y;

                FallingBlockData.Block first = initial.stream()
                    .filter(block -> block.at(pX, pY))
                    .findFirst()
                    .orElse(FallingBlockData.Block.EMPTY);

                FallingBlockData.Block second = maybeChanged.stream()
                    .filter(block -> block.at(pX, pY))
                    .findFirst()
                    .orElse(FallingBlockData.Block.EMPTY);

                if (first == FallingBlockData.Block.EMPTY && second == FallingBlockData.Block.EMPTY)
                    continue;

                if (!first.equals(second))
                    return true;
            }
        }

        return false;
    }

    /**
     * @return Whether the config file has been read and is ready to provide falling block data.
     */
    public static boolean isNotAvailable()
    {
        return BLOCK_DATA.isEmpty();
    }

    /**
     * @return Whether the falling block data has no blocks to read.
     */
    public static boolean hasNoBlocks()
    {
        if (BLOCK_DATA.isEmpty())
            return true;

        return BLOCK_DATA.getOrThrow().blocks.isEmpty();
    }
}
