import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.makhazany.Data.Local.Entity.OutboundDetailsEntity
import com.example.makhazany.Domain.Repository.OutboundRepository
import com.example.smartstock.Data.Local.Entity.OutboundEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class OutboundViewModel @Inject constructor(
    private val repository: OutboundRepository
) : ViewModel() {

    // Live Flow للفواتير مع التفاصيل
    val outboundList = repository
        .getOutboundWithDetails()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

    // دالة لإضافة فاتورة + التفاصيل
    fun insertOutboundWithDetails(
        outbound: OutboundEntity,
        details: List<OutboundDetailsEntity>
    ) {
        viewModelScope.launch {
            // إضافة الفاتورة الأساسية
            val outboundId = repository.insertOutbound(outbound)

            // ربط التفاصيل بالـ ID الجديد للفاتورة
            val detailsWithId = details.map {
                it.copy(outboundId = outboundId.toInt())
            }

            // إضافة التفاصيل
            repository.insertDetails(detailsWithId)
        }
    }
}