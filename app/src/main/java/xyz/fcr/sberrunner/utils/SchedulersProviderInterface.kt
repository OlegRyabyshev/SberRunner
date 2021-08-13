package xyz.fcr.sberrunner.utils

import io.reactivex.rxjava3.core.Scheduler

/**
 * Интерфейс получения объектов класса Scheduler
 *
 * @author Рябышев Олег on 05.08.2021
 */
interface SchedulersProviderInterface {
    /**
     * Возвращает Scheduler для сетевых запросов.
     */
    fun io(): Scheduler

    /**
     * Возвращает Scheduler для обработки запросов в основном потоке.
     */
    fun ui(): Scheduler
}