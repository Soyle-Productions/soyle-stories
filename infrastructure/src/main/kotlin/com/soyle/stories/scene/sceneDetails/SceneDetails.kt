package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.characterarc.components.characterNameModel
import com.soyle.stories.common.components.ComponentsStyles.Companion.notFirstChild
import com.soyle.stories.common.components.card
import com.soyle.stories.common.components.cardBody
import com.soyle.stories.common.components.cardHeader
import com.soyle.stories.common.components.fieldLabel
import com.soyle.stories.di.resolve
import javafx.css.converter.LadderConverter
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.stage.PopupWindow
import javafx.util.Duration
import tornadofx.*
import tornadofx.Stylesheet.Companion.label
import tornadofx.Stylesheet.Companion.labelContainer

class SceneDetails : View("Scene") {

	override val scope: SceneDetailsScope = super.scope as SceneDetailsScope
	private val viewListener = resolve<SceneDetailsViewListener>()
	private val model = resolve<SceneDetailsModel>()

	override val root: Parent = form {
		fieldset {
			textProperty.bind(model.locationSectionLabel)
			field {
				button {
					addClass("location-select")
					enableWhen {
						model.availableLocations.select {
							(!it.isNullOrEmpty() || model.selectedLocation.value != null).toProperty()
						}
					}
					textProperty().bind(model.itemProperty().select {
						(it?.selectedLocation?.name ?: it?.locationDropDownEmptyLabel).toProperty()
					})
					contextMenu = ContextMenu().apply {
						items.bind(model.availableLocations) {
							item(it.name) {
								action {
									viewListener.linkLocation(it.id)
								}
							}
						}
					}
					action {
						contextMenu?.show(this, Side.BOTTOM, 0.0, 0.0)
					}
				}
			}
		}
		stackpane {
			fitToParentWidth()
			fieldset(labelPosition = Orientation.VERTICAL) {
				textProperty.bind(model.charactersSectionLabel)
				fitToParentWidth()
				vbox {
					fitToParentWidth()
					bindChildren(model.includedCharacters) {
						card {
							cardHeader {
								label(it.characterName) {
									maxWidth = Double.MAX_VALUE
									hgrow = Priority.ALWAYS
								}
								button(model.removeCharacterButtonLabel) {
									hgrow = Priority.NEVER
									action {
										viewListener.removeCharacter(model.storyEventId.value!!, it.characterId)
									}
								}
							}
							cardBody {
								addClass(notFirstChild)
								spacing = 32.0
								vbox {
									isFillWidth = false
									fieldLabel("Position on Character's Arc")
									hbox {
										addClass("menu-button")
										addPseudoClass("openvertically")
										style {
											padding = box(4.px)
											backgroundColor += Color.WHITE
											borderColor += box(Color.GREY)
											borderWidth += box(1.px)
											borderRadius += box(4.px)
										}
										flowpane {
											label("Nothing selected")
										}
										stackpane {
											addClass("arrow-button")
											stackpane {
												addClass("arrow")
												maxWidth = Region.USE_PREF_SIZE
												maxHeight = Region.USE_PREF_SIZE
											}
										}
										setOnMousePressed { _ ->
											showAvailableCharacterArcSectionOptions(it.characterId, this)
										}
									}
								}
								vbox {
									fieldLabel("Motivation")
									vbox {
										hgrow = Priority.ALWAYS
										textfield(it.motivation) {
											focusedProperty().onChange { focused ->
												if (!focused && text != it.motivation) {
													viewListener.setMotivation(it.characterId, text)
												}
											}
										}
										hbox {
											hyperlink(model.resentButtonLabel) {
												addClass("reset-button")
												visibleWhen { it.canReset.toProperty() }
												managedProperty().bind(visibleProperty())
												action {
													viewListener.resetMotivation(it.characterId)
												}
											}
											hyperlink(model.lastChangedTipLabel) {
												addClass("previously-set-tip")
												visibleWhen { (it.previousMotivationSource != null).toProperty() }
												managedProperty().bind(visibleProperty())
												tooltip {
													style {
														backgroundColor += Color.WHITE
													}
													showDelay = Duration.INDEFINITE
													anchorLocation = PopupWindow.AnchorLocation.CONTENT_TOP_LEFT
													isAutoHide = true
													graphic = VBox().apply {
														hyperlink(it.previousMotivationSource?.sceneName ?: "") {
															action {
																this@tooltip.hide()
																it.previousMotivationSource?.sceneId?.let {
																	viewListener.openSceneDetails(it)
																}
															}
														}
														text(it.previousMotivationSource?.previousValue ?: "") {
															addClass("motivation")
														}
														setOnMouseExited { this@tooltip.hide() }
													}
												}
												action {
													val screenBounds = localToScreen(boundsInLocal)
													tooltip.show(this, screenBounds.minX, screenBounds.maxY)
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			menubutton {
				textProperty().bind(model.addCharacterButtonLabel)
				addClass("add-character")
				enableWhen {
					model.availableCharacters.select {
						(!it.isNullOrEmpty()).toProperty()
					}
				}
				stackpaneConstraints {
					alignment = Pos.TOP_RIGHT
				}
				items.bind(model.availableCharacters) {
					MenuItem(it.characterName, null).apply {
						action {
							viewListener.addCharacter(model.storyEventId.value!!, it.characterId)
						}
					}
				}
			}
		}
	}

	private val characterArcSectionOptionsMenu = ContextMenu().apply {
		isAutoHide = true
		isAutoFix = true
	}
	fun showAvailableCharacterArcSectionOptions(characterId: String, node: Node)
	{
		characterArcSectionOptionsMenu.items.clear()
		characterArcSectionOptionsMenu.item("Loading...") {
			isDisable = true
			graphic = progressindicator {
				maxWidth = 24.0
				maxHeight = 24.0
			}
		}
		characterArcSectionOptionsMenu.show(node, Side.BOTTOM, 0.0, 0.0)
		viewListener.getAvailableCharacterArcSections(characterId)
	}

	init {
		model.invalid.onChange {
			if (it != false) viewListener.getValidState()
		}
		if (model.invalid.value != false) {
			viewListener.getValidState()
		}
		Styles
	}

}

class Styles : Stylesheet()
{
	companion object {
		val noSelection by cssclass()

		init {
			importStylesheet(Styles::class)
		}
	}

	init {
		noSelection {
			focused {
				unsafe("-fx-accent", "transparent")
				unsafe("-fx-selection-bar", "transparent")

				label {
					unsafe("-fx-text-fill", "-fx-text-base-color")
				}
			}
		}
	}
}