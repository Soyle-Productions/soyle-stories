package com.soyle.stories.prose.proseEditor

import com.soyle.stories.common.NonBlankString

interface ProseEditorViewListener {
    fun getValidState()
    fun primeMentionQuery(primedIndex: Int)
    fun getStoryElementsContaining(query: NonBlankString)
    fun cancelQuery()
    fun selectStoryElement(filteredListIndex: Int)
/*
    /**
     * deletes one character forward (calling from end of content has no effect)
     */
    fun delete(start: Int): Boolean

    /**
     * deletes one character backward (calling from beginning of content has no effect)
     */
    fun backspace(start: Int): Boolean

    /**
     * deletes entire range of text content.  Usually triggered by selecting a range of text then typing backspace or delete.
     */
    fun deleteRange(start: Int, end: Int): Boolean

    /**
     * deletes, then enters new text at the beginning of a range of text.  Usually triggered by selecting a range of text, then starting to type or pasting text in.
     */
    fun replaceRange(start: Int, end: Int, replacementText: String): Boolean

    fun insert(start: Int, text: String): Boolean

    fun moveCursorLeft(currentPosition: Int): Int
    fun moveCursorRight(currentPosition: Int): Int

    /**
     * moves the cursor to the requested position, unless it's inside a mention.  Usually triggered by a mouse down, mouse drag, or arrow keys.
     */
    fun moveCursorTo(position: Int): Int
*/
    fun save()
}