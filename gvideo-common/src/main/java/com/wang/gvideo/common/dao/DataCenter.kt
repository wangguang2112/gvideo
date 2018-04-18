package com.wang.gvideo.common.dao

import com.wang.gvideo.common.utils.nil
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance

/**
 * Date:2018/4/15
 * Description:
 *
 * @author wangguang.
 */
class DataCenter {
    companion object {
        private val facade = DataCenter()
        fun instance(): DataCenter {
            return facade
        }
    }

    val daoCache = mutableMapOf<KClass<*>, ICollect<*>>()

    /**
     * 查询列表
     * @param model 对应的Dao实体类
     * @return 对应的需要映射的实体类的集合
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象作为model参数")
     */
    fun <S : Any> queryList(model: KClass<S>): Observable<List<S>> {
        return queryList(model, model, DefaultAdapter())
    }

    /**
     * 查询列表
     * @param model 对应的Dao实体类
     * @param origin 对应的需要映射的实体类
     * @param adapter 映射和Dao实体类的转换器
     * @return 对应的需要映射的实体类的集合
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象的类作为model参数")
     */
    fun <T : Any, S : Any> queryList(model: KClass<S>, origin: KClass<T>, adapter: IDaoAdapter<T, S>): Observable<List<T>> {
        var collect = getCacheCollect(model)
        var useAdapter = adapter
        return collect.queryList()
                .map { sList ->
                    val result = mutableListOf<T>()
                    sList.forEach { s ->
                        val r = useAdapter.reAdapt(s)
                        r?.let {
                            result.add(it)
                        }
                    }
                    result.toList()
                }

    }


    /**
     * 查询列表
     * @param model 对应的Dao实体类
     * @param key  需要筛选的值
     * @param sort 对应的排序的值
     * @return 对应的需要映射的实体类的集合
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象作为model参数")
     */
    fun <S : Any> queryWithConditionSort(model: KClass<S>, sort: String,key:String): Observable<List<S>> {
        return queryWithConditionSort(model, model, sort,key, DefaultAdapter())
    }

    /**
     * 查询列表
     * @param model 对应的Dao实体类
     * @param sort 对应的排序的值
     * @return 对应的需要映射的实体类的集合
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象作为model参数")
     */
    fun <S : Any> queryListWithSort(model: KClass<S>, sort: String): Observable<List<S>> {
        return queryWithConditionSort(model, model, sort,"", DefaultAdapter())
    }

    /**
     * 查询列表
     * @param model 对应的Dao实体类
     * @param sort 对应的排序的值
     * @return 对应的需要映射的实体类的集合
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象作为model参数")
     */
    fun <S : Any> queryListWithCondition(model: KClass<S>, condition: String): Observable<List<S>> {
        return queryWithConditionSort(model, model, "",condition, DefaultAdapter())
    }

    /**
     * 查询列表 排序的
     * @param model 对应的Dao实体类
     * @param origin 对应的需要映射的实体类
     * @param adapter 映射和Dao实体类的转换器
     * @param sort 对应的排序key
     * @param condition  需要筛选的值
     * @return 对应的需要映射的实体类的集合
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象的类作为model参数")
     */
    fun <T : Any, S : Any> queryWithConditionSort(model: KClass<S>, origin: KClass<T>, sort: String,condition:String, adapter: IDaoAdapter<T, S>): Observable<List<T>> {
        var collect = getCacheCollect(model)
        var useAdapter = adapter
        return collect.queryConditionSorted(sort,condition)
                .map { sList ->
                    val result = mutableListOf<T>()
                    sList.forEach { s ->
                        val r = useAdapter.reAdapt(s)
                        r?.let {
                            result.add(it)
                        }
                    }
                    result.toList()
                }
    }


    /**
     * 插入数据
     * @param model Dao实体对象
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象作为model参数")
     * @throws IllegalStateException("类型状态错误")
     */
    @Suppress("UNCHECKED_CAST")
    fun <S : Any> insert(model: S) {
        val cls = model::class as KClass<S>
        insert(cls, model, DefaultAdapter())
    }

