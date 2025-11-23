package com.example.carebuddy.di

import android.content.Context
import androidx.room.Room
import com.example.carebuddy.data.local.CareBuddyDatabase
import com.example.carebuddy.data.local.FoodLogDao
import com.example.carebuddy.data.local.HydrationDao
import com.example.carebuddy.data.local.MoodDao
import com.example.carebuddy.data.repositories.FoodRepository
import com.example.carebuddy.data.repositories.HydrationRepository
import com.example.carebuddy.data.repositories.MoodRepository
import com.example.carebuddy.data.remote.AiApi
import com.example.carebuddy.data.remote.NutritionBackendApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import com.google.firebase.auth.FirebaseAuth


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- OkHttp ---
    @Provides
    @Singleton
    @Named("okhttp_debug")
    fun provideDebugOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // --- Retrofit (AI + Nutrition backend same baseURL) ---
    @Provides
    @Singleton
    @Named("ai")
    fun provideAiRetrofit(@Named("okhttp_debug") client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("backend")
    fun provideBackendRetrofit(
        @Named("okhttp_debug") client: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAiApi(@Named("ai") aiRetrofit: Retrofit): AiApi =
        aiRetrofit.create(AiApi::class.java)
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideNutritionBackendApi(
        @Named("backend") backendRetrofit: Retrofit
    ): NutritionBackendApi =
        backendRetrofit.create(NutritionBackendApi::class.java)

    // --- ROOM: CareBuddyDatabase + DAOs ---
    @Provides
    @Singleton
    fun provideCareBuddyDatabase(
        @ApplicationContext context: Context
    ): CareBuddyDatabase =
        Room.databaseBuilder(
            context,
            CareBuddyDatabase::class.java,
            "carebuddy.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideHydrationDao(db: CareBuddyDatabase): HydrationDao = db.hydrationDao()

    @Provides
    @Singleton
    fun provideFoodDao(db: CareBuddyDatabase): FoodLogDao = db.foodDao()

    @Provides
    @Singleton
    fun provideMoodDao(db: CareBuddyDatabase): MoodDao = db.moodDao()

    // --- Repositories ---
    @Provides
    @Singleton
    fun provideHydrationRepository(dao: HydrationDao): HydrationRepository =
        HydrationRepository(dao)

    @Provides
    @Singleton
    fun provideFoodRepository(dao: FoodLogDao): FoodRepository =
        FoodRepository(dao)

    @Provides
    @Singleton
    fun provideMoodRepository(dao: MoodDao): MoodRepository =
        MoodRepository(dao)
}





