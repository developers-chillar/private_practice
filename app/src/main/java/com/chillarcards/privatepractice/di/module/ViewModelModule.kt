package com.chillarcards.privatepractice.di.module

import com.chillarcards.privatepractice.viewmodel.GeneralViewModel
import com.chillarcards.privatepractice.viewmodel.ProfileViewModel
import com.chillarcards.privatepractice.viewmodel.RegisterViewModel
import com.chillarcards.privatepractice.viewmodel.WorkViewModel
import com.chillarcards.privatepractice.viewmodel.*
import org.koin.dsl.module
import kotlin.math.sin


/**
 * @Author: Sherin Jaison
 * @Date: 01-11-2023
 * Chillar
 */
val viewModelModule = module {
    single {
        RegisterViewModel(get(), get())
    }
    single {
        ProfileViewModel(get(), get())
    }
    single {
        WorkViewModel(get(), get())
    }
    single {
        GeneralViewModel(get(), get())
    }
    single {
        BankViewModel(get(), get())
    }
    single {
        BookingViewModel(get(), get())
    }
    single {
        ReportViewModel(get(), get())
    }
    single {
        StatusViewModel(get(), get())
    }
    single { MobileScreenViewModel(get(),get()) }
}