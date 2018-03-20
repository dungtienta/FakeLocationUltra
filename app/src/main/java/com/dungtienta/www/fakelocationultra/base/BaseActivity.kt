package com.dungtienta.www.fakelocationultra.base

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.support.v7.app.AppCompatActivity

/**
 * Created by dungta on 3/15/18.
 */
open class BaseActivity: AppCompatActivity(), BaseView
{
    override fun getContext(): Context
    {
        return this
    }

    protected fun goToDeveloperSettings()
    {
        startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
    }
}