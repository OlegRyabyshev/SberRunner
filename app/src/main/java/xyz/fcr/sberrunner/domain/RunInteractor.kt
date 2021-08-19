package xyz.fcr.sberrunner.domain

import xyz.fcr.sberrunner.data.room.RunEntity
import xyz.fcr.sberrunner.data.room.RunRepository


class RunInteractor(
    private val runRepository: RunRepository,
) {

    fun getUsers(): List<RunEntity> {
        return runRepository.getListOfRuns()
    }

}