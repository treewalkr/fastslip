package com.xdev.fastslip.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xdev.fastslip.domain.model.BankAccount as DomainBankAccount
import com.xdev.fastslip.domain.usecase.GetBankAccountsUseCase
import com.xdev.fastslip.domain.usecase.UpdateBankAccountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel(
    private val getBankAccountsUseCase: GetBankAccountsUseCase,
    private val updateBankAccountUseCase: UpdateBankAccountUseCase
) : ViewModel(), KoinComponent {

    private val _bankAccounts = MutableStateFlow<List<DomainBankAccount>>(emptyList())
    val bankAccounts: StateFlow<List<DomainBankAccount>> get() = _bankAccounts

    private val _enabledAccounts = MutableStateFlow(setOf<String>())
    val enabledAccounts: StateFlow<Set<String>> get() = _enabledAccounts.asStateFlow()

    init {
        fetchBankAccounts()
    }

    private fun fetchBankAccounts() {
        viewModelScope.launch {
            getBankAccountsUseCase().collect { accounts ->
                _bankAccounts.value = accounts
                // Update enabledAccounts with the current enabled accounts
                _enabledAccounts.value =
                    accounts.filter { it.isEnabled }.map { it.accountNumber }.toSet()
            }
        }
    }

    // Update entire BankAccount object in the repository and update the enabledAccounts set
    fun updateBankAccount(account: DomainBankAccount) {
        viewModelScope.launch {
            updateBankAccountUseCase(account)
            // Update enabledAccounts based on the account's isEnabled status
            _enabledAccounts.value = if (account.isEnabled) {
                _enabledAccounts.value + cleanupAccountNumber(account.accountNumber)
            } else {
                _enabledAccounts.value - cleanupAccountNumber(account.accountNumber)
            }
        }
    }

    // Check if an account is enabled
    fun isAccountEnabled(accountNumber: String): Boolean {
        return enabledAccounts.value.contains(cleanupAccountNumber(accountNumber))
    }

    // Get the BankAccount object by account number
    fun getBankAccount(accountNumber: String): DomainBankAccount? {
        return bankAccounts.value.find { it.accountNumber == cleanupAccountNumber(accountNumber) }
    }

    // Toggle the enable/disable status of an account by account number
    fun toggleAccountStatus(accountNumber: String, isEnabled: Boolean) {
        _enabledAccounts.value = if (isEnabled) {
            _enabledAccounts.value + cleanupAccountNumber(accountNumber)
        } else {
            _enabledAccounts.value - cleanupAccountNumber(accountNumber)
        }

        // Update the specific account in the repository
        val account = _bankAccounts.value.find { it.accountNumber == accountNumber }
        if (account != null) {
            updateBankAccount(account.copy(isEnabled = isEnabled))
        }
    }

    private fun cleanupAccountNumber(accountNumber: String): String {
        return accountNumber.replace(Regex("[^\\d]"), "")
    }
}
