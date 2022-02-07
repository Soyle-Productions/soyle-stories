package com.soyle.stories.desktop.view.testframework

import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import tornadofx.App
import tornadofx.osgi.registerApplication

class TestActivator : BundleActivator {
    override fun start(context: BundleContext) {
        context.registerApplication(DesignApplication::class)
    }

    override fun stop(context: BundleContext?) {
    }
}

class DesignApplication : App()