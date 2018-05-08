package com.wang.gvideo.migu.cache

import com.wang.gvideo.common.utils.allNotNil
import kotlin.properties.Delegates

/**
 * Date:2018/5/3
 * Description:
 *
 * @author wangguang.
 */
class CacheTask(private val url: String,
                private val img: String,
                private val name: String,
                private val contId: String,
                private val nodeId: String) : ITask {


    var state by Delegates.observable(ITask.STATE.STATE_PARPERA) { _, oldState: ITask.STATE, newState: ITask.STATE ->
        changeListener?.invoke(oldState, newState)
    }

    var size by Delegates.observable(0L) { _, _, new: Long ->
        sizeListener?.invoke(new)
    }
    var percent by Delegates.observable(0) { _, _, new: Int ->
        percentListener?.invoke(new)
    }

    var ts = 0;
    var path = ""

    var taskId = generateTaskId()

    var changeListener: ((oldState: ITask.STATE, newState: ITask.STATE) -> Unit)? = null
    var sizeListener: ((size: Long) -> Unit)? = null
    var percentListener: ((percent: Int) -> Unit)? = null

    override fun state(): ITask.STATE {
        return state
    }

    fun clearListener() {
        changeListener = null
        sizeListener = null
        percentListener = null
    }

    override fun percent(progress: Long) {
        ts = progress.toInt()
        if(size != 0L) {
            val newPrecent = (progress * 100 / size).toInt()
            if (newPrecent != percent) {
                percent = newPrecent
            }
        }else{
            percent = 0
        }
    }

    override fun size(size: Long) {
        this.size = size
    }

    override fun taskId(): String {
        return taskId
    }

    override fun path(): String {
        return path
    }

    override fun size(): Long {
        return size
    }

    fun generateTaskId(): String {
        return "${contId()}:${System.currentTimeMillis()}"
    }

    override fun getUrl(): String {
        return url
    }

    override fun getImg(): String {
        return img
    }

    override fun getName(): String {
        return name
    }

    override fun contId(): String {
        return contId
    }

    override fun nodeId(): String {
        return nodeId
    }

    fun builder(): Builder {
        return Builder()
    }

    class Builder {
        private var url: String? = null
        private var img: String? = null
        private var name: String? = null
        private var contId: String? = null
        private var nodeId: String? = null

        fun url(url: String): Builder {
            this.url = url
            return this
        }

        fun img(img: String): Builder {
            this.img = img
            return this
        }

        fun name(name: String): Builder {
            this.name = name
            return this
        }

        fun contId(contId: String): Builder {
            this.contId = contId
            return this
        }

        fun nodeId(nodeId: String): Builder {
            this.nodeId = nodeId
            return this
        }

        fun build(): ITask {
            if (allNotNil(url, img, name, contId)) {
                if(nodeId == null){
                    nodeId = contId
                }
                return CacheTask(url!!, img!!, name!!, contId!!, nodeId!!)
            } else {
                throw IllegalArgumentException("参数问题，请全部赋值")
            }
        }
    }
}