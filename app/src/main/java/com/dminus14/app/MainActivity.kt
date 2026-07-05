package com.dminus14.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.dminus14.app.navigation.AppNavigationState
import com.dminus14.app.ui.theme.DMinus14Theme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigationState: AppNavigationState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DMinus14Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavDisplay(
                        backStack = navigationState.navigator.backStack,
                        onBack = navigationState.navigator::goBack,
                        modifier = Modifier.padding(innerPadding),
                        entryProvider =
                            entryProvider {
                                navigationState.entryInstallers.forEach { installer -> installer() }
                            },
                    )
                }
            }
        }
    }
}
