package com.example.project_prm392.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_prm392.DAO.AppRepository
import com.example.project_prm392.model.StoreLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel(private val repository: AppRepository) : ViewModel() {
    private val _locations = MutableStateFlow<List<StoreLocation>>(emptyList())
    val locations: StateFlow<List<StoreLocation>> = _locations.asStateFlow()

    init {
        loadStoreLocations()
    }

    private fun loadStoreLocations() {
        viewModelScope.launch {
            _locations.value = repository.getAllStoreLocations()
        }
    }
}
