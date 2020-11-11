package com.soyle.stories.desktop.view.theme.themeList

import com.soyle.stories.theme.themeList.ThemeList
import com.soyle.stories.theme.themeList.ThemeListItemViewModel
import javafx.scene.control.TreeItem
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull

class ThemeListAssert private constructor(private val themeList: ThemeList) {

    companion object {
        fun assertThat(themeList: ThemeList, assertions: ThemeListAssert.() -> Unit) {
            ThemeListAssert(themeList).assertions()
        }
    }

    private val driver by lazy { ThemeListDriver(themeList) }

    fun hasThemeNamed(themeName: String) {
        assertNotNull(driver.getThemeItem(themeName)) { "Theme List does not contain theme named $themeName" }
    }

    fun doesNotHaveThemeNamed(themeName: String) {
        assertNull(driver.getThemeItem(themeName)) { "Theme List contains theme named $themeName" }
    }

    fun andThemeItemNamed(themeName: String, assertions: ThemeItemAssert.() -> Unit) {
        ThemeItemAssert(driver.getThemeItemOrError(themeName)).assertions()
    }

    inner class ThemeItemAssert(private val themeItem: TreeItem<Any?>)
    {
        fun hasSymbolNamed(symbolName: String) {
            assertNotNull(driver.getSymbolItem(themeItem, symbolName)) { "Theme List should contain symbol named $symbolName" }
        }

        fun doesNotHaveSymbolNamed(symbolName: String) {
            assertNull(driver.getSymbolItem(themeItem, symbolName)) { "Theme List should not contain symbol named $symbolName" }
        }
    }
    /*
    andThemeItemNamed(themeName) {
                    hasSymbolNamed(symbolName)
                }
     */

}