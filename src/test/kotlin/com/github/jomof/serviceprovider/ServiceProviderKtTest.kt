package com.github.jomof.serviceprovider

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.fail
import org.junit.Test

class ServiceProviderKtTest {
    data class MyService(val value : Int)

    @Test
    fun testNoService() {
        try {
            queryService(MyService::class)
            fail()
        } catch(e : RuntimeException) {
            // Expected
        }
    }

    @Test
    fun testPushService() {
        try {
            pushService(MyService(4), MyService::class)
            pushService(MyService(5), MyService::class)
            val service = queryService(MyService::class)
            assertThat(service.value).isEqualTo(5)
        } finally {
            popService(MyService::class)
            popService(MyService::class)
        }
    }

    @Test
    fun initializeService() {
        initializeService(MyService::class) {
            MyService(5)
        }
        initializeService(MyService::class) {
            MyService(4)
        }
        val service = queryService(MyService::class)
        assertThat(service.value).isEqualTo(5)
        popService(MyService::class)
    }
}

