package mod.adrenix.nostalgic.client.gui.widget.input.suggestion;

import mod.adrenix.nostalgic.client.gui.widget.input.AbstractInput;
import mod.adrenix.nostalgic.util.client.search.GenericDatabase;
import net.minecraft.core.registries.BuiltInRegistries;

public class ParticleSuggester<Input extends AbstractInput<?, Input>> extends InputSuggester<Input> {
    /* Database */

    private static final GenericDatabase<String> PARTICLE_DATABASE = new GenericDatabase<>();

    /* Constructor */

    /**
     * Create a new {@link ParticleSuggester} provider.
     *
     * @param input The {@link Input} that the provider is associated with.
     */
    public ParticleSuggester(Input input) {
        super(input);

        if (PARTICLE_DATABASE.getDatabase().isEmpty()) {
            BuiltInRegistries.PARTICLE_TYPE.keySet()
                    .forEach(location -> PARTICLE_DATABASE.put(location.toString(), location.toString()));
        }
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericDatabase<String> getDatabase() {
        return PARTICLE_DATABASE;
    }
}
