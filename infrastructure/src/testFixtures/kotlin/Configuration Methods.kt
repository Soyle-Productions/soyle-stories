package com.soyle.stories.desktop.view

fun runHeadless()
{
    System.setProperty("testfx.robot", "glass")
    System.setProperty("testfx.headless", "true")
    System.setProperty("prism.order", "sw")
    System.setProperty("prism.text", "t2k")
    System.setProperty("java.awt.headless", "true")
    System.setProperty("headless.geometry", "1600x1200-32")
}