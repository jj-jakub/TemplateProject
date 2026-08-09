package com.jj.templateproject.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.jj.templateproject.BaseInstrumentedKoinTest
import com.jj.templateproject.presentation.navigation.MainNavGraph
import com.jj.templateproject.presentation.ui.main.MainScreenViewModel
import com.jj.templateproject.presentation.ui.main.model.MainScreenViewState
import io.mockk.every
import io.mockk.mockkClass
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.mock.MockProvider
import org.koin.test.mock.declareMock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class MainNavGraphTest : BaseInstrumentedKoinTest<ComponentActivity>() {

    private val appModule = module {}

    override fun getInstrumentedTestModules(): List<Module> = listOf(appModule)

    @get:Rule(order = 2)
    override val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    init {
        MockProvider.register { clazz ->
            mockkClass(clazz, relaxed = true)
        }
    }

    @Test
    fun testMainScreenIsDisplayed() {
        declareMock<MainScreenViewModel> {
            every { viewState } returns MutableStateFlow(MainScreenViewState())
            every { navigation } returns MutableSharedFlow()
        }

        composeTestRule.setContent {
            val navController = rememberNavController()
            MainNavGraph(navController = navController)
        }

        composeTestRule.onNodeWithText("Navigate without optional args").assertIsDisplayed()
    }
}
