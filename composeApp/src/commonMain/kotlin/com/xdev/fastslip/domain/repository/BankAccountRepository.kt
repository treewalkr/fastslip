package com.xdev.fastslip.domain.repository

import com.xdev.fastslip.domain.model.BankAccount
import kotlinx.coroutines.flow.Flow

interface BankAccountRepository {
    suspend fun getBankAccounts(): Flow<List<BankAccount>>
    suspend fun updateBankAccount(account: BankAccount)
}
