package ru.mquitr.electrocookv2.presentation.screens.search

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mquitr.electrocookv2.data.mock.FakeRecipes
import ru.mquitr.electrocookv2.presentation.components.FilterOption
import ru.mquitr.electrocookv2.presentation.components.RecipeCard
import ru.mquitr.electrocookv2.presentation.components.FilterSection
import ru.mquitr.electrocookv2.presentation.components.NumericFilterCard

@Composable
fun SearchScreen(
    onRecipeClick: (Int) -> Unit = {}
) {

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    var searchByIngredients by remember { mutableStateOf(false) }

    var caloriesFilter by rememberSaveable {
        mutableIntStateOf(0)
    }

    var customCalories by rememberSaveable {
        mutableStateOf("")
    }

    var cookingTimeFilter by rememberSaveable {
        mutableIntStateOf(0)
    }

    var customCookingTime by rememberSaveable {
        mutableStateOf("")
    }

    var servingsFilter by rememberSaveable {
        mutableIntStateOf(0)
    }

    var customServings by rememberSaveable {
        mutableStateOf("")
    }

    var hideAllergens by remember { mutableStateOf(false) }

    val caloriesOptions = remember {
        listOf(
            FilterOption("Любая", null),
            FilterOption("До 300 ккал", 300),
            FilterOption("До 500 ккал", 500),
            FilterOption("До 1000 ккал", 1000),
            FilterOption("Ввести вручную", null)
        )
    }

    val cookingTimeOptions = remember {
        listOf(
            FilterOption("Любое", null),
            FilterOption("До 15 минут", 15),
            FilterOption("До 30 минут", 30),
            FilterOption("До 60 минут", 60),
            FilterOption("Ввести вручную", null)
        )
    }

    val servingsOptions = remember {
        listOf(
            FilterOption("Любое", null),
            FilterOption("До 2", 2),
            FilterOption("До 4", 4),
            FilterOption("До 6", 6),
            FilterOption("Ввести вручную", null)
        )
    }

    val speechLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val matches =
                    result.data?.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS
                    )

                matches?.firstOrNull()?.let {
                    searchQuery = it
                }
            }
        }

    val caloriesLimit =
        if (caloriesFilter == caloriesOptions.lastIndex)
            customCalories.toIntOrNull()
        else
            caloriesOptions[caloriesFilter].value

    val cookingTimeLimit =
        if (cookingTimeFilter == cookingTimeOptions.lastIndex)
            customCookingTime.toIntOrNull()
        else
            cookingTimeOptions[cookingTimeFilter].value

    val servingsLimit =
        if (servingsFilter == servingsOptions.lastIndex)
            customServings.toIntOrNull()
        else
            servingsOptions[servingsFilter].value

    val recipes =
        FakeRecipes.recipes
            .filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
            .filter {
                caloriesLimit == null || it.calories <= caloriesLimit
            }
            .filter {
                cookingTimeLimit == null || it.cookingTime <= cookingTimeLimit
            }
            .filter {
                servingsLimit == null || it.servings <= servingsLimit
            }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {

            OutlinedTextField(
                value = searchQuery,

                onValueChange = {
                    searchQuery = it
                },

                modifier = Modifier.fillMaxWidth(),

                singleLine = true,

                placeholder = {
                    Text("Введите название рецепта")
                },

                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },

                trailingIcon = {

                    Row {

                        if (searchQuery.isNotEmpty()) {

                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                }
                            ) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Очистить"
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
                                Icons.Default.Mic,
                                contentDescription = "Голосовой поиск"
                            )
                        }
                    }
                }
            )

            Text(
                text = "Фильтры",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                FilterSection(
                    title = "Ингредиенты"
                ) {
                    FilterItem(
                        title = "Искать по ингредиентам",
                        checked = searchByIngredients,
                        onCheckedChange = {
                            searchByIngredients = it
                        }
                    )
                }

                FilterSection(
                    title = "Калорийность"
                ) {
                    NumericFilterCard(
                        options = caloriesOptions,
                        selectedIndex = caloriesFilter,
                        customValue = customCalories,
                        maxValue = 20000,
                        customLabel = "Калории",
                        onSelectedChange = {
                            caloriesFilter = it
                        },
                        onCustomValueChange = {
                            customCalories = it
                        }
                    )
                }

                FilterSection(
                    title = "Время приготовления"
                ) {

                    NumericFilterCard(
                        options = cookingTimeOptions,
                        selectedIndex = cookingTimeFilter,
                        customValue = customCookingTime,
                        maxValue = 1440,
                        customLabel = "Минут",
                        onSelectedChange = {
                            cookingTimeFilter = it
                        },
                        onCustomValueChange = {
                            customCookingTime = it
                        }
                    )
                }

                FilterSection(
                    title = "Количество порций"
                ) {

                    NumericFilterCard(
                        options = servingsOptions,
                        selectedIndex = servingsFilter,
                        customValue = customServings,
                        maxValue = 100,
                        customLabel = "Порций",
                        onSelectedChange = {
                            servingsFilter = it
                        },
                        onCustomValueChange = {
                            customServings = it
                        }
                    )
                }

                FilterSection(
                    title = "Аллергены"
                ) {
                    FilterItem(
                        title = "Без аллергенов",
                        checked = hideAllergens,
                        onCheckedChange = {
                            hideAllergens = it
                        }
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(
                text = "Найдено рецептов: ${recipes.size}",
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(recipes) { recipe ->

            RecipeCard(
                recipe = recipe,
                onClick = {
                    onRecipeClick(recipe.id)
                }
            )
        }
    }
}

@Composable
private fun FilterItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}