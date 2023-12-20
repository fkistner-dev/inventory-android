package com.kilomobi.cigobox

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InventoryViewModel(private val stateHandle: SavedStateHandle) : ViewModel() {
    private var restInterface: InventoryApiService
    private lateinit var inventoryCall: Call<List<Appetizer>>
    val state = mutableStateOf(emptyList<Appetizer>())
    val allowEdit = mutableStateOf(false)

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("redacted")
            .build()
        restInterface = retrofit.create(InventoryApiService::class.java)
        getInventory()
    }

    private fun getInventory() {
        inventoryCall = restInterface.getInventory()
        inventoryCall.enqueue(
            object : Callback<List<Appetizer>> {
                override fun onResponse(
                    call: Call<List<Appetizer>>,
                    response: Response<List<Appetizer>>
                ) {
                    response.body()?.let { appetizers ->
                        val appetizerList = appetizers.map { it.copy(isVisible = true) }
                        state.value = appetizerList.restoreSelections()
                    }
                }

                override fun onFailure(call: Call<List<Appetizer>>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    private fun updateQuantity(id: Int, quantityChange: Int) {
        val appetizers = state.value.toMutableList()
        val itemIndex = appetizers.indexOfFirst { it.id == id }

        if (itemIndex != -1) {
            val item = appetizers[itemIndex]
            appetizers[itemIndex] =
                item.copy(quantity = (item.quantity + quantityChange).coerceAtLeast(0))
            state.value = appetizers
        }
    }

    fun increaseQuantity(id: Int) {
        updateQuantity(id, 1)
    }

    fun decreaseQuantity(id: Int) {
        updateQuantity(id, -1)
    }

    fun toggleEdit() {
        allowEdit.value = !allowEdit.value
        toggleEditItems()
    }

    private fun toggleEditItems() {
        val currentList = state.value
        state.value = currentList.map { it.copy(isEditable = !it.isEditable) }
    }

    fun filterAction(category: Category) {
        val currentList = state.value
        val filteredList =
            currentList.filter { it.category.toCategory().name == category.name || it.category == Category.TOUT.name }
        state.value = currentList.map { item ->
            item.copy(isVisible = item in filteredList)
        }
    }

    fun validateStock() {
        toggleEdit()
    }

    private fun storeSelection(item: Appetizer) {
        val savedItems = stateHandle.get<List<Appetizer>?>(APPETIZERS)
            .orEmpty().toMutableList()
        savedItems.clear()
        savedItems.add(item)
        stateHandle[APPETIZERS] = savedItems
    }

    private fun List<Appetizer>.restoreSelections(): List<Appetizer> {
        stateHandle.get<List<Appetizer>?>(APPETIZERS)?.let {
            val restaurantsMap = this.associateBy { it.id }
            return restaurantsMap.values.toList()
        }
        return this
    }

    override fun onCleared() {
        super.onCleared()
        inventoryCall.cancel()
    }

    companion object {
        const val APPETIZERS = "appetizers"
    }
}