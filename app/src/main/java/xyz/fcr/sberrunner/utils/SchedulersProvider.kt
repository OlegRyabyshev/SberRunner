package xyz.fcr.sberrunner.utils


import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Получения объектов класса Scheduler
 *
 * @author Рябышев Олег on 05.08.2021
 */
class SchedulersProvider : SchedulersProviderInterface {
    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}