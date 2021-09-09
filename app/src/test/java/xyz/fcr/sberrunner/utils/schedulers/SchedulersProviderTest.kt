package xyz.fcr.sberrunner.utils.schedulers

import io.mockk.spyk
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Assert
import org.junit.Test

/**
 * Тесты на [SchedulersProvider].
 */
class SchedulersProviderTest {

    private val schedulersProvider: SchedulersProvider = spyk()

    /**
     * Проверка возвращения Schedulers.io()
     */
    @Test
    fun receiveIo() {
        Assert.assertEquals(Schedulers.io(), schedulersProvider.io())
    }

    /**
     * Проверка возвращения AndroidSchedulers.mainThread()
     */
    @Test
    fun receiveUi() {
        Assert.assertEquals(AndroidSchedulers.mainThread(), schedulersProvider.ui())
    }
}