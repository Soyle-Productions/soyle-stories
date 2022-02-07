package com.soyle.stories.character.list

import com.soyle.stories.character.create.createCharacter
import com.soyle.stories.character.profile.CharacterProfileProps
import com.soyle.stories.character.profile.CharacterProfileScope
import com.soyle.stories.character.profile.CharacterProfileView
import com.soyle.stories.common.*
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.hasArrow
import com.soyle.stories.common.components.buttons.inviteButton
import com.soyle.stories.common.components.buttons.primaryButton
import com.soyle.stories.common.components.surfaces.*
import com.soyle.stories.common.components.text.ToolTitle.Companion.toolTitle
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.character.Character
import com.soyle.stories.project.ProjectScope
import javafx.animation.Interpolator
import javafx.animation.Timeline
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.TextAlignment
import tornadofx.*
import java.lang.ref.WeakReference

class CharacterListView : View() {

    override val scope = super.scope as ProjectScope
    private val viewListener = resolve<CharacterListViewListener>()
    private val state = resolve<CharacterListState>()

    private val layoutStyle: SimpleObjectProperty<LayoutStyle> = SimpleObjectProperty(LayoutStyle.List)

    private val baseElevation
        get() = Elevation.getValue(4)

    override val root: Parent = stackpane {
        characterProfile().apply {
            elevation = baseElevation
            elevationVariant = elevated(objectProperty(Elevation.getValue(12)))
        }
        surface {
            alignment = Pos.CENTER
            spacing = 16.0
            parentProperty().onChangeWithCurrent { if (it != null) fitToParentSize() }
            vgrow = Priority.ALWAYS
            hgrow = Priority.SOMETIMES

            content()
        }
    }

    private fun EventTarget.characterProfile(): Node {

        val profileBeingViewed = SimpleObjectProperty<CharacterProfileProps?>(null)

        val profileScope = CharacterProfileScope(scope)
        val backdrop = pane {
            properties["com.soyle.stories.character.list.profile"] = profileBeingViewed
            existsWhen(profileBeingViewed.isNotNull)
            style { backgroundColor = multi(Color.rgb(0, 0, 0, 0.4)) }
        }
        backdrop.fitToParentSize()
        val profileContainer = backdrop.anchorpane {
            primaryButton("DONE") {
                anchorpaneConstraints {
                    topAnchor = 16.0
                    rightAnchor = 16
                }
                action {
                    state.profileBeingViewed.set(null)
                }
            }
            add(profileScope.get<CharacterProfileView>().apply {
                props.bind(profileBeingViewed)
                root.apply {
                    elevation = Elevation.getValue(16)
                    elevationVariant = outlined
                }
                root.anchorpaneConstraints {
                    topAnchor = 0
                    bottomAnchor = 0
                    leftAnchor = 0
                    rightAnchor = 0
                }
            })
            minHeight = 0.0
            clip = Rectangle().apply {
                heightProperty().bind(this@anchorpane.heightProperty())
                widthProperty().bind(this@anchorpane.widthProperty())
            }
        }
        profileContainer.fitToParentWidth()
        var currentAnimation: Timeline? = null

        fun openingAnimation(top: Double, height: Double) = timeline(play = false) {
            keyframe(0.seconds) {
                keyvalue(profileContainer.layoutYProperty(), top, null)
                keyvalue(profileContainer.prefHeightProperty(), height, null)
                keyvalue(profileContainer.maxHeightProperty(), height, null)
            }
            keyframe(0.3.seconds) {
                keyvalue(profileContainer.layoutYProperty(), 0.0, Interpolator.EASE_IN)
                keyvalue(profileContainer.prefHeightProperty(), backdrop.prefHeight, Interpolator.EASE_IN)
                keyvalue(profileContainer.maxHeightProperty(), backdrop.prefHeight, Interpolator.EASE_IN)
            }
            currentAnimation = this
        }

        fun closingAnimation(top: Double, height: Double) = timeline(play = false) {
            keyframe(0.seconds) {
                keyvalue(profileContainer.layoutYProperty(), 0.0, null)
                keyvalue(profileContainer.prefHeightProperty(), backdrop.prefHeight, null)
                keyvalue(profileContainer.maxHeightProperty(), backdrop.prefHeight, null)
            }
            keyframe(0.3.seconds) {
                keyvalue(profileContainer.layoutYProperty(), top, Interpolator.EASE_IN)
                keyvalue(profileContainer.prefHeightProperty(), height, Interpolator.EASE_IN)
                keyvalue(profileContainer.maxHeightProperty(), height, Interpolator.EASE_IN)
            }
            currentAnimation = this
            setOnFinished {
                if (currentAnimation == this) {
                    profileBeingViewed.set(null)
                }
            }
        }
        backdrop.scopedListener(state.profileBeingViewed) {
            currentAnimation?.stop()
            if (it == null) {
                val profileCharacterListNode = state.profileCharacterListNode.value
                if (profileCharacterListNode == null) {
                    closingAnimation(backdrop.prefHeight / 2, 0.0).playFromStart()
                } else {
                    val startTop =
                        backdrop.screenToLocal(profileCharacterListNode.localToScreen(profileCharacterListNode.boundsInLocal)).minY
                    closingAnimation(startTop, profileCharacterListNode.height).playFromStart()
                }
            } else {
                profileBeingViewed.set(object : CharacterProfileProps {
                    override val characterId: Character.Id = it.characterId
                    override val name: String = it.characterName
                    override val imageResource: String = it.imageResource
                })
                val profileCharacterListNode = state.profileCharacterListNode.value
                if (profileCharacterListNode == null) {
                    runLater {
                        openingAnimation(backdrop.prefHeight / 2, 0.0).playFromStart()
                    }
                } else {
                    val startTop =
                        backdrop.screenToLocal(profileCharacterListNode.localToScreen(profileCharacterListNode.boundsInLocal)).minY
                    runLater {
                        openingAnimation(startTop, profileCharacterListNode.height).playFromStart()
                    }
                }
            }
        }

        return backdrop
    }

