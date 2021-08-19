package xyz.fcr.sberrunner.utils

enum class ServiceStatesEnum(val state: String) {
    NOT_WORKING("NOT_WORKING"),
    WORKING("WORKING"),
    PAUSED("PAUSED"),
    FINISHED("FINISHED")
}