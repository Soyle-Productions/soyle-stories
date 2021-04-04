package com.soyle.stories.soylestories

import com.soyle.stories.common.components.ComponentsStyles
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import tornadofx.*

class modena(val base: Color = modena.base) {
    companion object {
        /** A light grey that is the base color for objects.  Instead of using
         * -fx-base directly, the sections in this file will typically use -fx-color.
         */
        val base = Color.web("#ececec")

        val standard = modena()

        fun hover(base: Color) = base.ladder(
            Stop(0.2, base.derive(0.2)),
            Stop(0.35, base.derive(0.3)),
            Stop(0.50, base.derive(0.4))
        )
    }

    /** A very light grey used for the background of windows.  See also
     * -fx-text-background-color, which should be used as the -fx-text-fill
     * value for text painted on top of backgrounds colored with -fx-background.
     */
    val background = base.derive(0.264)

    /** Used for the inside of text boxes, password boxes, lists, trees, and
     * tables.  See also -fx-text-inner-color, which should be used as the
     * -fx-text-fill value for text painted on top of backgrounds colored
     * with -fx-control-inner-background.
     */
    val controlInnerBackground = base.derive(0.8)

    /** Version of -fx-control-inner-background for alternative rows */
    val controlInnerBackgroundAlt = controlInnerBackground.derive(-0.02);

    /** One of these colors will be chosen based upon a ladder calculation
     * that uses the brightness of a background color.  Instead of using these
     * colors directly as -fx-text-fill values, the sections in this file should
     * use a derived color to match the background in use.  See also:
     *
     * -fx-text-base-color for text on top of -fx-base, -fx-color, and -fx-body-color
     * -fx-text-background-color for text on top of -fx-background
     * -fx-text-inner-color for text on top of -fx-control-inner-color
     * -fx-selection-bar-text for text on top of -fx-selection-bar
     */
    val darkTextColor = Color.BLACK;
    val midTextColor = Color.web("#333");
    val lightTextColor = Color.WHITE;

    /** A bright blue for highlighting/accenting objects.  For example: selected
     * text; selected items in menus, lists, trees, and tables; progress bars */
    val accent = Color.web("#0096C9");

    /** Default buttons color, this is similar to accent but more subtle */
    val defaultButton = Color.web("#ABD8ED");

    /** A bright blue for the focus indicator of objects. Typically used as the
     * first color in -fx-background-color for the "focused" pseudo-class. Also
     * typically used with insets of -1.4 to provide a glowing effect.
     */
    val focusColor = Color.web("#039ED3");
    val faintFocusColor = Color.web("#039ED322");

    /** The color that is used in styling controls. The default value is based
     * on [base], but is changed by pseudoclasses to change the base color.
     * For example, the "hover" pseudoclass will typically set [color] to
     * [hoverBase] (see below) and the "armed" pseudoclass will typically
     * set [color] to [pressedBase].
     */
    val color = base;

    /** A little lighter than -fx-base and used as the -fx-color for the
     * "hovered" pseudoclass state.
     */
    val hoverBase = hover(base)

    /** A little darker than -fx-base and used as the -fx-color for the
     * "armed" pseudoclass state.
     *
     */
    val pressedBase = base.derive(-0.06);

    /* The color to use for -fx-text-fill when text is to be painted on top of
     * a background filled with the -fx-background color.
     */
    val textBackgroundColor = background.ladder(
        Stop(0.45, lightTextColor),
        Stop(0.46, darkTextColor),
        Stop(0.59, darkTextColor),
        Stop(0.60, midTextColor)
    );

    /* A little darker than -fx-color and used to draw boxes around objects such
     * as progress bars, scroll bars, scroll panes, trees, tables, and lists.
     */
    val boxBorder = color.ladder(
        Stop(0.2, Color.BLACK),
        Stop(0.3, color.derive(-0.15))
    );

    /* Darker than -fx-background and used to draw boxes around text boxes and
     * password boxes.
     */
    val textBoxBorder = background.ladder(
        Stop(0.1, Color.BLACK),
        Stop(0.3, background.derive(-0.15))
    );

    /* Lighter than -fx-background and used to provide a small highlight when
     * needed on top of -fx-background. This is never a shadow in Modena but
     * keep -fx-shadow-highlight-color name to be compatible with Caspian.
     */
    val shadowHighlightColor = background.ladder(
        Stop(0.0, Color.rgb(255, 255, 255, 0.07)),
        Stop(0.2, Color.rgb(255, 255, 255, 0.07)),
        Stop(0.7, Color.rgb(255, 255, 255, 0.07)),
        Stop(0.9, Color.rgb(255, 255, 255, 0.7)),
        Stop(1.0, Color.rgb(255, 255, 255, 0.75))
    );

    /* A gradient that goes from a little darker than -fx-color on the top to
     * even more darker than -fx-color on the bottom.  Typically is the second
     * color in the -fx-background-color list as the small thin border around
     * a control. It is typically the same size as the control (i.e., insets
     * are 0).
     */
    val outerBorder = color.derive(-0.23);

