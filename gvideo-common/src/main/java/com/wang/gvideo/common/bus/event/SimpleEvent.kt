package com.wang.gvideo.common.bus.event


class SimpleEvent<T>(private val action: String, val attachObj: T) : Event {

    override fun action(): String {
        return action
    }
}
