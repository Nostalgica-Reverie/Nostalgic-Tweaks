package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.editor;

import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockConfig;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockData;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;

import java.util.ArrayDeque;

public class EditorHistory
{
    /* Static */

    public static final int ACTION_CAPTURE_MAX = 50;

    /* Fields */

    public final ArrayDeque<FallingBlockData> undo;
    public final ArrayDeque<FallingBlockData> redo;

    protected final FallingBlockEditorScreen editorScreen;
    protected FallingBlockData firstPoint;

    /* Constructor */

    public EditorHistory(FallingBlockEditorScreen screen)
    {
        this.undo = new ArrayDeque<>();
        this.redo = new ArrayDeque<>();

        this.firstPoint = FallingBlockConfig.getData();
        this.editorScreen = screen;
    }

    /* Methods */

    /**
     * @return Whether the history timeline has nothing to go back to.
     */
    @PublicAPI
    public boolean isNothingToUndo()
    {
        return this.undo.isEmpty();
    }

    /**
     * @return Whether the history timeline has nothing to move forwards to.
     */
    @PublicAPI
    public boolean isNothingToRedo()
    {
        return this.redo.isEmpty();
    }

    /**
     * Set what the first point of history is on the timeline. This is used to determine if an actual change in history
     * has occurred later on.
     *
     * @param data The initial {@link FallingBlockData} point of history. This data will be <b>copied</b>, so it is not
     *             necessary to make a copy beforehand.
     */
    @PublicAPI
    public void setFirstPointOnTimeline(FallingBlockData data)
    {
        this.firstPoint = data.copy();
    }

    /**
     * @return Whether there was a change from the first point in history or a change in the last point of history.
     */
    @PublicAPI
    public boolean isChangeInTimeline()
    {
        return this.undo.isEmpty() || FallingBlockConfig.isDataChanged(this.peekLastToUndo(), this.editorScreen.getManagedData());
    }

    /**
     * Capture the current state of history so that it can be undone later. This will <b color=red>clear</b> all points
     * on the timeline that can be redone. If the undo history is greater than the maximum number of actions that can be
     * captured, then the first action that was recorded will be dropped from memory.
     */
    @PublicAPI
    public void capture()
    {
        this.redo.clear();
        this.addPointToUndo();
    }

    /**
     * Add a point on the history timeline to undo to.
     */
    protected void addPointToUndo()
    {
        this.undo.add(this.editorScreen.getManagedData().copy());

        if (this.undo.size() > ACTION_CAPTURE_MAX)
            this.undo.pop();
    }

    /**
     * Add a point on the history timeline that can be redone. If the redo history is greater than the maximum number of
     * actions that can be captured, then the first redo action that was recorded will be dropped from memory.
     */
    protected void addPointToRedo()
    {
        this.redo.add(this.editorScreen.getManagedData().copy());

        if (this.redo.size() > ACTION_CAPTURE_MAX)
            this.redo.pop();
    }

    /**
     * Goes back to the last undo point on the history timeline.
     */
    @PublicAPI
    public void goBack()
    {
        if (this.undo.isEmpty())
            return;

        this.addPointToRedo();

        FallingBlockData last = this.undo.pollLast();

        if (last != null)
        {
            this.editorScreen.setManagedData(last);
            this.editorScreen.replayAnimation(true);
        }
    }

    /**
     * Goes forward to the next redo point on the history timeline.
     */
    @PublicAPI
    public void goForward()
    {
        if (this.redo.isEmpty())
            return;

        this.addPointToUndo();

        FallingBlockData last = this.redo.pollLast();

        if (last != null)
        {
            this.editorScreen.setManagedData(last);
            this.editorScreen.replayAnimation(true);
        }
    }

    /**
     * Peek the previous {@link FallingBlockData} that was undone or the first point of history of there is nothing to
     * undo.
     *
     * @return The last {@link FallingBlockData} that was captured or the first point in history.
     */
    @PublicAPI
    public FallingBlockData peekLastOrFirst()
    {
        FallingBlockData data = this.undo.peekLast();

        if (data != null)
            return data;

        return this.firstPoint;
    }

    /**
     * Peek the previous {@link FallingBlockData} that was undone.
     *
     * @return The last {@link FallingBlockData} that was captured.
     */
    @PublicAPI
    public FallingBlockData peekLastToUndo()
    {
        FallingBlockData data = this.undo.peekLast();

        if (data != null)
            return data;

        return new FallingBlockData();
    }

    /**
     * Peek the next {@link FallingBlockData} that will be redone to.
     *
     * @return The next {@link FallingBlockData} instance to redo to.
     */
    @PublicAPI
    public FallingBlockData peekLastToRedo()
    {
        FallingBlockData data = this.redo.peekLast();

        if (data != null)
            return data;

        return new FallingBlockData();
    }
}
