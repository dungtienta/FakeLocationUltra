package com.dungtienta.www.fakelocationultra.injection.component

import com.dungtienta.www.fakelocationultra.injection.MapScope
import com.dungtienta.www.fakelocationultra.injection.module.ContextModule
import com.dungtienta.www.fakelocationultra.injection.module.MapsModule
import com.dungtienta.www.fakelocationultra.map.MapsPresenter
import dagger.Component

/**
 * Created by dungta on 3/15/18.
 */
@Component(modules = [(MapsModule::class), (ContextModule::class)])
@MapScope
interface MapsComponent
{
    fun inject(mapsPresenter: MapsPresenter)
}