package com.xdev.fastslip.domain.usecase

import com.xdev.fastslip.domain.model.BankAccount
import com.xdev.fastslip.domain.repository.BankAccountRepository
import kotlinx.coroutines.flow.Flow

class GetBankAccountsUseCase(private val repository: BankAccountRepository) {
    suspend operator fun invoke(): Flow<List<BankAccount>> = repository.getBankAccounts()
}
