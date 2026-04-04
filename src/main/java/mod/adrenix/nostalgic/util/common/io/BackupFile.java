//package mod.adrenix.nostalgic.util.common.io;
//
//import dev.architectury.platform.Platform;
//import mod.adrenix.nostalgic.NostalgicTweaks;
//import mod.adrenix.nostalgic.tweak.config.ModTweak;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.StandardCopyOption;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Locale;
//
/// **
// * This utility class provides helper methods for creating backups of configs.
// * <p>
// * Anytime the config file is about to be reset, a backup will be created. A maximum of five files will automatically be
// * backed up. The user can increase this limit, and the user can manually create a backup at any time.
// */
//public abstract class BackupFile {
//    /* Fields */
//
//    private static final String MOD_ID = NostalgicTweaks.MOD_ID;
//
//    /**
//     * This field contains the current environment (client/server) in lower case format. This helps differentiate the
//     * content within the file without opening it.
//     */
//    private static final String LOGICAL_SIDE = Platform.getEnv().toString().toLowerCase(Locale.ROOT);
//
//    /**
//     * This field contains the unique backup filename formatting that will inform the backup utility cleaner which files
//     * were created by the mod. If a user uses this type of filename formatting anywhere else in the backup directory,
//     * then those files will also be marked for deletion.
//     * <p>
//     * The %s will be replaced with a date formatted as month-day-year-time (ex: jan-01-2022-1134pm).
//     */
//    public static final String FILE_NAME = String.format("%s-%s-backup_%s.json", MOD_ID, LOGICAL_SIDE, "%s");
//
//    /**
//     * This field contains the unique startup backup filename formatting that will inform the backup utility how to save
//     * startup backup files. Any existing startup backup file will be replaced each time the mod starts, so there is no
//     * need to perform any backup file cleanups for startup backup files.
//     */
//    public static final String STARTUP_NAME = String.format("%s-%s-startup_backup.json", MOD_ID, LOGICAL_SIDE);
//
//    /**
//     * This field defines the regex for finding the month within a backup file. Example of a regex month result
//     * (jan-01-2022-).
//     */
//    private static final String REGEX_MONTH = "[a-z]{3}-\\d{2}-\\d{4}-";
//
//    /**
//     * This field defines the regex for finding the time within a backup file. Example of a regex time result (1134am)
//     * or (1134am_1) where _# indicates a backup made at the same minute.
//     */
//    private static final String REGEX_TIME = "\\d{4}(?>am|pm)(?>_\\d+)?";
//
//    /**
//     * This field contains a unique regex string that will help the backup utility cleaner find files that were created
//     * by the mod.
//     */
//    public static final String FILE_REGEX = MOD_ID + "-" + LOGICAL_SIDE + "-backup_" + REGEX_MONTH + REGEX_TIME + "\\.json";
//
//    /* Methods */
//
//    /**
//     * Gets a saved path based on the current operating system. This is used by information loggers.
//     *
//     * @param path     The backup file path.
//     * @param filename The name of the backed-up file.
//     * @return A full-path to the backed-up file.
//     */
//    private static String getSavedPath(String path, String filename) {
//        return String.format("%s%s%s", path, PathUtil.getDirectorySlash(), filename);
//    }
//
//    /**
//     * Saves the current config file as a startup backup file. This is done as soon as the config manager is made so
//     * that any issues during loading or backing up will not prevent a proper backup file from being created.
//     *
//     * @param path The {@link Path} to save the startup backup file to.
//     * @throws IOException When there is an issue reading/writing files.
//     */
//    public static void startup(Path path) throws IOException {
//        Files.createDirectories(PathUtil.getBackupPath());
//
//        String message = "[Config Backup] Created new startup config backup at %s";
//        String info = String.format(message, getSavedPath(PathUtil.getBackupPath().toString(), STARTUP_NAME));
//
//        Files.copy(path, PathUtil.getBackupPath().resolve(STARTUP_NAME), StandardCopyOption.REPLACE_EXISTING);
//        NostalgicTweaks.LOGGER.info(info);
//    }
//
//    /**
//     * Saves the current config file as a backup file.
//     *
//     * @param path The {@link Path} to save the file to.
//     * @return The path reference to the created backup file.
//     * @throws IOException When there is an issue reading/writing files.
//     */
//    public static Path save(Path path) throws IOException {
//        int limit = ModTweak.NUMBER_OF_BACKUPS.fromDisk();
//
//        if (limit != -1) {
//            List<Path> oldest = PathUtil.getOldestFiles(PathUtil.getBackupPath(), PathUtil::isBackupFile);
//
//            if (limit != 0 && oldest.size() >= limit) {
//                // Remove the oldest backup files until the file limit is satisfied
//                int remove = oldest.size() + 1 - limit;
//
//                for (int i = 0; i < remove; i++)
//                    PathUtil.delete(oldest.get(i));
//            } else if (limit == 0) {
//                // If the size is set to zero, then delete all backup files before creating a new one
//                for (Path file : oldest)
//                    PathUtil.delete(file);
//            }
//        }
//
//        int underscore = 1;
//        String filename = String.format(FILE_NAME, getTimestamp());
//        List<String> backups = PathUtil.getFilenames(PathUtil.getBackupPath(), PathUtil::isBackupFile);
//
//        for (String name : backups) {
//            // Two backups were made within the same minute - append an underscore value
//            if (name.equals(filename)) {
//                // Keep incrementing copy underscore value until it no longer matches a file
//                boolean naming = true;
//
//                while (naming) {
//                    boolean isValueFound = false;
//
//                    for (String duplicate : backups) {
//                        if (duplicate.equals(filename.replace(".json", String.format("_%s.json", underscore)))) {
//                            isValueFound = true;
//                            break;
//                        }
//                    }
//
//                    if (!isValueFound)
//                        naming = false;
//                    else
//                        underscore++;
//                }
//
//                filename = filename.replace(".json", String.format("_%s.json", underscore));
//                break;
//            }
//        }
//
//        Files.createDirectories(PathUtil.getBackupPath());
//
//        String directory = getSavedPath(PathUtil.getBackupPath().toString(), filename);
//        String info = String.format("[Config Backup] Created new config backup at %s", directory);
//        Path copy = Files.copy(path, PathUtil.getBackupPath().resolve(filename));
//
//        NostalgicTweaks.LOGGER.info(info);
//
//        return copy;
//    }
//
//    /**
//     * Gets the timestamp that will be used in a backup file.
//     *
//     * @return A timestamp using month-day-year-time (ex: jan-01-2022-1134pm).
//     */
//    private static String getTimestamp() {
//        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM-dd-yyyy-hhmma")).toLowerCase(Locale.ROOT);
//    }
//}
