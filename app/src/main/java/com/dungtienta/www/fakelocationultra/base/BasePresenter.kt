package com.dungtienta.www.fakelocationultra.base

/**
 * Created by dungta on 3/15/18.
 */
abstract class BasePresenter<out V : BaseView>(protected val view: V)
{
    init {
        inject()
    }

    /**
     * This method may be called when the presenter view is created
     */
    open fun onViewCreated(){}

    /**
     * This method may be called when the presenter view is destroyed
     */
    open fun onViewDestroyed(){}

    /**
     * Injects the required dependencies
     */
    abstract fun inject()
}