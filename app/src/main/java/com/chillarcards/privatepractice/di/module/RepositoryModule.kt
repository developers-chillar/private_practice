package com.chillarcards.privatepractice.di.module

import com.chillarcards.privatepractice.data.api.ApiHelper
import com.chillarcards.privatepractice.data.api.ApiHelperImpl
import com.chillarcards.privatepractice.data.repository.AuthRepository
import org.koin.dsl.module

/**
 * @Author: Sherin Jaison
 * @Date: 01-11-2023
 * Chillar
 */
val repoModule = module {
    single {
        AuthRepository(get())
    }
    single<ApiHelper> {
        return@single ApiHelperImpl(get())
    }
}