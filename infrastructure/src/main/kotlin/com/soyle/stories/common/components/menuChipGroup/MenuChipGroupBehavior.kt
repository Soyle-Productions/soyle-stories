package com.soyle.stories.common.components.menuChipGroup

import com.sun.javafx.scene.control.behavior.ButtonBehavior
import com.sun.javafx.scene.control.inputmap.InputMap
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent

class MenuChipGroupBehavior(menuChipGroup: MenuChipGroup) : ButtonBehavior<MenuChipGroup>(menuChipGroup) {
    private val buttonInputMap: InputMap<MenuChipGroup>? = super.getInputMap()

    init {

        // We want to remove the mapping for MOUSE_RELEASED, as the event is
        // handled by the skin instead, which calls the mouseReleased method below.
        removeMapping(MouseEvent.MOUSE_RELEASED)

        /**
         * The base key bindings for a MenuButton. These basically just define the
         * bindings to close an open menu. Subclasses will tell you what can be done
         * to open it.
         */
        addDefaultMapping(InputMap.KeyMapping(KeyCode.ESCAPE) { node.hide() })
        addDefaultMapping(InputMap.KeyMapping(KeyCode.CANCEL) { node.hide() })

        // we create a child input map, as we want to override some of the
        // focus traversal behaviors (and child maps take precedence over parent maps)
        val customFocusInputMap: InputMap<MenuChipGroup> = InputMap<MenuChipGroup>(menuChipGroup)
        addDefaultMapping(customFocusInputMap, InputMap.KeyMapping(KeyCode.UP) { overrideTraversalInput(it!!) })
        addDefaultMapping(customFocusInputMap, InputMap.KeyMapping(KeyCode.DOWN) { overrideTraversalInput(it!!) })
        addDefaultChildMap(buttonInputMap, customFocusInputMap)

        /**
         * The key bindings for the MenuButton. Sets up the keys to open the menu.
         */
        addDefaultMapping(InputMap.KeyMapping(KeyCode.SPACE) { openAction() })
        addDefaultMapping(InputMap.KeyMapping(KeyCode.ENTER) { openAction() })
    }

    private fun overrideTraversalInput(event: KeyEvent) {
        val button: MenuChipGroup = node
        if (!button.isShowing &&
            event.code == KeyCode.UP ||
            event.code == KeyCode.DOWN
        ) {
            button.show()
        }
    }

    private fun openAction() {
        if (node.isShowing) {
            node.hide()
        } else {
            node.show()
        }
    }

    fun onMousePressed(e: MouseEvent) {
        val control = node
        if (!control.isFocused && control.isFocusTraversable) {
            control.requestFocus()
        }
        if (control.isShowing) {
            control.hide()
        } else {
            if (e.button == MouseButton.PRIMARY) {
                control.show()
            }
        }
    }

    fun onMouseReleased(e: MouseEvent) {
        if (node.isShowing && !node.contains(e.x, e.y)) {
            node.hide()
        }
        node.disarm()
    }

}