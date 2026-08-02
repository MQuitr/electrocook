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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.mquitr.electrocookv2.data.mock.FakeRecipes
import ru.mquitr.electrocookv2.presentation.components.AllergensFilterCard
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

    var selectedAllergens by rememberSaveable {
        mutableStateOf(setOf<String>())
    }

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

    val allergens = remember {

        listOf(

            "Глютен",

            "Лактоза",

            "Яйца",

            "Орехи",

            "Рыба",

            "Морепродукты"
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

    val ingredientsSubtitle =
        if (searchByIngredients)
            "По ингредиентам"
        else
            "По названию"

    val caloriesSubtitle =
        when (caloriesFilter) {

            0 -> "Любая"

            1 -> "До 300 ккал"

            2 -> "До 500 ккал"

            3 -> "До 1000 ккал"

            else ->

                if (customCalories.isBlank())
                    "Ввести вручную"
                else
                    "До $customCalories ккал"
        }

    val cookingSubtitle =
        when (cookingTimeFilter) {

            0 -> "Любое"

            1 -> "До 15 минут"

            2 -> "До 30 минут"

            3 -> "До 60 минут"

            else ->

                if (customCookingTime.isBlank())
                    "Ввести вручную"
                else
                    "До $customCookingTime минут"
        }

    val servingsSubtitle =
        when (servingsFilter) {

            0 -> "Любые"

            1 -> "До 2"

            2 -> "До 4"

            3 -> "До 6"

            else ->

                if (customServings.isBlank())
                    "Ввести вручную"
                else
                    "До $customServings порций"
        }

    val allergensSubtitle =
        when {

            selectedAllergens.isEmpty() ->
                "Не выбраны"

            selectedAllergens.size == 1 ->
                selectedAllergens.first()

            else ->
                "${selectedAllergens.size} выбрано"
        }

    val recipes =
        FakeRecipes.recipes
            .filter { recipe ->

                if (searchQuery.isBlank()) {

                    true

                } else {

                    val titleMatch =
                        recipe.title.contains(
                            searchQuery,
                            ignoreCase = true
                        )

                    if (!searchByIngredients) {

                        titleMatch

                    } else {

                        val ingredientMatch =
                            recipe.ingredients.any { ingredient ->

                                ingredient.name.contains(
                                    searchQuery,
                                    ignoreCase = true
                                )
                            }

                        titleMatch || ingredientMatch
                    }
                }
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
            .filter { recipe ->

                selectedAllergens.isEmpty() ||

                        recipe.allergens.none {

                            it in selectedAllergens
                        }
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),

                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Фильтры",
                    style = MaterialTheme.typography.titleLarge
                )

                TextButton(

                    onClick = {

                        searchQuery = ""

                        searchByIngredients = false

                        caloriesFilter = 0
                        customCalories = ""

                        cookingTimeFilter = 0
                        customCookingTime = ""

                        servingsFilter = 0
                        customServings = ""

                        selectedAllergens = emptySet()
                    }

                ) {
                    Text(
                        text = "Сбросить",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                FilterSection(
                    title = "Ингредиенты",
                    subtitle = ingredientsSubtitle
                ) {
                    FilterItem(
                        title = "Поиск по ингредиентам",
                        checked = searchByIngredients,
                        onCheckedChange = {
                            searchByIngredients = it
                        }
                    )
                }

                FilterSection(
                    title = "Калорийность",
                    subtitle = caloriesSubtitle
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
                    title = "Время приготовления",
                    subtitle = cookingSubtitle
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
                    title = "Количество порций",
                    subtitle = servingsSubtitle
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
                    title = "Аллергены",
                    subtitle = allergensSubtitle
                ) {
                    AllergensFilterCard(
                        allergens = allergens,
                        selectedAllergens = selectedAllergens,
                        onSelectionChanged = {
                            selectedAllergens = it
                        }
                    )
                }
            }

            Text(
                text = "Результаты поиска",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Найдено рецептов: ${recipes.size}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}