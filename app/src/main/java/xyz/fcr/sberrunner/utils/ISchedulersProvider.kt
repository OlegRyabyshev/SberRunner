package xyz.fcr.sberrunner.utils

import io.reactivex.rxjava3.core.Scheduler

/**
 * Интерфейс получения объектов класса Scheduler
 */
interface ISchedulersProvider {
    /**
     * Возвращает Scheduler для сетевых запросов.
     */
    fun io(): Scheduler

    /**
     * Возвращает Scheduler для обработки запросов в основном потоке.
     */
    fun ui(): Scheduler
}