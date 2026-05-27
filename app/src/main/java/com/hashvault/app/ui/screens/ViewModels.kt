package com.hashvault.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hashvault.app.HashVaultApp
import com.hashvault.app.data.local.PreferencesManager
import com.hashvault.app.data.model.*
import com.hashvault.app.data.repository.PoolRepository
import com.hashvault.app.data.repository.WalletRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ====================================================================
// HOME VIEWMODEL
// ====================================================================

class HomeViewModel(
    private val poolRepo: PoolRepository = PoolRepository(),
    private val walletRepo: WalletRepository
) : ViewModel() {

    data class UiState(
        val walletAddress: String? = null,
        val poolStats: PoolStatsResponse? = null,
        val walletStats: WalletStatsResponse? = null,
        val latestBlocks: List<Block> = emptyList(),
        val latestPayments: List<Payment> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val address = walletRepo.getSavedAddress()

            poolRepo.getPoolStats().onSuccess { poolStats ->
                _uiState.update { it.copy(poolStats = poolStats) }
            }.onFailure { e ->
                _uiState.update { it.copy(error = "Pool stats: ${e.message}") }
            }

            if (address != null) {
                walletRepo.getWalletStats(address).onSuccess { walletStats ->
                    _uiState.update { it.copy(walletStats = walletStats) }
                }

                walletRepo.getWalletBlocks(address, page = 0, limit = 5).onSuccess { blocks ->
                    _uiState.update { it.copy(latestBlocks = blocks) }
                }

                walletRepo.getWalletPayments(address, page = 0).onSuccess { payments ->
                    _uiState.update { it.copy(latestPayments = payments) }
                }
            }

            _uiState.update { it.copy(isLoading = false, walletAddress = address) }
        }
    }

    fun refresh() = loadAll()
}

class HomeViewModelFactory(private val app: HashVaultApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(walletRepo = WalletRepository(app.prefs)) as T
    }
}

// ====================================================================
// WALLET VIEWMODEL
// ====================================================================

class WalletViewModel(
    private val walletRepo: WalletRepository
) : ViewModel() {

    data class WalletUiState(
        val address: String = "",
        val stats: WalletStatsResponse? = null,
        val blocks: List<Block> = emptyList(),
        val payments: List<Payment> = emptyList(),
        val rewards: List<Reward> = emptyList(),
        val shares: List<ShareEntry> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(WalletUiState())
    val state: StateFlow<WalletUiState> = _state.asStateFlow()

    init {
        loadSavedAddress()
    }

    private fun loadSavedAddress() {
        viewModelScope.launch {
            val addr = walletRepo.getSavedAddress()
            if (addr != null) {
                _state.update { it.copy(address = addr) }
                loadWalletData(addr)
            }
        }
    }

    fun saveAddress(address: String) {
        viewModelScope.launch {
            walletRepo.saveAddress(address)
            _state.update { it.copy(address = address) }
            loadWalletData(address)
        }
    }

    fun loadWalletData(address: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // Load all wallet data in parallel where possible
            walletRepo.getWalletStats(address).onSuccess { stats ->
                _state.update { it.copy(stats = stats) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }

            walletRepo.getWalletBlocks(address).onSuccess { blocks ->
                _state.update { it.copy(blocks = blocks) }
            }

            walletRepo.getWalletPayments(address).onSuccess { payments ->
                _state.update { it.copy(payments = payments) }
            }

            walletRepo.getWalletRewards(address).onSuccess { rewards ->
                _state.update { it.copy(rewards = rewards) }
            }

            // Shares loaded separately
            walletRepo.getWalletTopShares(address).onSuccess { resp ->
                val allShares = (resp.collectiveRound.orEmpty() + resp.soloRound.orEmpty() + resp.overall.orEmpty())
                    .distinctBy { it.index }
                _state.update { it.copy(shares = allShares) }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    /** Refresh all wallet data for the current address */
    fun refresh() {
        val addr = _state.value.address
        if (addr.isNotBlank()) loadWalletData(addr)
    }
}

class WalletViewModelFactory(private val app: HashVaultApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WalletViewModel(WalletRepository(app.prefs)) as T
    }
}
