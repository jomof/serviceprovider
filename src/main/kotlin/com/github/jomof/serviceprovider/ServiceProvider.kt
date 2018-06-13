package com.github.jomof.serviceprovider

import kotlin.reflect.KClass

interface ServiceProvider {
    fun <T:Any> queryService(clazz : KClass<T>) : T
}

class BasicServiceProvider : ServiceProvider {
    private val services : MutableMap<KClass<*>, MutableList<Any>> = mutableMapOf()
    override fun <T:Any> queryService(clazz : KClass<T>): T {
        val list = services[clazz]
        if (list == null || list.isEmpty()) {
            throw RuntimeException("Service $clazz not recognized")
        }
        @Suppress("UNCHECKED_CAST")
        return list[0] as T
    }

    fun <T:Any> pushService(instance : T, clazz : KClass<T>) {
        val list = services[clazz]
        if (list == null) {
            services[clazz] = mutableListOf()
            return pushService(instance, clazz)
        }
        list.add(0, instance as Any)
    }

    fun <T:Any> popService(clazz : KClass<T>) {
        val list = services[clazz]
        if (list == null || list.isEmpty()) {
            throw RuntimeException("Unrecognized service $clazz")
        }
        list.removeAt(0)
    }

    fun <T:Any> initializeService(clazz : KClass<T>, factory : () -> T) : Boolean {
        val list = services[clazz]
        if (list == null || list.isEmpty()) {
            pushService(factory(), clazz)
            return true
        }
        return false
    }
}

private val serviceProvider : BasicServiceProvider by lazy {
    BasicServiceProvider()
}

fun <T:Any> pushService(instance : T, clazz : KClass<T>) {
    serviceProvider.pushService(instance, clazz)
}

fun <T:Any> popService(clazz : KClass<T>) {
    serviceProvider.popService(clazz)
}

fun <T:Any> queryService(clazz : KClass<T>) : T {
    return serviceProvider.queryService(clazz)
}

fun <T:Any> initializeService(clazz : KClass<T>, factory : () -> T): Boolean{
    return serviceProvider.initializeService(clazz, factory)
}


