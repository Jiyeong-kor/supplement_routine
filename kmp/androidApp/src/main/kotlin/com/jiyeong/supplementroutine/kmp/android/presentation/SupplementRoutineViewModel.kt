package com.jiyeong.supplementroutine.kmp.android.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jiyeong.supplementroutine.kmp.android.data.AndroidIntakeRecordRepository
import com.jiyeong.supplementroutine.kmp.android.data.AndroidRoutineDataStore
import com.jiyeong.supplementroutine.kmp.android.data.AndroidSettingsRepository
import com.jiyeong.supplementroutine.kmp.android.data.AndroidSupplementRepository
import com.jiyeong.supplementroutine.shared.data.IntakeRecordRepository
import com.jiyeong.supplementroutine.shared.data.SettingsRepository
import com.jiyeong.supplementroutine.shared.data.SupplementRepository
import com.jiyeong.supplementroutine.shared.domain.InstantValue
import com.jiyeong.supplementroutine.shared.domain.IntakeRecord
import com.jiyeong.supplementroutine.shared.domain.LocalDateValue
import com.jiyeong.supplementroutine.shared.domain.Supplement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class SupplementRoutineViewModel(
    private val supplementRepository: SupplementRepository,
    private val intakeRecordRepository: IntakeRecordRepository,
    private val settingsRepository: SettingsRepository,
    private val clock: () -> Long = { System.currentTimeMillis() },
    private val todayProvider: () -> LocalDateValue = ::currentLocalDateValue,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SupplementRoutineUiState(today = todayProvider()))
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
                    record.markDone(InstantValue(clock()))
                }
                intakeRecordRepository.upsertRecord(nextRecord)
                loadState()
            }
        }
    }

    fun addSupplement(supplement: Supplement) {
        viewModelScope.launch {
            runRepositoryAction {
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

    private suspend fun runRepositoryAction(action: suspend () -> SupplementRoutineUiState) {
        val result = runCatching {
            withContext(Dispatchers.IO) { action() }
        }

        result.fold(
            onSuccess = { nextState ->
                _uiState.value = nextState.copy(isLoading = false, errorMessage = null)
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
            today = todayProvider(),
            supplements = supplementRepository.getSupplements(),
            intakeRecords = intakeRecordRepository.getRecords(),
            mealTimeSettings = settingsRepository.getMealTimeSettings(),
            notificationEnabled = settingsRepository.getNotificationEnabled(),
        )
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val applicationContext = context.applicationContext
            return viewModelFactory {
                initializer {
                    val store = AndroidRoutineDataStore(applicationContext)
                    SupplementRoutineViewModel(
                        supplementRepository = AndroidSupplementRepository(store),
                        intakeRecordRepository = AndroidIntakeRecordRepository(store),
                        settingsRepository = AndroidSettingsRepository(store),
                    )
                }
            }
        }
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
