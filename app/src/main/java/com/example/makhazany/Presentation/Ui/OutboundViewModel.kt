package com.example.makhazany.Presentation.Ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.makhazany.Data.Local.Entity.OutboundDetailsEntity
import com.example.makhazany.Data.Local.Entity.OutboundEntity
import com.example.makhazany.Domain.Repository.OutboundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OutboundViewModel @Inject constructor(
    private val repository: OutboundRepository
) : ViewModel() {

    val outboundList = repository
        .getOutboundWithDetails()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

    fun insertOutboundWithDetails(
        outbound: OutboundEntity,
        details: List<OutboundDetailsEntity>
    ) {
        viewModelScope.launch {
            val outboundId = repository.insertOutbound(outbound)
            val detailsWithId = details.map {
                it.copy(outboundId = outboundId.toInt())
            }
            repository.insertDetails(detailsWithId)
        }
    }
}
