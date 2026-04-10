package com.example.makhazany.Presentation.Ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.makhazany.ui.theme.*
import com.example.makhazany.Data.Local.Entity.CustomerEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtScreen(viewModel: CustomerViewModel) {
    val customers by viewModel.customers.collectAsState()
    val totalDebt by viewModel.totalDebt.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "إدارة المخزن", 
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryPurple
                    ) 
                },
                navigationIcon = { 
                    InteractiveIconButton(
                        icon = Icons.Default.Menu,
                        onClick = { /* Menu logic */ }
                    )
                },
                actions = { 
                    InteractiveIconButton(
                        icon = Icons.Default.Notifications,
                        badgeCount = 3,
                        onClick = { Toast.makeText(context, "لا توجد إشعارات جديدة", Toast.LENGTH_SHORT).show() }
                    )
                    InteractiveIconButton(
                        icon = Icons.Default.Search,
                        onClick = { /* Search logic */ }
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(if (isPressed) 0.85f else 1f, label = "fabScale")

            FloatingActionButton(
                onClick = {},
                containerColor = PrimaryPurple,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.scale(scale),
                interactionSource = interactionSource
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(LightGray)
        ) {
            // Header Section with Animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(PrimaryPurple)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("إجمالي مديونيات العملاء", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val animatedTotalDebt by animateFloatAsState(
                        targetValue = totalDebt.toFloat(),
                        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                        label = "totalDebtAnimation"
                    )
                    
                    Text(
                        "${String.format("%.2f", animatedTotalDebt)} ج.م",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Interactive Filter Bar
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .offset(y = (-30).dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterActionItem(
                        icon = Icons.Default.FilterList,
                        text = "تصفية النتائج",
                        onClick = { /* Filter logic */ }
                    )
                    
                    VerticalDivider(modifier = Modifier.height(24.dp).width(1.dp), color = LightGray)
                    
                    FilterActionItem(
                        icon = Icons.Default.KeyboardArrowDown,
                        text = "ترتيب حسب الأحدث",
                        trailingIcon = true,
                        onClick = { /* Sort logic */ }
                    )
                }
            }

            // List Title Section
            Row(
                modifier = Modifier.padding(horizontal = 24.dp).padding(top = 0.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("قائمة المديونيات", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = PrimaryPurple)
                
                Surface(
                    color = SecondaryPurple.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "${customers.size} عميل",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPurple
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Lazy Column
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(customers, key = { it.id }) { customer ->
                    CustomerDebtItem(customer)
                }
            }
        }
    }
}

@Composable
fun FilterActionItem(
    icon: ImageVector,
    text: String,
    trailingIcon: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "filterItemScale")

    Row(
        modifier = Modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!trailingIcon) {
            Icon(icon, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextGray)
        if (trailingIcon) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(icon, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun CustomerDebtItem(customer: CustomerEntity) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1f, label = "itemScale")
    val elevation by animateDpAsState(if (isPressed) 0.dp else 4.dp, label = "itemElevation")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null) { /* Detail logic */ },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(elevation)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                color = SecondaryPurple.copy(alpha = 0.5f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        customer.customerName.take(1),
                        color = PrimaryPurple,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(customer.customerName, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${customer.customerType} - ${customer.address}", 
                    color = TextGray, 
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "ج.م ${String.format("%.1f", customer.customerDebt)}",
                    color = DebtRed,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("منذ ساعتين", color = Color.Gray, fontSize = 10.sp)
            }
        }
    }
}
