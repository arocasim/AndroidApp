package com.example.myapplication.di

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.api.IDataSource
import com.example.myapplication.data.api.TestApiService
import com.example.myapplication.data.local.database.AppDatabase
import com.example.myapplication.data.repository.ArticleRepository
import com.example.myapplication.ui.MainContract
import com.example.myapplication.ui.presenter.MainPresenter

object DiHelper {

    private val retrofitHelper: RetrofitApiHelper by lazy {
        RetrofitApiHelper()
    }

    private val service: IDataSource by lazy {
        val api = retrofitHelper.retrofit.create(ApiService::class.java)
        TestApiService(api)
    }

    @Volatile
    private var database: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "article_database"
            )
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
                .also { database = it }
        }
    }

    fun getRepository(context: Context): ArticleRepository {
        return ArticleRepository(
            service = service,
            db = getDatabase(context)
        )
    }

    fun getPresenter(
        view: MainContract.View,
        context: Context
    ): MainContract.Presenter {
        return MainPresenter(
            view = view,
            repository = getRepository(context)
        )
    }
}