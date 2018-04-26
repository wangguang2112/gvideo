package com.wang.gvideo.common.bus


import com.wang.gvideo.common.bus.event.EmptyEvent
import com.wang.gvideo.common.bus.event.Event

import android.util.Log
import com.wang.gvideo.common.bus.event.SimpleEvent

import java.util.Arrays

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.subjects.BehaviorSubject
import rx.subjects.PublishSubject
import rx.subjects.SerializedSubject


class RxBus private constructor() {

    private val rxBus: SerializedSubject<Event, Event> = SerializedSubject(PublishSubject.create<Event>())

    private val rxStickBus: SerializedSubject<Event, Event> = SerializedSubject(BehaviorSubject.create<Event>())

    companion object {
        private val instance = RxBus()
        private val TAG = RxBus::class.simpleName

        fun instance(): RxBus {
            return instance
        }
    }

    fun postEvent(event: Event) {
        Log.d("RxEvent", "postEvent action : " + event.action())
        if (this.hasObservers()) {
            rxBus.onNext(event)
        }
    }

    fun postEmptyEvent(action: String) {
        Log.d("RxEvent", "postEmptyEvent action : " + action)
        if (this.hasObservers()) {
            rxBus.onNext(EmptyEvent(action))
        }
    }

    fun <T> postSingleEvent(action: String,obj: T) {
        Log.d("RxEvent", "postSingleEvent action : $action obj : $obj")
        if (this.hasObservers()) {
            rxBus.onNext(SimpleEvent(action,obj))
        }
    }
    fun postStickEvent(event: Event) {
        rxStickBus.onNext(event)
    }

    fun postStickEmptyEvent(action: String) {
        rxStickBus.onNext(EmptyEvent(action))
    }

    fun toObservable(action: String): Observable<Event> {
        return rxBus
                .asObservable()
                .filter { event -> event.action() == action }.onBackpressureBuffer()
    }

    fun toObservable(actions: List<String>?): Observable<Event> {

        if (actions == null) {
            throw NullPointerException("[RxBus.toObservable] actions cannot be null")
        }

        return rxBus
                .asObservable()
                .filter { event -> actions.contains(event.action()) }.onBackpressureBuffer()
    }

    fun toObservableOnMain(action: String): Observable<Event> {
        return toObservable(action).observeOn(AndroidSchedulers.mainThread())
    }

    fun toObservableOnMain(vararg actions: String): Observable<Event> {
        return toObservableOnMain(Arrays.asList(*actions))
    }

    fun toObservableOnMain(actions: List<String>): Observable<Event> {
        return toObservable(actions).observeOn(AndroidSchedulers.mainThread())
    }

    fun toStickObservable(action: String): Observable<Event> {
        return rxStickBus.asObservable()
                .filter { event -> event.action() == action }.onBackpressureBuffer()
    }

    fun toStickObservable(actions: List<String>?): Observable<Event> {

        if (actions == null) {
            throw NullPointerException("[RxBus.toStickObservable] actions cannot be null")
        }

        return rxStickBus.asObservable()
                .filter { event -> actions.contains(event.action()) }.onBackpressureBuffer()
    }

    fun toStickObservableOnMain(action: String): Observable<Event> {
        return toStickObservable(action).observeOn(AndroidSchedulers.mainThread())
    }

    fun toStickObservableOnMain(actions: List<String>): Observable<Event> {
        return toStickObservable(actions).observeOn(AndroidSchedulers.mainThread())
    }

    private fun hasObservers(): Boolean {
        return rxBus.hasObservers()
    }


}
