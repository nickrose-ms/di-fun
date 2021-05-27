package com.example.di_fun

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Test

class MainFragmentTest {
    @Test
    fun databaseNumbersAreCorrectlyDisplayed() {
        val database = FakeDatabase()
        val vmFactory = MainViewModel.Factory(database)
        launchFragmentInContainer { MainFragment(vmFactory) }

        onView(withId(R.id.message))
            .check(matches(withText("[100]")))
    }
}

class FakeDatabase : Database {
    override fun numbers(): List<Int> {
        return listOf(100)
    }
}
