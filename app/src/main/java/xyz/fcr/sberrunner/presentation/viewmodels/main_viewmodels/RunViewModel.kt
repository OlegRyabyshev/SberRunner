package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.ViewModel
import xyz.fcr.sberrunner.data.room.RunEntity
import javax.inject.Inject

class RunViewModel @Inject constructor() : ViewModel() {

    fun insertRun(run: RunEntity) {}

}