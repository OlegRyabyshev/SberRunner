package xyz.fcr.sberrunner.utils.schedulers

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Получения объектов класса Scheduler
 */
class SchedulersProvider : ISchedulersProvider {

    /**
     * Возвращает Scheduler для i/o запросов
     */
    override fun io(): Scheduler {
        return Schedulers.io()
    }

    /**
     * Возвращает Scheduler для обработки запросов в основном потоке
     */
    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}