package xyz.fcr.sberrunner.utils.schedulers

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Тесты на [SchedulersProvider]
 */
class SchedulersProviderTest {

    private val schedulersProvider: SchedulersProvider = SchedulersProvider()

    /**
     * Проверка возвращения Schedulers.io()
     */
    @Test
    fun receiveIo() {
        assertEquals(Schedulers.io(), schedulersProvider.io())
    }

    /**
     * Проверка возвращения AndroidSchedulers.mainThread()
     */
    @Test
    fun receiveUi() {
        assertEquals(AndroidSchedulers.mainThread(), schedulersProvider.ui())
    }
}