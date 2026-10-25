package com.example.cyberdefender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberdefender.model.BattleLog
import com.example.cyberdefender.model.LogType
import com.example.cyberdefender.ui.theme.*

/**
 * Windows XP コマンドプロンプト (cmd.exe) 風 イベントログ
 */
@Composable
fun TerminalLogView(
    logs: List<BattleLog>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF000000))
            .border(2.dp, XpBorderDark)
            .padding(4.dp)
    ) {
        Text(
            text = "C:\\WINDOWS\\system32\\cmd.exe - [情シス防御ログ監視]",
            color = Color(0xFFAAAAAA),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(logs, key = { it.id }) { log ->
                val (color, prefix) = when (log.type) {
                    LogType.INFO -> Pair(Color(0xFFEEEEEE), "[情報]")
                    LogType.WARNING -> Pair(Color(0xFFFFFF55), "[警告]")
                    LogType.ALERT -> Pair(Color(0xFFFF5555), "[警報]")
                    LogType.SUCCESS -> Pair(Color(0xFF55FF55), "[成功]")
                    LogType.CRITICAL -> Pair(Color(0xFFFF55FF), "[特効]")
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "$prefix ${log.sender}> ",
                        color = color,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = log.message,
                        color = if (log.type == LogType.CRITICAL) Color(0xFF55FF55) else Color(0xFFDDDDDD),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}
