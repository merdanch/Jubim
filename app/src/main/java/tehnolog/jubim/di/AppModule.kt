package tehnolog.jubim.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import tehnolog.jubim.data.local.AppDatabase
import tehnolog.jubim.data.local.datastore.UIModeDataStore
import javax.inject.Singleton

@InstallIn(ActivityComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun providePreferenceManager(application: Application): UIModeDataStore {
        return UIModeDataStore(application.applicationContext)
    }

    @Singleton
    @Provides
    fun provideNoteDatabase(application: Application): AppDatabase {
        return AppDatabase.invoke(application.applicationContext)
    }
}
