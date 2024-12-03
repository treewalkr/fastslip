package com.xdev.fastslip.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.xdev.fastslip.domain.model.BankAccount
import com.xdev.fastslip.screens.list.ListViewModel
import fast_slip.composeapp.generated.resources.Res
import fast_slip.composeapp.generated.resources.add
import fast_slip.composeapp.generated.resources.credit_card
import fast_slip.composeapp.generated.resources.home
import fast_slip.composeapp.generated.resources.qr_code_scanner
import fast_slip.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToScanQr: () -> Unit,
    navigateToHome: () -> Unit
) {
    val homeViewModel = koinViewModel<HomeViewModel>()

    var isAddingAccount by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(0) }

    val bankAccounts by homeViewModel.bankAccounts.collectAsState()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Slip Check", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* Handle notification */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    // Add Avatar here. For simplicity, we'll use an Icon
                    IconButton(onClick = { /* Handle profile */ }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf("Home", "Scan", "Accounts", "Settings")
                val icons = listOf(
                    painterResource(Res.drawable.home),
                    painterResource(Res.drawable.qr_code_scanner),
                    painterResource(Res.drawable.credit_card),
                    painterResource(Res.drawable.settings),
                )
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                icons[index],
                                contentDescription = item
                            )
                        },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            if (item == "Home") {
                                navigateToHome()
                            } else if (item == "Scan") {
                                navigateToScanQr()
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Quick Actions", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuickActionButton(
                        icon = painterResource(Res.drawable.qr_code_scanner),
                        text = "Scan Bank Slip",
                        onClick = { navigateToScanQr() },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        icon = painterResource(Res.drawable.add),
                        text = "Add Bank Account",
                        onClick = { isAddingAccount = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text("Your Bank Accounts", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                if (bankAccounts.isNotEmpty()) {
                    bankAccounts.forEach { account ->
                        BankAccountCard(
                            title = account.title,
                            bank = account.bankName,
                            accountNumber = account.accountNumber
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    //Text("No bank accounts found", style = MaterialTheme.typography.bodyMedium)
                    BankAccountCard(
                        title = "Main Business Account",
                        bank = "Bank of Example",
                        accountNumber = "**** **** **** ABCD"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BankAccountCard(
                        title = "Savings Account",
                        bank = "Second National Bank",
                        accountNumber = "**** **** **** WXYZ"
                    )
                }
            }

            item {
                Text("Recent Activity", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Card {
                    Column {
                        ActivityItem(
                            icon = painterResource(Res.drawable.qr_code_scanner),
                            title = "Slip Verified",
                            time = "Today, 2:30 PM"
                        )
                        HorizontalDivider()
                        ActivityItem(
                            icon = painterResource(Res.drawable.credit_card),
                            title = "New Account Added",
                            time = "Yesterday, 11:15 AM"
                        )
                        HorizontalDivider()
                        ActivityItem(
                            icon = painterResource(Res.drawable.credit_card),
                            title = "New Account Added",
                            time = "Yesterday, 11:15 AM"
                        )
                    }
                }
            }
        }
    }

    if (isAddingAccount) {
        AddAccountDialog(
            onDismiss = { isAddingAccount = false },
            onAddAccount = { title, bankName, accountNumber, removeAccountNumber ->
                homeViewModel.updateBankAccount(
                    BankAccount(
                        title = title,
                        bankName = bankName,
                        accountNumber = accountNumber,
                        isEnabled = !removeAccountNumber
                    )
                )
            }
        )
    }
}

@Composable
fun QuickActionButton(
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = text)
            Spacer(Modifier.height(4.dp))
            Text(text)
        }
    }
}