    /**
     * 插入列表数据
     * @param model Dao实体对象
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象作为model参数")
     * @throws IllegalStateException("类型状态错误")
     */
    @Suppress("UNCHECKED_CAST")
    fun <S : Any> insertList(models: Iterable<S>) {
        if(models.iterator().hasNext()) {
            val cls = models.iterator().next()::class as KClass<S>
            insertList(cls, models, DefaultAdapter())
        }
    }

    /**
     * 插入数据
     * @param model 对应的Dao实体类
     * @param origin 对应的需要映射的实体对象
     * @param adapter 映射和Dao实体类的转换器
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象的类作为model参数")
     */
    fun <T : Any, S : Any> insert(model: KClass<S>, origin: T, adapter: IDaoAdapter<T, S>) {
        var collect = getCacheCollect(model)
        var useAdapter = adapter
        collect.insert(useAdapter.adapt(origin))
    }

    /**
     * 插入列表数据
     * @param model 对应的Dao实体类
     * @param origin 对应的需要映射的实体对象
     * @param adapter 映射和Dao实体类的转换器
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象的类作为model参数")
     */
    fun <T : Any, S : Any> insertList(model: KClass<S>, origin: Iterable<T>, adapter: IDaoAdapter<T, S>) {
        var collect = getCacheCollect(model)
        var useAdapter = adapter
        collect.insertList(origin.map { useAdapter.adapt(it) })
    }

    /**
     * 删除数据
     * @param model 对应的Dao实体类
     * @param key 需要删除数据的主键
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象的类作为model参数")
     */
    fun <S : Any> delete(model: KClass<S>, key: String) {
        var collect = getCacheCollect(model)
        collect.delete(key)
    }

    /**
     * 删除数据
     * @param model 对应的Dao实体类
     * @param condition 需要删除数据的条件
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象的类作为model参数")
     */
    fun <S : Any> deleteList(model: KClass<S>, condition: String) {
        var collect = getCacheCollect(model)
        collect.deleteList(condition)
    }

    /**
     * 查询数据
     * @param model 对应的Dao实体类
     * @param key 需要查询数据的主键
     * @return 对应的需要映射的实体对象
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象作为model参数")
     */
    fun <S : Any> query(model: KClass<S>, key: String): S? {
        return query(model, model, key, DefaultAdapter())
    }

    /**
     * 查询数据
     * @param model 对应的Dao实体类
     * @param origin 对应的需要映射的实体类
     * @param key 需要查询数据的主键
     * @param adapter 映射和Dao实体类的转换器
     * @return 对应的需要映射的实体对象
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象的类作为model参数")
     */
    fun <T : Any, S : Any> query(model: KClass<S>, origin: KClass<T>, key: String, adapter: IDaoAdapter<T, S>): T? {
        var collect = getCacheCollect(model)
        var useAdapter = adapter
        return useAdapter.reAdapt(collect.query(key))
    }


    /**
     * 查询数据是否存在
     * @param model 对应的Dao实体类
     * @param key 需要查询数据的主键
     * @return 是否已存在该数据
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象的类作为model参数")
     */
    fun <S : Any> exist(model: KClass<S>, key: String): Boolean {
        var collect = getCacheCollect(model)
        return collect.exist(key)
    }

    /**
     * @param model 对应的Dao实体类
     * @return 返回对应的数据获取辅助类
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象作为model参数")
     */
    private fun <S : Any> checkModelIlleagl(model: KClass<S>): IDao<S> {
        model.companionObject.nil {
            throw IllegalArgumentException("请使用实现IDao接口的伴生对象的类作为model参数")
        }
        return model.companionObjectInstance as? IDao<S> ?: throw IllegalArgumentException("请使用实现IDao接口的伴生对象的类作为model参数")
    }

    /**
     * @param model 对应的Dao实体类
     * @return 返回对应的处理接口
     * @throws IllegalArgumentException("请使用实现IDao接口的伴生对象作为model参数")
     */
    private fun <S : Any> getCacheCollect(model: KClass<S>): ICollect<S> {
        val com = checkModelIlleagl(model)
        return if (daoCache.containsKey(model)) {
            daoCache[model] as ICollect<S>
        } else {
            val newCollect = com.getCollect()
            daoCache.put(model, newCollect)
            newCollect
        }
    }

}