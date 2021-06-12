package com.soyle.stories.desktop.locale

import com.soyle.stories.desktop.locale.en.EnglishMessages
import java.util.*

object SoyleMessages {

    fun getLocale(locale: Locale): SoyleMessageBundle
    {
        // obviously, this only ever returns [english].  Can be expanded to include other locales later.
        if (locale === Locale.ENGLISH) return english

        else return english
    }

    val english: SoyleMessageBundle
        get() = EnglishMessages

}