@Composable
fun BankAccountCard(
    title: String,
    bank: String,
    accountNumber: String,
    //onEdit: () -> Unit = {},
    //onDelete: () -> Unit = {}
) {
    val clipboardManager = LocalClipboardManager.current
    //var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Copy the full account number to clipboard
                clipboardManager.setText(AnnotatedString(accountNumber))
            }
            .padding(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(bank, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    hiddenAccountNumber(accountNumber),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

private fun getLastNChars(str: String, n: Int): String {
    return str.takeLast(n)
}

private fun hiddenAccountNumber(accountNumber: String): String {
    val lastFour = getLastNChars(accountNumber, 4)
    return "**** **** **** $lastFour"
}

@Composable
fun ActivityItem(icon: Painter, title: String, time: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(time, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun AddAccountDialog(
    onDismiss: () -> Unit,
    onAddAccount: (
        title: String,
        bankName: String,
        accountNumber: String,
        removeAccountNumber: Boolean
    ) -> Unit
) {
    val viewModel = koinViewModel<HomeViewModel>()

    // State handling
    val dialogState = remember {
        mutableStateOf(
            DialogState(
                title = "",
                bankName = "",
                accountNumber = "",
                removeAccountNumber = false
            )
        )
    }

    // Derived states
    val isAccountEnabled by remember(dialogState.value.accountNumber) {
        derivedStateOf { viewModel.isAccountEnabled(dialogState.value.accountNumber) }
    }

    val queriedAccount by remember(dialogState.value.accountNumber) {
        derivedStateOf { viewModel.getBankAccount(dialogState.value.accountNumber) }
    }

    // Update fields when queried account changes
    LaunchedEffect(queriedAccount) {
        queriedAccount?.let { account ->
            dialogState.value = dialogState.value.copy(
                title = account.title,
                bankName = account.bankName,
                accountNumber = account.accountNumber
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Bank Account") },
        text = {
            DialogContent(
                state = dialogState.value,
                onStateChange = { dialogState.value = it },
                isAccountEnabled = isAccountEnabled
            )
        },
        confirmButton = {
            ConfirmButton(
                state = dialogState.value,
                isAccountEnabled = isAccountEnabled,
                onConfirm = {
                    with(dialogState.value) {
                        onAddAccount(title, bankName, accountNumber, removeAccountNumber)
                    }
                    onDismiss()
                }
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// State data class
private data class DialogState(
    val title: String = "",
    val bankName: String = "",
    val accountNumber: String = "",
    val removeAccountNumber: Boolean = false
)

@Composable
private fun DialogContent(
    state: DialogState,
    onStateChange: (DialogState) -> Unit,
    isAccountEnabled: Boolean
) {
    Column {
        OutlinedTextField(
            value = state.accountNumber,
            onValueChange = { newAccountNumber ->
                onStateChange(state.copy(
                    accountNumber = newAccountNumber.filter { it.isDigit() || it == '-' }
                ))
            },
            label = { Text("Account Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.title,
            onValueChange = { newTitle ->
                onStateChange(state.copy(title = newTitle.filter { it != '\n' }))
            },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.bankName,
            onValueChange = { newBankName ->
                onStateChange(state.copy(bankName = newBankName.filter { it != '\n' }))
            },
            label = { Text("Bank Name") },
            modifier = Modifier.fillMaxWidth()
        )

        if (state.accountNumber.isNotBlank() && isAccountEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onStateChange(state.copy(removeAccountNumber = !state.removeAccountNumber))
                }
            ) {
                Checkbox(
                    checked = state.removeAccountNumber,
                    onCheckedChange = { isChecked ->
                        // Directly toggle here as well to avoid double updates
                        onStateChange(state.copy(removeAccountNumber = isChecked))
                    }
                )
                Text("Delete this account number!")
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

    }
}

@Composable
private fun ConfirmButton(
    state: DialogState,
    isAccountEnabled: Boolean,
    onConfirm: () -> Unit
) {
    Button(onClick = onConfirm) {
        Text(
            when {
                isAccountEnabled && state.removeAccountNumber -> "Remove Account"
                isAccountEnabled && !state.removeAccountNumber -> "Update Account"
                else -> "Add Account"
            }
        )
    }
}