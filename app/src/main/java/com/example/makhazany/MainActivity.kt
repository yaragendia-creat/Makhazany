package com.example.makhazany

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.makhazany.Data.Local.Entity.CustomerEntity
import com.example.makhazany.Data.Local.Entity.InboundEntity
import com.example.makhazany.Data.Local.Entity.ItemEntity
import com.example.makhazany.Presentation.Ui.*
import com.example.makhazany.ui.theme.MakhazanyTheme
import com.example.makhazany.ui.theme.PrimaryPurple
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val customerViewModel: CustomerViewModel by viewModels()
    private val stockViewModel: StockViewModel by viewModels()
    private val inboundViewModel: InboundViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        lifecycleScope.launch {
            val currentCustomers = customerViewModel.customers.first()
            if (currentCustomers.isEmpty()) {

                customerViewModel.addCustomer(
                    CustomerEntity(
                        customerName = "محمد سعودي",
                        address = "الجيزة - الكنيسة",
                        customerType = "عميل جملة",
                        customerDebt = 6850.0,
                        userId = "1",
                        customerNum = 1
                    )
                )
                customerViewModel.addCustomer(
                    CustomerEntity(
                        customerName = "أحمد الشناوي",
                        address = "القاهرة",
                        customerType = "عميل قطاعي",
                        customerDebt = 12400.0,
                        userId = "1",
                        customerNum = 2
                    )
                )
                customerViewModel.addCustomer(
                    CustomerEntity(
                        customerName = "شركة الفهد للتوريدات",
                        address = "الإسكندرية",
                        customerType = "مورد تجاري",
                        customerDebt = 420.75,
                        userId = "1",
                        customerNum = 3
                    )
                )
                customerViewModel.addCustomer(
                    CustomerEntity(
                        customerName = "محمود عبد اللطيف",
                        address = "أسيوط",
                        customerType = "عميل VIP",
                        customerDebt = 95000.0,
                        userId = "1",
                        customerNum = 4
                    )
                )


                stockViewModel.addItem(
                    ItemEntity(
                        name = "لمبه ديسكو",
                        description = "إضاءة",
                        price = 5.0,
                        category = "إضاءة",
                        sku = "DISCO-001"
                    ), 1000.0
                )
                stockViewModel.addItem(
                    ItemEntity(
                        name = "كابل توصيل 5 متر",
                        description = "كهرباء",
                        price = 10.0,
                        category = "كهرباء",
                        sku = "CBL-5M"
                    ), 450.0
                )
                stockViewModel.addItem(
                    ItemEntity(
                        name = "بطارية شحن جاف",
                        description = "طاقة",
                        price = 100.0,
                        category = "طاقة",
                        sku = "BAT-DRY-12"
                    ), 12.0
                )


                val calendar = Calendar.getInstance()

                calendar.set(2023, Calendar.OCTOBER, 24)
                inboundViewModel.addInbound(
                    InboundEntity(
                        itemId = 1,
                        amount = 1000,
                        pricePerUnit = 5.0,
                        invoiceNumber = "TRX-9921",
                        inboundDate = calendar.timeInMillis
                    )
                )

                calendar.set(2023, Calendar.SEPTEMBER, 12)
                inboundViewModel.addInbound(
                    InboundEntity(
                        itemId = 1,
                        amount = 2500,
                        pricePerUnit = 4.8,
                        invoiceNumber = "TRX-8742",
                        inboundDate = calendar.timeInMillis
                    )
                )

                calendar.set(2023, Calendar.AUGUST, 5)
                inboundViewModel.addInbound(
                    InboundEntity(
                        itemId = 1,
                        amount = 1500,
                        pricePerUnit = 5.5,
                        invoiceNumber = "TRX-7104",
                        inboundDate = calendar.timeInMillis
                    )
                )
            }
        }

        enableEdgeToEdge()
        setContent {
            MakhazanyTheme {
                var selectedTab by remember { mutableIntStateOf(0) }
                var showHistorySheet by remember { mutableStateOf(false) }
                var selectedItemName by remember { mutableStateOf("") }

                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                label = { Text("المديونيات") },
                                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryPurple,
                                    selectedTextColor = PrimaryPurple
                                )
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                label = { Text("المخزن") },
                                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryPurple,
                                    selectedTextColor = PrimaryPurple
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        when (selectedTab) {
                            0 -> DebtScreen(viewModel = customerViewModel)
                            1 -> StockScreen(
                                viewModel = stockViewModel,
                                onItemClick = { itemId, name ->
                                    inboundViewModel.selectItem(itemId)
                                    selectedItemName = name
                                    showHistorySheet = true
                                }
                            )
                        }
                    }

                    if (showHistorySheet) {
                        InboundHistoryScreen(
                            viewModel = inboundViewModel,
                            itemName = selectedItemName,
                            onDismiss = { showHistorySheet = false }
                        )
                    }
                }
            }
        }
    }
}
