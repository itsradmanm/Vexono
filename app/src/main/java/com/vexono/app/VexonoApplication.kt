package com.vexono.app

import android.app.Application
import com.vexono.app.data.notification.NotificationHelper
import com.vexono.app.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VexonoApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationHelper.createNotificationChannels(this)

        // Preload occasions in background
        CoroutineScope(Dispatchers.IO).launch {
            container.occasionRepository.ensureOccasionsLoaded()
        }
    }
}
