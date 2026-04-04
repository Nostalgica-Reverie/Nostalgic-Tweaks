package mod.adrenix.nostalgic.client.gui.widget.input.suggestion;

import mod.adrenix.nostalgic.client.gui.widget.input.AbstractInput;
import mod.adrenix.nostalgic.util.client.search.GenericDatabase;

import java.util.List;

public abstract class InputSuggester<Input extends AbstractInput<?, Input>> {
    /* Fields */

    protected final Input input;
    protected String suggestion;

    /* Constructor */

    InputSuggester(Input input) {
        this.input = input;
        this.suggestion = "";
    }

    /* Methods */

    /**
     * @return The {@link GenericDatabase} associated with this suggestion provider.
     */
    public abstract GenericDatabase<String> getDatabase();

    /**
     * @return The input suggestion as a string.
     */
    public String get() {
        return this.suggestion;
    }

    /**
     * Generate a suggestion based on current input.
     */
    public void generate() {
        if (this.input.getInput().isEmpty() || this.input.getInput().isBlank())
            return;

        List<String> values = this.getDatabase().findValues(this.input.getInput());

        if (values.isEmpty())
            this.suggestion = "";
        else
            this.suggestion = values.getFirst();
    }
}
