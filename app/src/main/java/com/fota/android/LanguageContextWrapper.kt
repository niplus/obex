package com.fota.android

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import com.umeng.socialize.utils.DeviceConfig
import java.util.*


class LanguageContextWrapper(context: Context): ContextWrapper(context) {

    companion object{
        fun wrap(context: Context, local: Locale): ContextWrapper{

            val res: Resources = context.resources
            val configuration: Configuration = res.configuration

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(local)
                val localeList = LocaleList(local)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
                return ContextWrapper(context.createConfigurationContext(configuration))
            }

            return ContextWrapper(context)
        }
    }
}