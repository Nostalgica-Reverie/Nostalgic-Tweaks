package mod.adrenix.nostalgic.client.gui.widget.input.suggestion;

import mod.adrenix.nostalgic.client.gui.widget.input.AbstractInput;
import mod.adrenix.nostalgic.util.client.search.GenericDatabase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.MobCategory;

public class CreatureSuggester<Input extends AbstractInput<?, Input>> extends InputSuggester<Input>
{
    /* Database */

    private static final GenericDatabase<String> CREATURES = new GenericDatabase<>();

    /* Constructor */

    /**
     * Create a new {@link CreatureSuggester} provider.
     *
     * @param input The {@link Input} that the provider is associated.
     */
    public CreatureSuggester(Input input)
    {
        super(input);

        if (CREATURES.getDatabase().isEmpty())
        {
            BuiltInRegistries.ENTITY_TYPE.keySet().forEach(location -> {
                if (BuiltInRegistries.ENTITY_TYPE.get(location).getCategory() == MobCategory.CREATURE)
                    CREATURES.put(location.toString(), location.toString());
            });
        }
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericDatabase<String> getDatabase()
    {
        return CREATURES;
    }
}
