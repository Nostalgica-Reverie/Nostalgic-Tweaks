package mod.adrenix.nostalgic.client.gui.widget.text;

import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface MultiLineText {
    /**
     * An empty {@link MultiLineText} instance.
     */
    MultiLineText EMPTY = new MultiLineText() {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public @Unmodifiable List<Line> getLines() {
            return List.of();
        }
    };

    /**
     * Create an instance that represents multiple lines of text.
     *
     * @param text     A {@link FormattedText} to convert into multiple lines.
     * @param maxWidth The maximum width allowed for each line of text.
     * @return A new {@link MultiLineText} instance.
     */
    static MultiLineText create(FormattedText text, int maxWidth) {
        List<Line> textList = GuiUtil.font().split(text, maxWidth).stream().map(Line::new).toList();

        if (textList.isEmpty())
            return EMPTY;

        return new MultiLineText() {
            @Override
            public int getCount() {
                return textList.size();
            }

            @Override
            public @Unmodifiable List<Line> getLines() {
                return textList;
            }
        };
    }

    /**
     * @return The immutable {@link List} of {@link Line} built by this {@link MultiLineText} instance.
     */
    @Unmodifiable
    List<Line> getLines();

    /**
     * @return The number of lines this {@link MultiLineText} instance has.
     */
    int getCount();

    /**
     * @return The largest font width in the {@link MultiLineText} instance.
     */
    default int maxWidth() {
        return getLines().stream().mapToInt(Line::getWidth).max().orElse(0);
    }

    class Line {
        private final FormattedCharSequence text;
        private final int width;

        private Line(FormattedCharSequence text) {
            this.text = text;
            this.width = GuiUtil.font().width(text);
        }

        public FormattedCharSequence getText() {
            return this.text;
        }

        public int getWidth() {
            return this.width;
        }
    }
}
