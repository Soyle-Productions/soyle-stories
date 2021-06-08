package com.soyle.stories.location.locationDetails

import com.soyle.stories.common.*
import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.asyncMenuButton.AsyncMenuButton.Companion.asyncMenuButton
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.components.surfaces.*
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.components.surfaces.Surface.Companion.surface
import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.common.components.text.SectionTitle.Companion.section
import com.soyle.stories.common.components.text.SectionTitle.Companion.sectionTitle
import com.soyle.stories.common.components.text.TextStyles
import com.soyle.stories.di.resolveLater
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.text.TextAlignment
import com.soyle.stories.location.locationDetails.LocationDetailsStyles as Styles
import tornadofx.*

class LocationDetails : View() {

	override val scope: LocationDetailsScope = super.scope as LocationDetailsScope
	private val model by resolveLater<LocationDetailsModel>()
	private val viewListener by resolveLater<LocationDetailsViewListener>()

	override val root: Parent = locationDetailsRoot { /* this: Parent */ widthProperty ->
		descriptionField(widthProperty)
		hostedScenesList().apply {
			vgrow = Priority.ALWAYS
		}
	}

	private fun locationDetailsRoot(createChildren: Parent.(ObservableValue<Number>) -> Unit) = scrollpane {
		addClass(Styles.locationDetails)
		content = vbox {
			addClass(Stylesheet.content)
			createChildren(this@scrollpane.widthProperty())
		}
	}

	@ViewBuilder
	private fun Parent.descriptionField(rootWidthProperty: ObservableValue<Number>) {
		section(model.descriptionLabelProperty) {
			addClass(Styles.description)
			descriptionInput(rootWidthProperty)
		}
	}

	@ViewBuilder
	private fun Parent.descriptionInput(rootWidthProperty: ObservableValue<Number>) {
		textarea {
			id = "description"
			minWidth = Region.USE_COMPUTED_SIZE
			prefRowCountProperty().bind(rootWidthProperty.integerBinding {
				if (it != null && it.toInt() > 480) 10 else 5
			})
			prefRowCountProperty().onChange { parent?.requestLayout() }
			minHeight = Region.USE_PREF_SIZE
			textProperty().softBind(model.descriptionProperty) { it }
			focusedProperty().onChange {
				if (! it) {
					if (text != model.descriptionProperty.value) {
						viewListener.reDescribeLocation(text)
					}
				}
			}
		}
	}

	@ViewBuilder
	private fun Parent.hostedScenesList(): Node {
		return vbox {
			addClass(Styles.hostedScenesSection)
			dynamicContent(model.hostedScenesProperty.emptyProperty()) {
				hostedScenesListHeader(it == false)
				hostedScenesListBody(it == false).apply {
					vgrow = Priority.ALWAYS
				}
			}
		}
	}

	@ViewBuilder
	private fun Parent.hostedScenesListHeader(hasScenes: Boolean) {
		hbox {
			addClass(Stylesheet.header)
			sectionTitle("Hosted Scenes") {
				hgrow = Priority.ALWAYS
				maxWidth = Double.MAX_VALUE
			}
			if (hasScenes) {
				addSceneButton(secondary = true)
			}
		}
	}
	@ViewBuilder
	private fun Parent.hostedScenesListBody(hasScenes: Boolean): Node {
		return if (hasScenes) {
			hostedSceneItems()
		} else {
			addSceneInvitation()
		}.apply {

		}
	}
	@ViewBuilder
	private fun Parent.addSceneInvitation(): Node {
		return vbox {
			addClass(Styles.invitation)
			fieldLabel("Nothing has ever or will ever happen here.  Sounds boring.  Why not spice this place up by adding a scene or five?") {
				//maxWidth = Double.MAX_VALUE
			}
			addSceneButton()
		}
	}
	@ViewBuilder
	private fun Parent.addSceneButton(secondary: Boolean = false) {
		asyncMenuButton<AvailableSceneToHostViewModel> {
			root.apply {
				id = Styles.addScene.name
				text = "Add Scene"
				addClass(ComponentsStyles.outlined)
				addClass(ComponentsStyles.secondary)
				addClass(ButtonStyles.noArrow)
				if (!secondary) addClass(ButtonStyles.inviteButton)
			}
			sourceProperty.bind(model.availableScenesToHostProperty)
			itemsWhenLoaded { availableScenesToHost ->
				availableScenesToHost.map {
					MenuItem(it.sceneName).apply {
						id = it.sceneId.toString()
						action { viewListener.hostScene(it.sceneId) }
					}
				}
			}
			onLoad = {
				model.availableScenesToHostProperty.set(null)
				viewListener.getAvailableScenesToHost() }
		}
	}
	@ViewBuilder
	private fun Parent.hostedSceneItems(): Node {
		return flowpane {
			addClass(Styles.itemList)
			// inefficient because it completely rebuilds the list each time there's an update, but JavaFX or TornadoFx
			// doesn't want to play nice and wants to create a duplicate for the first scene when bindChildren or
			// children.bind is used, so we'll just use this until someone smarter than me can figure it out (or we move
			// to JetPack Compose)
			scopedListener(model.hostedScenesProperty) { hostedScenes ->
				children.setAll(hostedScenes.orEmpty().map { hostedScene ->
					Chip().apply {
						asSurface { inheritedElevation = Elevation.getValue(5) }
						id = hostedScene.sceneId.toString()
						textProperty().bind(hostedScene.nameProperty)
						addClass(Styles.hostedSceneItem)
						deleteGraphic = MaterialIconView(MaterialIcon.MORE_VERT)
						onDeleteProperty().bind(hoverProperty().objectBinding {
							if (it == true) EventHandler {
								val deleteGraphicScreenCoords = deleteGraphic!!.localToScreen(0.0, 0.0)
								hostedSceneMenu(hostedScene).show(deleteGraphic, deleteGraphicScreenCoords.x, deleteGraphicScreenCoords.y)
							}
							else null
						})
					}
				})
			}
		}
	}

	private fun hostedSceneMenu(hostedScene: HostedSceneItemViewModel): ContextMenu
	{
		return ContextMenu().apply {
			/*item("Remove Scene") {
				action { viewListener.removeHostedScene() }
			}*/
		}
	}

	init {
		titleProperty.bind(model.toolNameProperty)
		viewListener.getValidState()
	}
}