    @ViewBuilder
    private fun VBox.content() {
        getChildList()?.clear()
        val characters = state.characters.value
        when {
            characters == null -> {
                loader()
                (state.characters as ObservableValue<*>).onChangeUntil({ it != null }) {
                    if (it != null) content()
                }
            }
            characters.isEmpty() -> {
                spacing = 16.0
                emptyCharacterList()
                (state.characters as ObservableValue<ObservableList<*>>).onChangeUntil({ it?.isEmpty() != true }) {
                    if (it?.isEmpty() != true) {
                        content()
                    }
                }
            }
            else -> {
                spacing = 0.0
                populatedCharacterList()
                (state.characters as ObservableValue<ObservableList<*>>).onChangeUntil({ it.isNullOrEmpty() }) {
                    if (it.isNullOrEmpty()) {
                        content()
                    }
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
            action { createCharacter(scope) }
        }
    }

    @ViewBuilder
    internal fun Parent.populatedCharacterList() {
        val header = header()
        val characterItemsProperty = characterItems()

        header.apply {
            elevation = baseElevation
        }
        header.scopedListener(characterItemsProperty) {
            it?.apply {
                elevation = baseElevation
                elevationVariant = elevated(objectProperty(Elevation.getValue(4)))
            }
        }
    }

    @ViewBuilder
    private fun Parent.header() = surface<HBox>(elevation = Elevation.getValue(8)) {
        alignment = Pos.CENTER_LEFT
        spacing = 8.0
        paddingAll = 8.0
        style {
            padding = box(8.px)
        }
        primaryButton("Create New Character") {
            id = "create_character_button"
            action { createCharacter(scope) }
        }
        spacer()
        menubutton("Options") {
            addClass(ComponentsStyles.primary)
            addClass(ComponentsStyles.outlined)
            hasArrow = false
            disableWhen(state.selectedCharacterListItem.isNull)

            scopedListener(state.selectedCharacterListItem) {
                when (it) {
                    is CharacterListState.CharacterListItem.CharacterItem -> items.setAll(
                        characterOptions(
                            scope,
                            state.profileBeingViewed::set,
                            it.character.value
                        )
                    )
                    is CharacterListState.CharacterListItem.ArcItem -> items.setAll(
                        characterArcOptions(
                            scope,
                            it.arc.value
                        )
                    )
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