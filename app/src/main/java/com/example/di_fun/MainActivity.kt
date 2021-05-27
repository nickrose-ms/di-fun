package com.example.di_fun

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.di_fun.databinding.MainFragmentBinding

class App : Application() {
    val appModule = AppModule()
}

class AppModule {
    val database: Database = StaticDatabase()
}

class MainModule(appModule: AppModule) {
    private val vmFactory: MainViewModel.Factory = MainViewModel.Factory(appModule.database)
    private val mainFragmentProvider: () -> MainFragment = { MainFragment(vmFactory) }
    val fragmentFactory: FragmentFactory = AppFragmentFactory(mainFragmentProvider)
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val appModule = (application as App).appModule
        val module = MainModule(appModule)
        supportFragmentManager.fragmentFactory = module.fragmentFactory
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment::class.java, null)
                .commit()
        }
    }
}

class AppFragmentFactory(
    private val mainFragmentProvider: () -> MainFragment
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val clazz = loadFragmentClass(classLoader, className)
        return when (clazz) {
            MainFragment::class.java -> mainFragmentProvider()
            else -> super.instantiate(classLoader, className)
        }
    }
}

class MainFragmentWrapper : Fragment(R.layout.main_activity) {
    override fun onAttach(context: Context) {
        val appModule = (context.applicationContext as App).appModule
        val module = MainModule(appModule)
        childFragmentManager.fragmentFactory = module.fragmentFactory
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment::class.java, null)
                .commit()
        }
    }
}

class MainFragment(
    private val vmFactory: MainViewModel.Factory
) : Fragment(R.layout.main_fragment) {
    private val viewModel by viewModels<MainViewModel> { vmFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = MainFragmentBinding.bind(view)
        binding.message.text = viewModel.numbers().toString()
    }
}

class MainViewModel(
    private val database: Database
) : ViewModel() {

    fun numbers() = database.numbers()

    class Factory(
        private val database: Database
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(database) as T
        }

    }
}

interface Database {
    fun numbers(): List<Int>
}

class StaticDatabase : Database {
    override fun numbers(): List<Int> {
        return listOf(1, 2, 3)
    }
}
