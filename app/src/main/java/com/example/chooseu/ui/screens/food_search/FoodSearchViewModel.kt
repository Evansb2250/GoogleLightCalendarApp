package com.example.chooseu.ui.screens.food_search

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chooseu.core.MealType
import com.example.chooseu.core.cache.FoodItemCache
import com.example.chooseu.data.rest.api_service.dtos.nutrition_dto_extensions.toUniqueFoodItems
import com.example.chooseu.di.VMAssistFactoryModule
import com.example.chooseu.navigation.components.destinations.BottomNavBarDestinations
import com.example.chooseu.navigation.components.destinations.GeneralDestinations
import com.example.chooseu.navigation.components.destinations.destinationArguments.DiaryArgs
import com.example.chooseu.navigation.components.navmanagers.MainFlowNavManager
import com.example.chooseu.repo.foodRepository.FoodRepository
import com.example.chooseu.ui.screens.food_search.states.FoodSearchStates
import com.example.chooseu.ui.screens.nutrition_screen.FoodItem
import com.example.chooseu.ui.screens.nutrition_screen.toUserMealEntry
import com.example.chooseu.utils.AsyncResponse
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException

@HiltViewModel(
    assistedFactory = VMAssistFactoryModule.FoodSearchFactory::class
)
class FoodSearchViewModel @AssistedInject constructor(
    private val foodRepository: FoodRepository,
    private val navManager: MainFlowNavManager,
    private val foodItemCache: FoodItemCache,
    @Assisted private val userId: String,
    @Assisted private val date: Long,
    @Assisted private val mealType: MealType?,
) : ViewModel() {

    private var searchJob: Job? = null

    private val _state: MutableStateFlow<FoodSearchStates.LoggingFoodItem> =
        MutableStateFlow(FoodSearchStates.LoggingFoodItem())
    val state: StateFlow<FoodSearchStates.LoggingFoodItem> = _state.asStateFlow()

    init {
        if (mealType == null) {
            _state.value
        }
    }

    fun navigateBackToFoodDiary() {
        navManager.navigate(
            BottomNavBarDestinations.Diary,
            mapOf(
                DiaryArgs.LONG_DATE.name to "$date"
            )
        )
    }

    fun reset() {
        _state.update { FoodSearchStates.LoggingFoodItem() }
    }

    fun updateFoodItemList(foodItem: FoodItem) {
        viewModelScope.launch {
            foodRepository.saveFoodDetails(
                foodItem.toUserMealEntry(
                    userId = userId,
                    date = date,
                    mealType = mealType!!,
                    quantity = 1.0
                )
            )
        }
    }


    fun updateSearchText(foodName: String) {
        searchJob?.cancel() // Cancel previous job if it exists
        if (mealType == null) {
            setErrorState(
                message = "Mealtype category missing",
                errorOrigin = "on dependency injection"
            )
        } else {
            searchJob = viewModelScope.launch {
                _state.update {
                    it.copy(userInput = foodName)
                }
                throttleNetworkCall()
            }
        }
    }

    private suspend fun throttleNetworkCall() {
        state.debounce(1000)
            .distinctUntilChanged()
            .catch {
                setErrorState(it.message ?: "error in throttling", "ThrottleNetwork debounce")
            }
            .collectLatest {
                if (
                    it.userInput.isNotEmpty() &&
                    it.searchedText != it.userInput
                ) {
                    makeNetworkCall(it.userInput)
                } else if (it.userInput.isEmpty()) {
                    reset()
                }
            }
    }

    fun viewNutrientDetails(foodId: String) {
        try {
            val foodItem =
                _state.value.foodItemsFound.value.firstOrNull { it.foodId == foodId } ?: return
            foodItemCache.map[foodId] = foodItem

            navManager.navigate(
                GeneralDestinations.Nutrition,
                mapOf(
                    "foodId" to foodId,
                    "userId" to userId,
                    "${DiaryArgs.LONG_DATE.name}" to date.toString()
                )
            )

        } catch (e: SerializationException) {
            setErrorState("SerializationException", "serialization error viewNurienDeails")
        } catch (e: IllegalArgumentException) {
            setErrorState("IllegalArgumentException", "IllegalArg viewNutrientDetails")
        }
    }


    private fun setErrorState(message: String, errorOrigin: String) {
        Log.d("ErrorFound", "Error occurs in throttleNetworkCall  origin$errorOrigin")
        _state.update {
            it.copy(
                errorState = FoodSearchStates.FoodSearchErrorState(
                    true,
                    message,
                ),
            )
        }
    }

    private fun setStateToLoading() {
        _state.update {
            it.copy(loading = true)
        }
    }

    private suspend fun makeNetworkCall(foodName: String) {

        setStateToLoading()

        val result = when (val response = foodRepository.makeNetworkRequest(foodName)) {
            is AsyncResponse.Failed -> {
                Log.d("ErrorFound", "Failed inside AsynResponse.Failed ${response.message}")
                FoodSearchStates.LoggingFoodItem(
                    errorState = FoodSearchStates.FoodSearchErrorState(
                        true,
                        response.message ?: "failed network request"
                    )
                )
            }

            is AsyncResponse.Success -> {
                FoodSearchStates.LoggingFoodItem(
                    searchedText = foodName,
                    foodItemsFound = mutableStateOf(
                        response.data?.hints?.toUniqueFoodItems() ?: emptyList()
                    ),
                    userInput = foodName
                )

            }
        }
        _state.update { result }
    }
}
