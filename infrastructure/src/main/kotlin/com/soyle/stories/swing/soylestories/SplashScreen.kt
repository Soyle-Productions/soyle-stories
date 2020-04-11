package com.soyle.stories.swing.soylestories

import java.awt.Dimension
import java.io.File
import javax.imageio.ImageIO
import javax.swing.OverlayLayout
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel

class SplashScreen {

	private val root: JFrame = JFrame().apply {
		isUndecorated = true
		defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE

		val layout = OverlayLayout(contentPane)
		contentPane.layout = layout

		val url = this@SplashScreen.javaClass.getResource("/com/soyle/stories/soylestories/splash.png")

		val label = JLabel(ImageIcon(url)).apply {

		}

		contentPane.add(label)
		layout.addLayoutComponent(label, Unit)

		pack()

		setLocationRelativeTo(null)
		isVisible = true
	}
}