    /* A gradient that goes from a bit lighter than -fx-color on the top to
     * a little darker at the bottom.  Typically is the third color in the
     * -fx-background-color list as a thin highlight inside the outer border.
     * Insets are typically 1.
     */
    val innerBorder = LinearGradient(
        0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE,
        Stop(
            0.0, color.ladder(
                Stop(0.0, color.derive(0.3)),
                Stop(0.4, color.derive(0.2)),
                Stop(0.6, color.derive(0.25)),
                Stop(0.8, color.derive(0.55)),
                Stop(0.9, color.derive(0.55)),
                Stop(1.0, color.derive(0.75))
            )
        ),
        Stop(
            1.0, color.ladder(
                Stop(0.0, color.derive(0.2)),
                Stop(0.2, color.derive(0.1)),
                Stop(0.4, color.derive(0.05)),
                Stop(0.6, color.derive(-0.02)),
                Stop(1.0, color.derive(-0.05))
            )
        )
    )
    val innerBorderHorizontal = LinearGradient(
        0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE,
        Stop(0.0, color.derive(0.55)),
        Stop(1.0, color.derive(-0.05))
    )
    val innerBorderBottomUp = LinearGradient(
        0.0, 1.0, 0.0, 0.0, true, CycleMethod.NO_CYCLE,
        Stop(0.0, color.derive(0.55)),
        Stop(1.0, color.derive(-0.05))
    )

    /* A gradient that goes from a little lighter than -fx-color at the top to
     * a little darker than -fx-color at the bottom and is used to fill the
     * body of many controls such as buttons.
     */
    val bodyColor = LinearGradient(
        0.0, 0.0, 0.0, 1.0, true, CycleMethod.NO_CYCLE,
        Stop(0.0, color.ladder(
            Stop(0.75, color.derive(0.08)),
            Stop(1.8, color.derive(0.1))
        )),
        Stop(1.0, color.derive(-0.08))
    )/*
    -fx-body-color-bottomup: linear-gradient(to top, derive(-fx-color,10%) ,derive(-fx-color,-6%));
    -fx-body-color-to-right: linear-gradient(to right, derive(-fx-color,10%) ,derive(-fx-color,-6%));

    /* The color to use as -fx-text-fill when painting text on top of
     * backgrounds filled with -fx-base, -fx-color, and -fx-body-color.
     */
    -fx-text-base-color: ladder(
    -fx-color,
    -fx-light-text-color 45%,
    -fx-dark-text-color  46%,
    -fx-dark-text-color  59%,
    -fx-mid-text-color   60%
    );

    /* The color to use as -fx-text-fill when painting text on top of
     * backgrounds filled with -fx-control-inner-background.
     */
    -fx-text-inner -color: ladder(
    -fx-control-inner -background,
    -fx-light-text-color 45%,
    -fx-dark-text-color  46%,
    -fx-dark-text-color  59%,
    -fx-mid-text-color   60%
    );

    /* The color to use for small mark-like objects such as checks on check
     * boxes, filled in circles in radio buttons, arrows on scroll bars, etc.
     */
    -fx-mark-color: ladder(
    -fx-color,
    white 30%,
    derive(-fx-color,-63%) 31%
    );

    /* The small thin light "shadow" for mark-like objects. Typically used in
     * conjunction with -fx-mark-color with an insets of 1 0 -1 0. */
    -fx-mark-highlight-color: ladder(
    -fx-color,
    derive(-fx-color,80%) 60%,
    white 70%
    );

    /* Background for items in list like things such as menus, lists, trees,
     * and tables. */
    -fx-selection-bar: -fx-accent;

    /* Background color to use for selection of list cells etc. This is when
     * the control doesn't have focus or the row of a previously selected item. */
    -fx-selection-bar-non-focused: lightgrey;

    /* The color to use as -fx-text-fill when painting text on top of
     * backgrounds filled with -fx-selection-bar.
     *
     */
    -fx-selection-bar-text: -fx-text-background-color;

    /* These are needed for Popup */
    -fx-background-color: inherit;
    -fx-background-radius: inherit;
    -fx-background-insets: inherit;
    -fx-padding: inherit;

    /* The color to use in ListView/TreeView/TableView to indicate hover. */
    -fx-cell-hover-color: #cce3f4;

    /** Focus line for keyboard focus traversal on cell based controls */
    -fx-cell-focus-inner -border: derive(-fx-selection-bar,30%);

    /* The colors to use in Pagination */
    -fx-page-bullet-border: #acacac;
    -fx-page-indicator-hover-border: #accee5;

    -fx-focused-text-base-color : ladder(
    -fx-selection-bar,
    -fx-light-text-color 45%,
    -fx-dark-text-color 46%,
    -fx-dark-text-color 59%,
    -fx-mid-text-color 60%
    );
    -fx-focused-mark-color : -fx-focused-text-base-color;*/
}

class Styles : Stylesheet() {

    companion object {

        val Purple = Color.web("#862F89")
        val Orange = Color.web("#D38147")
        val Blue = Color.web("#3A518E")

        val appIcon = Image("com/soyle/stories/soylestories/icon.png")
        val logo = Image("com/soyle/stories/soylestories/bronze logo.png")

        init {
            loadFont("/com/soyle/stories/soylestories/corbel/CORBEL.TTF", 14)!!
            loadFont("/com/soyle/stories/soylestories/corbel/CORBELB.TTF", 14)!!

            ComponentsStyles // reference to initialize
        }

        val section by cssclass()

    }

    init {
        root {
            accentColor = Blue
            focusColor = Purple
        }
        section {
            padding = box(0.px, 0.px, 15.px, 0.px)
        }
        scrollPane {
            backgroundColor = multi(Color.TRANSPARENT) // Why would it have a border?
        }
        tooltip {
            backgroundColor = multi(Color.WHITE)
            textFill = Color.BLACK
        }
    }

}