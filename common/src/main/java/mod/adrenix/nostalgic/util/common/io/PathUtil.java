package mod.adrenix.nostalgic.util.common.io;

import com.google.common.collect.Lists;
import dev.architectury.platform.Platform;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.Util;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PathUtil
{
    /**
     * Get the size of a directory in bytes.
     *
     * @param directory The {@link Path} of a directory.
     * @return The size of the directory in bytes.
     */
    @PublicAPI
    public static long getSizeOfDirectory(Path directory)
    {
        long size = 0;

        try (Stream<Path> walk = Files.walk(directory))
        {
            size = walk.filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)).mapToLong(path -> {
                try
                {
                    return Files.size(path);
                }
                catch (Exception e)
                {
                    return 0L;
                }
            }).sum();

        }
        catch (IOException exception)
        {
            NostalgicTweaks.LOGGER.error("An IO error occurred when getting directory size\n%s", exception);
        }

        return size;
    }

    /**
     * Get the future size of a directory in bytes.
     *
     * @param directory The {@link Path} of a directory.
     * @return A {@link CompletableFuture} that will determine the size of the directory in bytes.
     */
    @PublicAPI
    public static CompletableFuture<Long> getFutureSizeOfDirectory(final Path directory)
    {
        return CompletableFuture.supplyAsync(() -> PathUtil.getSizeOfDirectory(directory), Util.backgroundExecutor());
    }

    /**
     * Get a human-readable file size.
     *
     * @param size The size of the file in bytes.
     * @return A human-readable file size.
     */
    @PublicAPI
    public static String getFormattedFileSize(long size)
    {
        String[] types = new String[] { "B", "KB", "MB", "GB", "TB" };
        int index = (int) (Math.log10(size) / 3);
        double typeValue = 1 << (index * 10);

        return new DecimalFormat("#,##0.0").format(size / typeValue) + " " + types[index];
    }

    /**
     * Check if the given path is a directory.
     *
     * @param path A {@link Path} instance.
     * @return Whether the path is a directory.
     */
    @PublicAPI
    public static boolean isDirectory(Path path)
    {
        return Files.isDirectory(path);
    }

    /**
     * Check if the given path is not a directory.
     *
     * @param path A {@link Path} instance.
     * @return Whether the path is a file.
     */
    @PublicAPI
    public static boolean isNotDirectory(Path path)
    {
        return !isDirectory(path);
    }

    /**
     * Check if the given path is a backup file.
     *
     * @param path A {@link Path} instance.
     * @return Whether the file is a mod config backup file.
     */
    @PublicAPI
    public static boolean isBackupFile(Path path)
    {
        return !Files.isDirectory(path) && path.getFileName().toString().matches(BackupFile.FILE_REGEX);
    }

    /**
     * Check if the given file ends in a {@code .json} extension.
     *
     * @param file A {@link Path} instance.
     * @return Whether the given path is a json file.
     */
    @PublicAPI
    public static boolean isJsonFile(Path file)
    {
        return getFileExtension(file).equals("json");
    }

    /**
     * On Windows, it is a backslash, on other operating systems, it is a forward slash.
     *
     * @return A directory separator symbol.
     */
    @PublicAPI
    public static String getDirectorySlash()
    {
        return Util.getPlatform() == Util.OS.WINDOWS ? "\\" : "/";
    }

    /**
     * Lazily get a list of files within a directory. This does not check embedded directories or walk the filesystem.
     *
     * @param dir The directory to get files from.
     * @return A set of paths within the directory.
     * @throws IOException When there is an issue reading files.
     */
    @PublicAPI
    public static List<String> getFilenames(Path dir, Predicate<? super Path> filter) throws IOException
    {
        try (Stream<Path> stream = Files.list(dir))
        {
            return stream.filter(filter)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toCollection(ArrayList::new));
        }
    }

    /**
     * Retrieve the creation time of a file. If the file system does not store a date of creating a file, then this
     * method will return the last modified date. If the last modified date is not stored as well, then the epoch of
     * (1970-01-01T00:00:00Z) will be returned.
     *
     * @param path The {@link Path} to get creation time from.
     * @return A creation timestamp measured in milliseconds since the epoch (01.01.1970).
     */
    @PublicAPI
    public static long getCreationTime(Path path)
    {
        try
        {
            return Files.readAttributes(path, BasicFileAttributes.class).creationTime().toMillis();
        }
        catch (IOException exception)
        {
            return 0L;
        }
    }

    /**
     * Retrieve the last time a file was modified. If the file system does not store a date of last modification, then
     * this method will return an implementation-specific default value, typically a {@code long} representing the epoch
     * (1970-01-01T00:00:00Z).
     *
     * @param path The {@link Path} to get the last modification time from.
     * @return The last modified timestamp that is measured in milliseconds since the epoch (01.01.1970).
     */
    @PublicAPI
    public static long getLastModifiedTime(Path path)
    {
        try
        {
            return Files.readAttributes(path, BasicFileAttributes.class).lastModifiedTime().toMillis();
        }
        catch (IOException exception)
        {
            return 0L;
        }
    }

    /**
     * Parse an epoch time in milliseconds to a formatted timestamp.
     *
     * @param timeInMillis The epoch time in milliseconds.
     * @return A formatted timestamp.
     */
    @PublicAPI
    public static String parseEpochTime(long timeInMillis)
    {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), TimeZone.getDefault().toZoneId())
            .format(DateTimeFormatter.ofPattern("EEEE, ee MMMM yyyy hh:mm a"));
    }

    /**
     * Get the file extension from the given path. If the given path is a directory, then an empty string is returned.
     * If no extension was found, then an empty string is returned. If the filename is the form {@code .ext} then
     * {@code ext} will be returned.
     *
     * @param path A {@link Path} instance.
     * @return An empty string or an extension name.
     */
    @PublicAPI
    public static String getFileExtension(Path path)
    {
        if (PathUtil.isDirectory(path))
            return "";

        String filename = path.getFileName().toString();
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex == -1 ? "" : filename.substring(dotIndex + 1);
    }

    /**
     * Lazily get a list of files (oldest to newest) within a directory. This does not check embedded directories or
     * walk the filesystem. No directory paths will be in the resulting list.
     *
     * @param dir    The directory to get files from.
     * @param filter The filter to apply to the stream of paths.
     * @return A list of paths from oldest created to newest created.
     * @throws IOException When there is an issue reading files.
     */
    @PublicAPI
    public static List<Path> getOldestFiles(Path dir, @Nullable Predicate<? super Path> filter) throws IOException
    {
        try (Stream<Path> stream = Files.list(dir))
        {
            if (filter == null)
                filter = (file) -> true;

            return stream.filter(filter)
                .filter(PathUtil::isNotDirectory)
                .sorted(Comparator.comparingLong(PathUtil::getCreationTime))
                .collect(Collectors.toCollection(LinkedList::new));
        }
    }

    /**
     * Overload method for {@link PathUtil#getOldestFiles(Path, Predicate)}. The default filter prevents directories
     * from being included in the resulting list.
     *
     * @param dir The directory to get the oldest files from.
     * @return A list of paths (which will only be files) within the directory from oldest created to newest created.
     * @throws IOException When there is an issue reading files.
     */
    @PublicAPI
    public static List<Path> getOldestFiles(Path dir) throws IOException
    {
        return PathUtil.getOldestFiles(dir, null);
    }

    /**
     * Lazily get a list of files (oldest modified to newest modified) within a directory. This does not check embedded
     * directories or walk the filesystem. No directory paths will be in the resulting list.
     *
     * @param dir    The directory to get files from.
     * @param filter The filter to apply to the stream of paths.
     * @return A list of paths from oldest modified to newest modified.
     * @throws IOException When there is an issue reading files.
     */
    @PublicAPI
    public static List<Path> getOldestModified(Path dir, @Nullable Predicate<? super Path> filter) throws IOException
    {
        try (Stream<Path> stream = Files.list(dir))
        {
            if (filter == null)
                filter = (file) -> true;

            return stream.filter(filter)
                .filter(PathUtil::isNotDirectory)
                .sorted(Comparator.comparingLong(PathUtil::getLastModifiedTime))
                .collect(Collectors.toCollection(LinkedList::new));
        }
    }

    /**
     * Overload method for {@link PathUtil#getOldestModified(Path, Predicate)}. The default filter prevents directories
     * from being included in the resulting list.
     *
     * @param dir The directory to get the oldest files from.
     * @return A list of paths (which will only be files) within the directory from oldest modified to newest modified.
     * @throws IOException When there is an issue reading files.
     */
    @PublicAPI
    public static List<Path> getOldestModified(Path dir) throws IOException
    {
        return PathUtil.getOldestModified(dir, null);
    }

    /**
     * Lazily get a list of files (newest to oldest) within a directory. This does not check embedded directories or
     * walk the file system. No directory paths will be in the resulting list.
     *
     * @param dir    The directory to get files from.
     * @param filter The filter to apply to the stream of paths.
     * @return A list of paths from newest to oldest modified.
     * @throws IOException When there is an issue reading files.
     */
    @PublicAPI
    public static List<Path> getNewestFiles(Path dir, Predicate<? super Path> filter) throws IOException
    {
        return Lists.reverse(getOldestFiles(dir, filter));
    }

    /**
     * Get the files found in the given path in order from newest created to oldest created. No directories will be
     * included in the results.
     *
     * @param dir The directory to get file data from.
     * @return A set of paths (which will only be files) within the directory from newest created to oldest created.
     * @throws IOException When there is an issue reading files.
     */
    @PublicAPI
    public static List<Path> getNewestFiles(Path dir) throws IOException
    {
        return Lists.reverse(getOldestFiles(dir));
    }

    /**
     * Lazily get a list of files (newest modified to oldest modified) within a directory. This does not check embedded
     * directories or walk the file system. No directory paths will be in the resulting list.
     *
     * @param dir    The directory to get files from.
     * @param filter The filter to apply to the stream of paths.
     * @return A list of paths from newest modified to oldest modified.
     * @throws IOException When there is an issue reading files.
     */
    @PublicAPI
    public static List<Path> getNewestModified(Path dir, Predicate<? super Path> filter) throws IOException
    {
        return Lists.reverse(getOldestModified(dir, filter));
    }

    /**
     * Get the files found in the given path in order from newest modified to oldest modified. No directories will be
     * included in the results.
     *
     * @param dir The directory to get file data from.
     * @return A set of paths (which will only be files) within the directory from newest modified to oldest modified.
     * @throws IOException When there is an issue reading files.
     */
    @PublicAPI
    public static List<Path> getNewestModified(Path dir) throws IOException
    {
        return Lists.reverse(getOldestModified(dir));
    }

    /**
     * Delete the given path from the file system. This task cannot be undone. If catching {@link IOException} is
     * desired, then use {@link #deleteWithoutCatch(Path)}.
     *
     * @param path The {@link Path} to delete.
     */
    @PublicAPI
    public static void delete(Path path)
    {
        try
        {
            deleteWithoutCatch(path);
        }
        catch (NoSuchFileException exception)
        {
            NostalgicTweaks.LOGGER.error("File: %s - does not exist so it cannot be deleted", path);
        }
        catch (IOException exception)
        {
            NostalgicTweaks.LOGGER.error("Could not delete %s\n%s", path, exception);
        }
    }

    /**
     * Delete the given path from the file system. This task cannot be undone. If avoiding the need to catch
     * {@link IOException} is desired, then use {@link #delete(Path)}.
     *
     * @param path The {@link Path} to delete.
     * @throws IOException When there is an issue deleting the given path.
     */
    @PublicAPI
    public static void deleteWithoutCatch(Path path) throws IOException
    {
        Files.delete(path);
        NostalgicTweaks.LOGGER.info("Deleted: %s", path.getFileName().toString());
    }

    /**
     * Get (and create the path if necessary) a path instance.
     *
     * @param path The path string to resolve against the desired path.
     * @return A {@link Path} instance from the given path string.
     */
    @PublicAPI
    public static Path getOrCreatePath(String path)
    {
        try
        {
            return Files.createDirectories(Platform.getConfigFolder().resolve(path));
        }
        catch (IOException exception)
        {
            NostalgicTweaks.LOGGER.error("Could not resolve config path (%s)\n%s", path, exception);
        }

        return null;
    }

    /**
     * Get (and create the path if necessary) the game's logs directory.
     *
     * @return The game's logs directory.
     */
    @PublicAPI
    public static Path getLogsPath()
    {
        try
        {
            return Files.createDirectories(Platform.getGameFolder().resolve("logs"));
        }
        catch (IOException exception)
        {
            NostalgicTweaks.LOGGER.error("Could not resolve game logs path\n%s", exception);
        }

        return Platform.getGameFolder();
    }

    /**
     * @return The mod's main config directory.
     */
    @PublicAPI
    public static Path getConfigPath()
    {
        return getOrCreatePath(NostalgicTweaks.MOD_ID);
    }

    /**
     * @return The mod's backup config directory.
     */
    @PublicAPI
    public static Path getBackupPath()
    {
        return getOrCreatePath(NostalgicTweaks.MOD_ID + "/backup");
    }

    /**
     * @return The mod's tweak packs config directory.
     */
    @PublicAPI
    public static Path getPacksPath()
    {
        return getOrCreatePath(NostalgicTweaks.MOD_ID + "/packs");
    }

    /**
     * @return The mod's presets config directory.
     */
    @PublicAPI
    public static Path getPresetsPath()
    {
        return getOrCreatePath(NostalgicTweaks.MOD_ID + "/presets");
    }

    /**
     * @return The mod's falling block logo config directory.
     */
    @PublicAPI
    public static Path getLogoPath()
    {
        return getOrCreatePath(NostalgicTweaks.MOD_ID + "/logo");
    }
}
