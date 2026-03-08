package com.example.safetynet

import com.example.safetynet.data.SafetyPinDatabase
import com.example.safetynet.data.auth.AuthRepository
import com.example.safetynet.data.auth.AuthRepositoryImpl
import android.content.Context
import com.example.safetynet.data.SafetyPinDao
import com.example.safetynet.data.UserDao
import com.example.safetynet.utils.NotificationHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Auth providers
    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore
//
//    @Provides
//    @Singleton
//    fun providesAuthRepositoryImp(firebaseAuth: FirebaseAuth) : AuthRepository {
//        return AuthRepositoryImpl(firebaseAuth = firebaseAuth)
//    }

    // Database providers
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : SafetyPinDatabase {
        return SafetyPinDatabase.getDatabase(context)
    }

    @Provides
    fun provideSafetyPinDao(database: SafetyPinDatabase) : SafetyPinDao {
        return database.safetyPinDao()
    }

    @Provides
    fun provideUserDao(database: SafetyPinDatabase): UserDao {
        return database.userDao()
    }

    // Location providers
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context) : FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }

//
//    @Provides
//    @Singleton
//    fun providesDatabaseRepositoryImp() : DatabaseRepository {
//        return DatabaseRepositoryImp()
//    }

}