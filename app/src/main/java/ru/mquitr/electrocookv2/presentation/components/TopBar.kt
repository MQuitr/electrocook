package ru.mquitr.electrocookv2.presentation.components

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import ru.mquitr.electrocookv2.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    currentRoute: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPackagesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val speechLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val matches =
                    result.data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )

                matches?.firstOrNull()?.let {

                    onSearchQueryChange(it)
                }
            }
        }

    TopAppBar(

        title = {

            when {

                currentRoute == Screen.Home.route -> {

                    OutlinedTextField(
                        value = searchQuery,

                        onValueChange = {
                            onSearchQueryChange(it)
                        },

                        singleLine = true,

                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        },

                        trailingIcon = {

                            androidx.compose.foundation.layout.Row {

                                if (searchQuery.isNotEmpty()) {

                                    IconButton(
                                        onClick = {
                                            onSearchQueryChange("")
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Очистить поиск"
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {

                                        val intent = Intent(
                                            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                                        ).apply {

                                            putExtra(
                                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                            )

                                            putExtra(
                                                RecognizerIntent.EXTRA_LANGUAGE,
                                                "ru-RU"
                                            )

                                            putExtra(
                                                RecognizerIntent.EXTRA_PROMPT,
                                                "Назовите рецепт"
                                            )
                                        }

                                        speechLauncher.launch(intent)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Голосовой поиск"
                                    )
                                }
                            }
                        },

                        placeholder = {
                            Text("Поиск рецептов")
                        }
                    )
                }

                currentRoute == Screen.Favorites.route -> {
                    Text("Избранное")
                }

                currentRoute == Screen.Search.route -> {
                    Text("Поиск")
                }

                currentRoute == Screen.Packages.route -> {
                    Text("Пакеты рецептов")
                }

                currentRoute == Screen.Settings.route -> {
                    Text("Настройки")
                }

                currentRoute == Screen.About.route -> {
                    Text("О приложении")
                }

                else -> {
                    Text("ElectroCook")
                }
            }
        },

        actions = {

            IconButton(
                onClick = {
                    expanded = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }

            DropdownMenu(
                expanded = expanded,

                onDismissRequest = {
                    expanded = false
                }
            ) {

                DropdownMenuItem(
                    text = {
                        Text("Пакеты рецептов")
                    },

                    onClick = {
                        expanded = false
                        onPackagesClick()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Настройки")
                    },

                    onClick = {
                        expanded = false
                        onSettingsClick()
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("О приложении")
                    },

                    onClick = {
                        expanded = false
                        onAboutClick()
                    }
                )
            }
        }
    )
}