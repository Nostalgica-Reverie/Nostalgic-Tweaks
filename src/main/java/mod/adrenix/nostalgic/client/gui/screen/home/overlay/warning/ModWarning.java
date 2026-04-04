//package mod.adrenix.nostalgic.client.gui.screen.home.overlay.warning;
//
//import mod.adrenix.nostalgic.tweak.TweakIssue;
//import mod.adrenix.nostalgic.util.common.lang.Lang;
//import mod.adrenix.nostalgic.util.common.lang.Translation;
//
//import java.util.Arrays;
//import java.util.stream.Stream;
//
/// **
// * Create a new mod warning instance that will be in the warning overlay when the warning mod is actively installed. The
// * mod must also cause a lot of related {@link TweakIssue} to warrant a mod warning instance. A banner will appear on
// * the home screen when a mod warning is active.
// */
//public enum ModWarning {
//    POLYTONE(TweakIssue.POLYTONE, Lang.Warning.POLYTONE),
//    OPTIFINE(TweakIssue.OPTIFINE, Lang.Warning.OPTIFINE);
//
//    /* Fields */
//
//    private final TweakIssue issue;
//    private final Translation description;
//
//    /* Constructor */
//
//    ModWarning(TweakIssue issue, Translation description) {
//        this.issue = issue;
//        this.description = description;
//    }
//
//    /* Static */
//
//    /**
//     * Get a stream of all warning enumerations.
//     *
//     * @return A {@link Stream} of all {@link ModWarning}.
//     */
//    public static Stream<ModWarning> stream() {
//        return Arrays.stream(ModWarning.values());
//    }
//
//    /* Methods */
//
//    /**
//     * @return The {@link TweakIssue} associated with this {@link ModWarning}.
//     */
//    public TweakIssue getIssue() {
//        return this.issue;
//    }
//
//    /**
//     * @return The unique {@link Translation} description for this warning.
//     */
//    public Translation getDescription() {
//        return this.description;
//    }
//
//    /**
//     * @return Whether this warning is currently active.
//     */
//    public boolean isActive() {
//        return this.issue.isActive().getAsBoolean();
//    }
//}
