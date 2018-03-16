package com.dungtienta.www.fakelocationultra.injection.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * Created by dungta on 3/15/18.
 */
@Module
class ContextModule(val context: Context)
{
    @Provides
    internal fun providesContext(): Context
    {
        return context
    }

    @Provides
    internal fun providesApplication(): Application
    {
        return context.applicationContext as Application
    }
}