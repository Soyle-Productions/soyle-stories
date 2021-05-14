package com.soyle.stories.character.list

import com.soyle.stories.character.create.CreateCharacterFlow
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialog
import com.soyle.stories.common.*
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.hasArrow
import com.soyle.stories.common.components.buttons.inviteButton
import com.soyle.stories.common.components.buttons.primaryButton
import com.soyle.stories.common.components.surfaces.*
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.*
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.lang.ref.WeakReference

class CharacterListView : View() {

    private val viewListener = resolve<CharacterListViewListener>()
    private val state = resolve<CharacterListState>()

    private val layoutStyle: SimpleObjectProperty<LayoutStyle> = SimpleObjectProperty(LayoutStyle.List)

    override val root: Parent = vbox(alignment = Pos.CENTER, spacing = 16.0) {
        parentProperty().onChangeWithCurrent { if (it != null) fitToParentSize() }
        vgrow = Priority.ALWAYS
        hgrow = Priority.SOMETIMES

        content()
    }

    @ViewBuilder
    private fun VBox.content() {
        getChildList()?.clear()
        val characters = state.characters.value
        when {
            characters == null -> {
                loader()
                state.characters.isNull.onChangeOnce { content() }
            }
            characters.isEmpty() -> {
                spacing = 16.0
                emptyCharacterList()
                state.characters.onChangeUntil({ it?.isEmpty() != true }) {
                    if (it?.isEmpty() != true) { content() }
                }
            }
            else -> {
                spacing = 0.0
                populatedCharacterList()
                state.characters.onChangeUntil({ it.isNullOrEmpty() }) {
                    if (it.isNullOrEmpty()) { content() }
                }
            }
        }
    }

    @ViewBuilder
    private fun Parent.loader() {
        progressindicator {
            id = "loader"
        }
        label("Loading characters")
    }

    @ViewBuilder
    internal fun Parent.emptyCharacterList() {
        imageview("com/soyle/stories/character/Character List Invite Image.png") {
            id = "inviteImage"
            onImageLoadingDone { error ->
                if (error) {
                    replaceWith(label("Invite image failed to load."))
                }
                inviteImageFinished()
            }
        }
    }

    @ViewBuilder
    private fun Parent.inviteImageFinished() {
        toolTitle("Characters")
        label("Characters are the perspectives from which you tell your story.  Create your first character by clicking the button below and get started!") {
            isWrapText = true
            textAlignment = TextAlignment.CENTER
            effect
        }
        inviteButton("Create First Character") {
            id = "create_character_button"
            action { scope.get<CreateCharacterFlow>().start() }
        }
    }

    @ViewBuilder
    internal fun Parent.populatedCharacterList() {
        val header = header()
        val characterItemsProperty = characterItems()

        // should only show drop shadow based on elevation distance
        header.surfaceRelativeElevation = header.surfaceElevation!! - characterItemsProperty.value.surfaceElevation!!
    }

    @ViewBuilder
    private fun Parent.header() = surface<HBox>(elevation = 16) {
        alignment = Pos.CENTER_LEFT
        spacing = 8.0
        paddingAll = 8.0
        style {
            padding = box(8.px)
        }
        primaryButton("Create New Character") {
            id = "create_character_button"
            action { scope.get<CreateCharacterDialog>().create() }
        }
        spacer()
        menubutton("Options") {
            addClass(ComponentsStyles.primary)
            addClass(ComponentsStyles.outlined)
            hasArrow = false
            disableWhen(state.selectedCharacterListItem.isNull)

            scopedListener(state.selectedCharacterListItem) {
                when (it) {
                    is CharacterListState.CharacterListItem.CharacterItem -> items.setAll(characterOptions(scope, it.character.value))
                    is CharacterListState.CharacterListItem.ArcItem -> items.setAll(characterArcOptions(scope, it.arc.value))
                    else -> items.clear()
                }
            }
        }/*
        spacer()
        menubutton("View As") {
            addClass(ComponentsStyles.secondary)
            addClass(ComponentsStyles.outlined)
            hasArrow = false
            fun viewOption(label: String, icon: MaterialIcon, style: LayoutStyle) {
                checkmenuitem(label) {
                    graphic = MaterialIconView(icon)
                    selectedProperty().softBind(layoutStyle) { it == style }
                    selectedProperty().onChange {
                        if (it) layoutStyle.set(style)
                        else if (layoutStyle.value == style) selectedProperty().set(true) }
                }
            }
            viewOption("List", MaterialIcon.LIST, LayoutStyle.List)
            viewOption("Grid", MaterialIcon.GRID_ON, LayoutStyle.Grid)
        }*/
    }

    @ViewBuilder
    private fun Parent.characterItems(): ObservableValue<Parent> {

        val binding = SimpleObjectProperty<Parent>(null)

        fun nodeCycle(layoutStyle: LayoutStyle): Node {
            val characterItemNode = characterItemLayout(layoutStyle)
            binding.set(characterItemNode)

            val nodeRef = WeakReference(characterItemNode)
            this@CharacterListView.layoutStyle.onChangeOnce {
                val oldNode = nodeRef.get() ?: return@onChangeOnce
                val newNode = nodeCycle(it!!)
                oldNode.replaceWith(newNode)
            }

            return characterItemNode
        }

        val node = nodeCycle(layoutStyle.value!!)

        add(node)

        return binding

    }

    private fun characterItemLayout(
        layoutStyle: LayoutStyle,
    ): Parent {
        val characterItemNode = when (layoutStyle) {
            LayoutStyle.List -> find<CharacterItemList>(scope).root
            LayoutStyle.Grid -> find<CharacterItemGrid>(scope).root
        }.apply {
            id = "character_item_layout"
            vgrow = Priority.ALWAYS
            addClass(SurfaceStyles.elevated[8])
            style { padding = box(8.px) }
        }

        return characterItemNode
    }

    init {
        viewListener.getList()
    }
}