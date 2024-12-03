package com.xdev.fastslip.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.xdev.fastslip.data.proto.BankAccountProto.BankAccount
import com.xdev.fastslip.data.proto.BankAccountListSerializer
import com.xdev.fastslip.data.proto.BankAccountProto.BankAccountList
import com.xdev.fastslip.domain.model.BankAccount as DomainBankAccount
import com.xdev.fastslip.domain.repository.BankAccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val DATASTORE_FILE_NAME = "bank_accounts.pb"
private const val TAG = "BankAccountRepository"

private val Context.bankAccountDataStore: DataStore<BankAccountList> by dataStore(
    fileName = DATASTORE_FILE_NAME,
    serializer = BankAccountListSerializer
)

class BankAccountRepositoryImpl(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BankAccountRepository {

    override suspend fun getBankAccounts(): Flow<List<DomainBankAccount>> =
        context.bankAccountDataStore.data
            .map { it.accountsList.map(BankAccount::toDomain) }
            .catch { exception ->
                Log.e(TAG, "Error fetching bank accounts", exception)
                emit(emptyList())
            }

    override suspend fun updateBankAccount(account: DomainBankAccount) {
        try {
            Log.d(TAG, "Updating bank account: $account")
            context.bankAccountDataStore.updateData { currentList ->
                updateBankAccountList(currentList, account)
            }
            logUpdatedAccounts()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating bank account: $account", e)
            throw e
        }
    }

    private fun updateBankAccountList(
        currentList: BankAccountList,
        account: DomainBankAccount
    ): BankAccountList {
        val updatedAccounts = currentList.accountsList
            .mapNotNull { it.updateOrRemove(account) }
            .let { accounts ->
                if (shouldAddNewAccount(accounts, account)) {
                    accounts + account.toProto()
                } else accounts
            }

        return currentList.toBuilder()
            .clearAccounts()
            .addAllAccounts(updatedAccounts)
            .build()
    }

    private fun shouldAddNewAccount(
        accounts: List<BankAccount>,
        account: DomainBankAccount
    ): Boolean =
        account.isEnabled && accounts.none { it.accountNumber == account.accountNumber }

    private fun BankAccount.updateOrRemove(account: DomainBankAccount): BankAccount? = when {
        accountNumber == account.accountNumber && account.isEnabled -> {
            Log.d(TAG, "Updating existing account: $accountNumber with new data: $account")
            toBuilder()
                .setTitle(account.title)
                .setBankName(account.bankName)
                .setAccountNumber(account.accountNumber)
                .setIsEnabled(true)
                .build()
        }

        accountNumber == account.accountNumber && !account.isEnabled -> {
            Log.d(TAG, "Removing account: $accountNumber")
            null
        }

        else -> this
    }

    private fun logUpdatedAccounts() {
        CoroutineScope(ioDispatcher).launch {
            context.bankAccountDataStore.data
                .map { it.accountsList.map(BankAccount::toDomain) }
                .catch { exception ->
                    Log.e(TAG, "Error logging updated accounts", exception)
                }
                .collect { accounts ->
                    accounts.forEach { Log.d(TAG, "Updated list: $it") }
                }
        }
    }
}

// Model mapping extensions
private fun BankAccount.toDomain() = DomainBankAccount(
    title = title,
    bankName = bankName,
    accountNumber = accountNumber,
    isEnabled = isEnabled
)

private fun DomainBankAccount.toProto() = BankAccount.newBuilder()
    .setTitle(title)
    .setBankName(bankName)
    .setAccountNumber(accountNumber)
    .setIsEnabled(isEnabled)
    .build()