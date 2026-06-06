package com.jiyeong.supplementroutine.kmp.android.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jiyeong.supplementroutine.shared.data.IntakeRecordRepository
import com.jiyeong.supplementroutine.shared.data.SettingsRepository
import com.jiyeong.supplementroutine.shared.data.SupplementRepository
import com.jiyeong.supplementroutine.shared.domain.InstantValue
import com.jiyeong.supplementroutine.shared.domain.IntakeRecord
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.domain.Supplement
import com.jiyeong.supplementroutine.shared.domain.TimeOfDayValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SupplementRoutineViewModel @Inject constructor(
    private val supplementRepository: SupplementRepository,
    private val intakeRecordRepository: IntakeRecordRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SupplementRoutineUiState(today = currentLocalDateValue()))
    val uiState: StateFlow<SupplementRoutineUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { state -> state.copy(isLoading = true, errorMessage = null) }
            runRepositoryAction {
                loadState()
            }
        }
    }

    fun toggleRecord(record: IntakeRecord) {
        viewModelScope.launch {
            runRepositoryAction {
                val nextRecord = if (record.isDone) {
                    record.markUndone()
                } else {
                    record.markDone(InstantValue(System.currentTimeMillis()))
                }
                intakeRecordRepository.upsertRecord(nextRecord)
                loadState()
            }
        }
    }

    fun addSupplement(
        supplement: Supplement,
        onSuccess: () -> Unit = {},
    ) {
        viewModelScope.launch {
            runRepositoryAction(
                onSuccess = onSuccess,
            ) {
                supplementRepository.addSupplement(supplement)
                loadState()
            }
        }
    }

    fun updateSupplement(supplement: Supplement) {
        viewModelScope.launch {
            runRepositoryAction {
                supplementRepository.updateSupplement(supplement)
                loadState()
            }
        }
    }

    fun removeSupplement(supplement: Supplement) {
        viewModelScope.launch {
            runRepositoryAction {
                supplementRepository.removeSupplement(supplement.id)
                intakeRecordRepository.clearRecordsForSupplement(supplement.id)
                loadState()
            }
        }
    }

    fun toggleSupplementNotification(supplement: Supplement) {
        updateSupplement(
            supplement.copy(isNotificationEnabled = !supplement.isNotificationEnabled),
        )
    }

    fun updateBreakfastTime(time: TimeOfDayValue) {
        viewModelScope.launch {
            runRepositoryAction {
                settingsRepository.updateBreakfastTime(time)
                loadState()
            }
        }
    }

    fun updateLunchTime(time: TimeOfDayValue) {
        viewModelScope.launch {
            runRepositoryAction {
                settingsRepository.updateLunchTime(time)
                loadState()
            }
        }
    }

    fun updateDinnerTime(time: TimeOfDayValue) {
        viewModelScope.launch {
            runRepositoryAction {
                settingsRepository.updateDinnerTime(time)
                loadState()
            }
        }
    }

    fun updateNotificationEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            runRepositoryAction {
                settingsRepository.updateNotificationEnabled(isEnabled)
                loadState()
            }
        }
    }

    fun resetRoutineData() {
        viewModelScope.launch {
            runRepositoryAction {
                supplementRepository.clearSupplements()
                intakeRecordRepository.clearAll()
                loadState()
            }
        }
    }

    private suspend fun runRepositoryAction(
        onSuccess: () -> Unit = {},
        action: suspend () -> SupplementRoutineUiState,
    ) {
        val result = runCatching {
            withContext(Dispatchers.IO) { action() }
        }

        result.fold(
            onSuccess = { nextState ->
                _uiState.value = nextState.copy(isLoading = false, errorMessage = null)
                onSuccess()
            },
            onFailure = { throwable ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "저장된 데이터를 불러오지 못했습니다.",
                    )
                }
            },
        )
    }

    private fun loadState(): SupplementRoutineUiState {
        return SupplementRoutineUiState(
            isLoading = false,
            today = currentLocalDateValue(),
            supplements = supplementRepository.getSupplements(),
            intakeRecords = intakeRecordRepository.getRecords(),
            mealTimeSettings = settingsRepository.getMealTimeSettings(),
            notificationEnabled = settingsRepository.getNotificationEnabled(),
        )
    }
}

fun currentLocalDateValue(): LocalDateValue {
    val calendar = Calendar.getInstance()
    return LocalDateValue(
        year = calendar.get(Calendar.YEAR),
        month = calendar.get(Calendar.MONTH) + 1,
        day = calendar.get(Calendar.DAY_OF_MONTH),
    )
}
