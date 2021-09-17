package xyz.fcr.sberrunner.data.converter

import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.domain.model.Run

/**
 * Класс конвертации забегов слоя data <-> presentation
 */
class RunConverter {

    /**
     * Конвертация Run в RunEntity
     *
     * @param run [Run] - забег слоя presentation
     *
     * @return [RunEntity] - забег слоя data
     */
    fun toRunEntity(run: Run): RunEntity {
        return RunEntity(
            avgSpeedInKMH = run.avgSpeedInKMH,
            calories = run.calories,
            distanceInMeters = run.distanceInMeters,
            timeInMillis = run.timeInMillis,
            timestamp = run.timestamp,
            toDeleteFlag = run.toDeleteFlag,
            mapImage = run.mapImage
        )
    }

    /**
     * Конвертация RunEntity в Run
     *
     * @param run [RunEntity] - забег слоя data
     *
     * @return [Run] - забег слоя presentation
     */
    fun toRun(run: RunEntity): Run {
        return Run(
            avgSpeedInKMH = run.avgSpeedInKMH,
            calories = run.calories,
            distanceInMeters = run.distanceInMeters,
            timeInMillis = run.timeInMillis,
            timestamp = run.timestamp,
            toDeleteFlag = run.toDeleteFlag,
            mapImage = run.mapImage
        )
    }

    /**
     * Конвертация списка Run в список RunEntity
     *
     * @param list [List] - список забегов слоя presentation
     *
     * @return [List] - список забегов слоя data
     */
    fun toRunEntityList(list: List<Run>): List<RunEntity> {
        return list.map { toRunEntity(it) }
    }

    /**
     * Конвертация списка RunEntity в список Run
     *
     * @param list [List] - список забегов слоя data
     *
     * @return [List] - список забегов слоя presentation
     */
    fun toRunList(list: List<RunEntity>): List<Run> {
        return list.map { toRun(it) }
    }
}