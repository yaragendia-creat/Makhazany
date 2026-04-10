package com.example.makhazany.Presentation.Ui

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.makhazany.Data.Local.Entity.InboundEntity
import com.example.makhazany.Data.Local.Entity.ItemEntity
import com.example.makhazany.Data.Local.Relation.ItemWithStock
import com.example.makhazany.ui.theme.*
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockScreen(
    viewModel: StockViewModel,
    onItemClick: (Int, String) -> Unit
) {
    val stockItems by viewModel.stockItems.collectAsState()
    val totalItems by viewModel.totalItems.collectAsState()
    val shortages by viewModel.shortages.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val context = LocalContext.current

    var selectedInbound by remember { mutableStateOf<Pair<String, InboundEntity>?>(null) }
    var showAddProductSheet by remember { mutableStateOf(false) }
    var showImportSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "المخازن", 
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryPurple
                    ) 
                },
                navigationIcon = { 
                    InteractiveIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        onClick = { /* Back logic */ }
                    )
                },
                actions = { 
                    InteractiveIconButton(
                        icon = Icons.Default.Notifications,
                        badgeCount = 3,
                        onClick = { Toast.makeText(context, "لا توجد إشعارات جديدة", Toast.LENGTH_SHORT).show() }
                    )
                    InteractiveIconButton(
                        icon = Icons.Default.Settings,
                        onClick = { /* Settings logic */ }
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(LightGray)
        ) {
            
            // Action Buttons
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedActionButton(
                    text = "منتج جديد",
                    icon = Icons.Default.AddBox,
                    containerColor = PrimaryPurple,
                    contentColor = Color.White,
                    onClick = { showAddProductSheet = true },
                    modifier = Modifier.weight(1f)
                )
                AnimatedActionButton(
                    text = "استيراد",
                    icon = Icons.Default.UploadFile,
                    containerColor = Color.White,
                    contentColor = PrimaryPurple,
                    onClick = { showImportSheet = true },
                    modifier = Modifier.weight(1f),
                    isOutlined = true
                )
            }

            // Summary Cards
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = PrimaryPurple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("نظرة عامة على الرصيد", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(
                            label = "إجمالي الأصناف", 
                            value = totalItems.toString(), 
                            color = PrimaryPurple,
                            isSelected = currentFilter == StockFilter.ALL,
                            onClick = { viewModel.setFilter(StockFilter.ALL) }
                        )
                        StatItem(
                            label = "حركة اليوم", 
                            value = "24", 
                            color = Color.Black,
                            isSelected = false,
                            onClick = {}
                        )
                        StatItem(
                            label = "نواقص", 
                            value = shortages.toString(), 
                            color = Color.Red,
                            isSelected = currentFilter == StockFilter.SHORTAGE,
                            onClick = { viewModel.setFilter(StockFilter.SHORTAGE) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Inventory List Section
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "قائمة الجرد", 
                            fontWeight = FontWeight.ExtraBold, 
                            fontSize = 22.sp, 
                            color = PrimaryPurple
                        )
                        
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = { Text("ابحث هنا...", fontSize = 12.sp) },
                            modifier = Modifier
                                .width(180.dp)
                                .height(52.dp)
                                .animateContentSize(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = { 
                                val interactionSource = remember { MutableInteractionSource() }
                                val isPressed by interactionSource.collectIsPressedAsState()
                                val iconScale by animateFloatAsState(if (isPressed) 1.3f else 1f, label = "searchIconScale")
                                
                                Icon(
                                    Icons.Default.Search, 
                                    contentDescription = null, 
                                    modifier = Modifier
                                        .size(20.dp)
                                        .scale(iconScale)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null,
                                            onClick = { /* Focus logic */ }
                                        ),
                                    tint = if (searchQuery.isNotEmpty()) PrimaryPurple else Color.Gray
                                ) 
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryPurple,
                                unfocusedBorderColor = LightGray,
                                cursorColor = PrimaryPurple
                            ),
                            singleLine = true
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    // Interactive Table Header (Sortable)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(LightGray.copy(alpha = 0.4f))
                            .padding(10.dp)
                    ) {
                        SortableHeaderItem(
                            text = "الصنف",
                            modifier = Modifier.weight(1.5f),
                            textAlign = TextAlign.End,
                            sortOrder = sortOrder,
                            ascOrder = SortOrder.NAME_ASC,
                            descOrder = SortOrder.NAME_DESC,
                            onClick = { viewModel.toggleSortByName() }
                        )
                        Text(
                            "التصنيف", 
                            modifier = Modifier.weight(1f), 
                            fontSize = 12.sp, 
                            textAlign = TextAlign.Center, 
                            fontWeight = FontWeight.Bold, 
                            color = TextGray
                        )
                        SortableHeaderItem(
                            text = "الرصيد",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start,
                            sortOrder = sortOrder,
                            ascOrder = SortOrder.QTY_ASC,
                            descOrder = SortOrder.QTY_DESC,
                            onClick = { viewModel.toggleSortByQty() }
                        )
                    }

                    // Results List with animations
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(stockItems, key = { it.item.id }) { item ->
                            Box(modifier = Modifier.animateItem()) {
                                StockListItem(item) {
                                    if (searchQuery.isNotEmpty() && item.inboundHistory.any { it.invoiceNumber == searchQuery }) {
                                        val inbound = item.inboundHistory.first { it.invoiceNumber == searchQuery }
                                        selectedInbound = item.item.name to inbound
                                    } else if (item.inboundHistory.isNotEmpty()) {
                                        selectedInbound = item.item.name to item.inboundHistory.first()
                                    } else {
                                        onItemClick(item.item.id, item.item.name)
                                    }
                                }
                            }
                        }
                    }
                    
                    // Animated search feedback
                    AnimatedVisibility(
                        visible = searchQuery.isNotEmpty() && stockItems.isEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                            Text("لا توجد نتائج مطابقة لـ \"$searchQuery\"", color = TextGray, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }

    // Bottom Sheets and Overlays
    if (showAddProductSheet) {
        AddProductBottomSheet(
            onDismiss = { showAddProductSheet = false },
            onAdd = { item, qty ->
                viewModel.addItem(item, qty)
                showAddProductSheet = false
            }
        )
    }

    if (showImportSheet) {
        ImportBottomSheet(onDismiss = { showImportSheet = false })
    }

    if (selectedInbound != null) {
        InvoiceBottomSheet(
            itemName = selectedInbound!!.first,
            inbound = selectedInbound!!.second,
            onDismiss = { selectedInbound = null },
            onShare = { shareInvoice(context, selectedInbound!!.first, selectedInbound!!.second) },
            onPrint = { printInvoice(context, selectedInbound!!.first, selectedInbound!!.second) }
        )
    }
}

@Composable
fun SortableHeaderItem(
    text: String,
    modifier: Modifier,
    textAlign: TextAlign,
    sortOrder: SortOrder,
    ascOrder: SortOrder,
    descOrder: SortOrder,
    onClick: () -> Unit
) {
    val isSorted = sortOrder == ascOrder || sortOrder == descOrder
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.9f else 1f, label = "headerScale")

    Row(
        modifier = modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (textAlign == TextAlign.End) Arrangement.End else Arrangement.Start
    ) {
        if (textAlign == TextAlign.Start && isSorted) {
            Icon(
                imageVector = if (sortOrder == ascOrder) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSorted) FontWeight.ExtraBold else FontWeight.Bold,
            color = if (isSorted) PrimaryPurple else TextGray,
            textAlign = textAlign
        )
        if (textAlign == TextAlign.End && isSorted) {
            Icon(
                imageVector = if (sortOrder == ascOrder) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = PrimaryPurple,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun InteractiveIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    badgeCount: Int = 0,
    tint: Color = PrimaryPurple
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.8f else 1f, label = "iconButtonScale")
    
    Box(modifier = Modifier.scale(scale), contentAlignment = Alignment.Center) {
        IconButton(onClick = onClick, interactionSource = interactionSource) {
            Icon(icon, contentDescription = null, tint = tint)
        }
        if (badgeCount > 0) {
            val infiniteTransition = rememberInfiniteTransition(label = "badgePulse")
            val badgeScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "badgeScale"
            )
            
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
                    .size(16.dp)
                    .scale(badgeScale),
                color = Color.Red,
                shape = CircleShape
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductBottomSheet(
    onDismiss: () -> Unit,
    onAdd: (ItemEntity, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("إضافة صنف جديد", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = PrimaryPurple)
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("اسم الصنف") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("التصنيف") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("السعر") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("الكمية") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(
                            ItemEntity(
                                name = name,
                                sku = sku,
                                category = category,
                                price = price.toDoubleOrNull() ?: 0.0,
                                description = ""
                            ),
                            quantity.toDoubleOrNull() ?: 0.0
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text("إضافة للمخزن", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportBottomSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload, 
                contentDescription = null, 
                modifier = Modifier.size(64.dp), 
                tint = PrimaryPurple
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("استيراد بيانات الأصناف", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("يمكنك استيراد قائمة الأصناف من ملف Excel أو CSV", textAlign = TextAlign.Center, color = TextGray)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Surface(
                onClick = { /* File picker logic */ },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(16.dp),
                color = LightGray.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.FileOpen, contentDescription = null, tint = PrimaryPurple)
                    Text("اختر ملفاً من هاتفك", color = PrimaryPurple, fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Color.Red)
            }
        }
    }
}

@Composable
fun AnimatedActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "actionButtonScale")

    Surface(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        contentColor = contentColor,
        border = if (isOutlined) androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryPurple) else null,
        shadowElevation = if (isPressed) 0.dp else 4.dp,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceBottomSheet(
    itemName: String,
    inbound: InboundEntity,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onPrint: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) },
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    color = LightGray.copy(alpha = 0.5f)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.padding(8.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("سجل شراء: $itemName", fontWeight = FontWeight.ExtraBold, color = PrimaryPurple, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.History, contentDescription = null, tint = PrimaryPurple)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = LightGray.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("رقم الفاتورة:", fontSize = 12.sp, color = TextGray)
                            Text("#${inbound.invoiceNumber}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        }
                        val date = SimpleDateFormat("dd MMMM yyyy", Locale("ar")).format(Date(inbound.inboundDate))
                        Text(date, fontSize = 13.sp, color = TextGray, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("الكمية المستلمة", fontSize = 12.sp, color = TextGray)
                            Text("الكمية: ${inbound.amount}", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("سعر الوحدة", fontSize = 12.sp, color = TextGray)
                            Text("السعر: ${inbound.pricePerUnit} ج.م", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryPurple)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onPrint,
                    modifier = Modifier.weight(1f).height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    shape = RoundedCornerShape(20.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Default.Print, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("طباعة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f).height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryPurple)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("مشاركة", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else if (isSelected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "statScale"
    )
    val elevation by animateDpAsState(if (isSelected || isPressed) 10.dp else 0.dp, label = "statElevation")
    
    Card(
        modifier = Modifier
            .width(100.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color.copy(alpha = 0.15f) else LightGray.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 10.sp, color = TextGray, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth(0.6f)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isSelected) color else color.copy(alpha = 0.2f))
            )
        }
    }
}

@Composable
fun StockListItem(
    itemWithStock: ItemWithStock,
    onClick: () -> Unit
) {
    val item = itemWithStock.item
    val stock = itemWithStock.stock
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val backgroundColor by animateColorAsState(if (isPressed) SecondaryPurple.copy(alpha = 0.2f) else Color.Transparent, label = "listBg")
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "listScale")

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .padding(horizontal = 4.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    color = SecondaryPurple.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = PrimaryPurple, modifier = Modifier.padding(10.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Text("SKU: ${item.sku}", fontSize = 12.sp, color = TextGray)
                }
            }
            Text(item.category ?: "عام", modifier = Modifier.weight(1f), fontSize = 13.sp, textAlign = TextAlign.Center, color = TextGray, fontWeight = FontWeight.Medium)
            Text("${stock?.quantity?.toInt() ?: 0}", modifier = Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.Black, color = PrimaryPurple, textAlign = TextAlign.Start)
        }
    }
}

fun shareInvoice(context: Context, itemName: String, inbound: InboundEntity) {
    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(inbound.inboundDate))
    val shareBody = """
        فاتورة شراء
        الصنف: $itemName
        رقم الفاتورة: ${inbound.invoiceNumber}
        التاريخ: $date
        الكمية: ${inbound.amount}
        سعر الوحدة: ${inbound.pricePerUnit} ج.م
        الإجمالي: ${inbound.amount * inbound.pricePerUnit} ج.م
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "فاتورة رقم ${inbound.invoiceNumber}")
        putExtra(Intent.EXTRA_TEXT, shareBody)
    }
    context.startActivity(Intent.createChooser(intent, "مشاركة الفاتورة عبر"))
}

fun printInvoice(context: Context, itemName: String, inbound: InboundEntity) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val jobName = "Invoice_${inbound.invoiceNumber}"
    
    printManager.print(jobName, object : PrintDocumentAdapter() {
        override fun onLayout(oldAttributes: PrintAttributes?, newAttributes: PrintAttributes, cancellationSignal: CancellationSignal?, callback: LayoutResultCallback, extras: Bundle?) {
            if (cancellationSignal?.isCanceled == true) {
                callback.onLayoutCancelled()
                return
            }
            val info = PrintDocumentInfo.Builder(jobName)
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(1)
                .build()
            callback.onLayoutFinished(info, true)
        }

        override fun onWrite(pages: Array<out PageRange>?, destination: ParcelFileDescriptor?, cancellationSignal: CancellationSignal?, callback: WriteResultCallback?) {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(300, 500, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            
            paint.textSize = 14f
            canvas.drawText("فاتورة شراء", 100f, 40f, paint)
            paint.textSize = 10f
            canvas.drawText("الصنف: $itemName", 20f, 80f, paint)
            canvas.drawText("رقم الفاتورة: ${inbound.invoiceNumber}", 20f, 100f, paint)
            canvas.drawText("الكمية: ${inbound.amount}", 20f, 120f, paint)
            canvas.drawText("السعر: ${inbound.pricePerUnit} ج.م", 20f, 140f, paint)
            canvas.drawText("الإجمالي: ${inbound.amount * inbound.pricePerUnit} ج.م", 20f, 160f, paint)

            pdfDocument.finishPage(page)
            try {
                pdfDocument.writeTo(FileOutputStream(destination?.fileDescriptor))
            } catch (e: IOException) {
                callback?.onWriteFailed(e.toString())
                return
            } finally {
                pdfDocument.close()
            }
            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        }
    }, null)
}
