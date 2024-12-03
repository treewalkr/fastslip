package com.xdev.fastslip.domain.usecase

import com.xdev.fastslip.domain.model.BankAccount
import com.xdev.fastslip.domain.repository.BankAccountRepository

class UpdateBankAccountUseCase(private val repository: BankAccountRepository) {
    suspend operator fun invoke(account: BankAccount) = repository.updateBankAccount(account)
}
