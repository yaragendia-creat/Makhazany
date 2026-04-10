package com.example.makhazany.Presentation.Ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.makhazany.Data.Local.Relation.ItemWithStock
import com.example.makhazany.Data.Local.Entity.ItemEntity
import com.example.makhazany.Data.Local.Entity.StockEntity
import com.example.makhazany.Domain.Repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class StockFilter { ALL, SHORTAGE, RECENT }
enum class SortOrder { NONE, NAME_ASC, NAME_DESC, QTY_ASC, QTY_DESC }

@HiltViewModel
class StockViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentFilter = MutableStateFlow(StockFilter.ALL)
    val currentFilter: StateFlow<StockFilter> = _currentFilter.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _allStockItems = MutableStateFlow<List<ItemWithStock>>(emptyList())
    
    val stockItems: StateFlow<List<ItemWithStock>> = combine(_allStockItems, _searchQuery, _currentFilter, _sortOrder) { items, query, filter, sort ->
        var filteredList = items
        
        // Apply category/status filters
        filteredList = when(filter) {
            StockFilter.SHORTAGE -> filteredList.filter { (it.stock?.quantity ?: 0.0) < 10 }
            else -> filteredList
        }

        // Apply search query
        filteredList = if (query.isBlank()) {
            filteredList
        } else {
            filteredList.filter { itemWithStock ->
                itemWithStock.item.name.contains(query, ignoreCase = true) || 
                (itemWithStock.item.sku?.contains(query, ignoreCase = true) ?: false) ||
                (itemWithStock.item.category?.contains(query, ignoreCase = true) ?: false) ||
                itemWithStock.inboundHistory.any { it.invoiceNumber.contains(query, ignoreCase = true) }
            }
        }

        // Apply Sorting
        when (sort) {
            SortOrder.NAME_ASC -> filteredList.sortedBy { it.item.name }
            SortOrder.NAME_DESC -> filteredList.sortedByDescending { it.item.name }
            SortOrder.QTY_ASC -> filteredList.sortedBy { it.stock?.quantity ?: 0.0 }
            SortOrder.QTY_DESC -> filteredList.sortedByDescending { it.stock?.quantity ?: 0.0 }
            SortOrder.NONE -> filteredList
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems.asStateFlow()

    private val _shortages = MutableStateFlow(0)
    val shortages: StateFlow<Int> = _shortages.asStateFlow()

    init {
        getStockData()
    }

    private fun getStockData() {
        viewModelScope.launch {
            repository.getItemsWithStock().collectLatest { list ->
                _allStockItems.value = list
                _totalItems.value = list.size
                _shortages.value = list.count { (it.stock?.quantity ?: 0.0) < 10 }
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun setFilter(filter: StockFilter) {
        _currentFilter.value = if (_currentFilter.value == filter) StockFilter.ALL else filter
    }

    fun toggleSortByName() {
        _sortOrder.value = when (_sortOrder.value) {
            SortOrder.NAME_ASC -> SortOrder.NAME_DESC
            SortOrder.NAME_DESC -> SortOrder.NONE
            else -> SortOrder.NAME_ASC
        }
    }

    fun toggleSortByQty() {
        _sortOrder.value = when (_sortOrder.value) {
            SortOrder.QTY_ASC -> SortOrder.QTY_DESC
            SortOrder.QTY_DESC -> SortOrder.NONE
            else -> SortOrder.QTY_ASC
        }
    }

    fun addItem(item: ItemEntity, quantity: Double) {
        viewModelScope.launch {
            val id = repository.insertItem(item)
            repository.insertStock(StockEntity(itemId = id.toInt(), quantity = quantity))
        }
    }
}
