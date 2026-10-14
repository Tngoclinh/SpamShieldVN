package vn.spamshield.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import vn.spamshield.data.BlockedCall
import vn.spamshield.data.BrandRule
import vn.spamshield.data.SpamRepository
import vn.spamshield.domain.PhoneMatcher
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpamShieldApp(
    repository: SpamRepository,
    roleHeld: Boolean,
    roleAvailable: Boolean,
    refreshKey: Int,
    onRequestRole: () -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var rules by remember { mutableStateOf(repository.loadRules()) }
    var history by remember { mutableStateOf(repository.loadHistory()) }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(refreshKey) {
        rules = repository.loadRules()
        history = repository.loadHistory()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Lá Chắn Cuộc Gọi", fontWeight = FontWeight.Bold)
                        Text(
                            "Chặn spam ngay trên thiết bị",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text("◉") },
                    label = { Text("Danh sách chặn") },
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text("◷") },
                    label = { Text("Nhật ký") },
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Text("＋", style = MaterialTheme.typography.headlineSmall)
                }
            }
        },
    ) { padding ->
        if (selectedTab == 0) {
            RulesScreen(
                modifier = Modifier.padding(padding),
                roleHeld = roleHeld,
                roleAvailable = roleAvailable,
                rules = rules,
                onRequestRole = onRequestRole,
                onToggle = { rule, enabled ->
                    rules = repository.setRuleEnabled(rule.id, enabled)
                },
                onDelete = { rule -> rules = repository.deleteRule(rule.id) },
            )
        } else {
            HistoryScreen(
                modifier = Modifier.padding(padding),
                history = history,
                onClear = {
                    repository.clearHistory()
                    history = emptyList()
                },
            )
        }
    }

    if (showAddDialog) {
        AddRuleDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, patterns ->
                rules = repository.addRule(name, patterns)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun RulesScreen(
    modifier: Modifier,
    roleHeld: Boolean,
    roleAvailable: Boolean,
    rules: List<BrandRule>,
    onRequestRole: () -> Unit,
    onToggle: (BrandRule, Boolean) -> Unit,
    onDelete: (BrandRule) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            StatusCard(roleHeld, roleAvailable, onRequestRole)
        }
        item {
            Text(
                "Thương hiệu và mẫu số",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                "Tên dùng để nhóm. Android cung cấp số gọi đến, vì vậy hãy thêm các số hoặc đầu số của thương hiệu.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (rules.isEmpty()) {
            item { EmptyRulesCard() }
        } else {
            items(rules, key = { it.id }) { rule ->
                RuleCard(rule, onToggle, onDelete)
            }
        }
        item { Spacer(Modifier.height(72.dp)) }
    }
}

@Composable
private fun StatusCard(roleHeld: Boolean, roleAvailable: Boolean, onRequestRole: () -> Unit) {
    val container = if (roleHeld) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = container),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                if (roleHeld) "Bảo vệ đang hoạt động" else "Cần kích hoạt bảo vệ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                when {
                    roleHeld -> "Ứng dụng đã được chọn làm dịch vụ nhận diện và chặn cuộc gọi."
                    !roleAvailable -> "Thiết bị này không cung cấp vai trò sàng lọc cuộc gọi."
                    else -> "Android cần bạn xác nhận ứng dụng được phép sàng lọc cuộc gọi."
                },
                style = MaterialTheme.typography.bodyMedium,
            )
            if (!roleHeld && roleAvailable) {
                Spacer(Modifier.height(12.dp))
                Button(onClick = onRequestRole) { Text("Kích hoạt ngay") }
            }
        }
    }
}

@Composable
private fun EmptyRulesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Chưa có thương hiệu nào", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(
                "Nhấn nút + để thêm thương hiệu và các mẫu số cần chặn.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RuleCard(
    rule: BrandRule,
    onToggle: (BrandRule, Boolean) -> Unit,
    onDelete: (BrandRule) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(rule.brandName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "${rule.patterns.size} mẫu số",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(checked = rule.enabled, onCheckedChange = { onToggle(rule, it) })
            }
            Spacer(Modifier.height(10.dp))
            Text(rule.patterns.joinToString("  •  "), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { onDelete(rule) }) { Text("Xóa thương hiệu") }
        }
    }
}

@Composable
private fun HistoryScreen(
    modifier: Modifier,
    history: List<BlockedCall>,
    onClear: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Cuộc gọi đã chặn", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Chỉ lưu cục bộ tối đa 100 mục", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (history.isNotEmpty()) OutlinedButton(onClick = onClear) { Text("Xóa hết") }
            }
        }
        if (history.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 56.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("Chưa có cuộc gọi nào bị chặn", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(history) { call ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(call.brandName, fontWeight = FontWeight.Bold)
                        Text(call.phoneNumber.ifBlank { "Số không xác định" })
                        Text(
                            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                                .format(Date(call.blockedAtMillis)),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddRuleDialog(onDismiss: () -> Unit, onAdd: (String, List<String>) -> Unit) {
    var brandName by remember { mutableStateOf("") }
    var numberInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm thương hiệu") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Ví dụ: nhập “Quảng cáo BĐS” và các mẫu 0247300*, 0901234567. Dấu * ở cuối nghĩa là mọi số cùng đầu số.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                OutlinedTextField(
                    value = brandName,
                    onValueChange = { brandName = it; error = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Tên thương hiệu / nhóm") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = numberInput,
                    onValueChange = { numberInput = it; error = null },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Số hoặc mẫu số") },
                    supportingText = { Text("Mỗi mẫu một dòng hoặc cách nhau bằng dấu phẩy") },
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = error != null,
                )
                if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val patterns = numberInput
                        .split(',', '\n', ';')
                        .map(PhoneMatcher::sanitizePattern)
                        .filter(String::isNotEmpty)
                        .distinct()
                    when {
                        brandName.isBlank() -> error = "Hãy nhập tên thương hiệu hoặc tên nhóm."
                        patterns.isEmpty() -> error = "Hãy nhập ít nhất một số hoặc mẫu số hợp lệ."
                        else -> onAdd(brandName.trim(), patterns)
                    }
                }
            ) { Text("Thêm vào danh sách") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } },
    )
}
