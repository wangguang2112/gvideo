package com.wang.gvideo.common.bus.event


class EmptyEvent(private val action: String) : Event {

    override fun action(): String {
        return action
    }
}
