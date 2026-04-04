package mod.adrenix.nostalgic.client.gui.widget.input.suggestion;

import mod.adrenix.nostalgic.client.gui.widget.input.AbstractInput;
import mod.adrenix.nostalgic.util.client.search.GenericDatabase;
import net.minecraft.client.Minecraft;

public class SoundSuggester<Input extends AbstractInput<?, Input>> extends InputSuggester<Input> {
    /* Database */

    private static final GenericDatabase<String> SOUND_DATABASE = new GenericDatabase<>();

    /* Constructor */

    /**
     * Create a new {@link SoundSuggester} provider.
     *
     * @param input The {@link Input} that provider is associated with.
     */
    public SoundSuggester(Input input) {
        super(input);

        if (SOUND_DATABASE.getDatabase().isEmpty()) {
            Minecraft.getInstance()
                    .getSoundManager()
                    .getAvailableSounds()
                    .forEach(sound -> SOUND_DATABASE.put(sound.toString(), sound.toString()));
        }
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericDatabase<String> getDatabase() {
        return SOUND_DATABASE;
    }
}
