package com.example.cyberdefender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberdefender.model.ChatMessage
import com.example.cyberdefender.model.ChatSenderType
import com.example.cyberdefender.ui.theme.*

/**
 * 上部に配置するチャット画面風のタイムライン表示
 * （内線着信、社内メール、情シスAI、システム監視アラートが時系列で流れる）
 */
@Composable
fun ChatTimelineView(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // 新着メッセージが届いたら最下部へ自動スクロール
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF0F172A)) // 深いネイビー（監視モニタ風）
            .border(2.dp, Color(0xFF334155), RoundedCornerShape(4.dp))
            .padding(6.dp)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubbleItem(message = msg)
            }
        }
    }
}

@Composable
fun ChatBubbleItem(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val (bubbleBg, borderColor, speakerColor) = when (message.senderType) {
        ChatSenderType.PHONE -> Triple(Color(0xFF2E1065), Color(0xFF7C3AED), Color(0xFFC4B5FD)) // 紫（内線）
        ChatSenderType.EMAIL -> Triple(Color(0xFF1E293B), Color(0xFF38BDF8), Color(0xFF7DD3FC)) // 青（メール）
        ChatSenderType.AI -> Triple(Color(0xFF064E3B), Color(0xFF10B981), Color(0xFF6EE7B7))    // 緑（AI）
        ChatSenderType.ALERT -> Triple(Color(0xFF450A0A), Color(0xFFEF4444), Color(0xFFFCA5A5)) // 赤（アラート）
        ChatSenderType.COMPLAINT -> Triple(Color(0xFF500724), Color(0xFFF43F5E), Color(0xFFFDA4AF)) // 濃赤（激怒）
        ChatSenderType.ACTION -> Triple(Color(0xFF172554), Color(0xFF3B82F6), Color(0xFF93C5FD)) // 水色（操作）
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(bubbleBg)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = message.senderType.icon,
                    fontSize = 12.sp
                )
                Text(
                    text = message.speaker,
                    color = speakerColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily
                )
            }
            Text(
                text = message.timestamp,
                color = Color(0xFF94A3B8),
                fontSize = 9.sp,
                fontFamily = PixelFontFamily
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = message.message,
            color = if (message.isImportant) Color(0xFFFEF08A) else Color(0xFFF1F5F9),
            fontSize = 11.sp,
            fontFamily = PixelFontFamily,
            lineHeight = 16.sp,
            fontWeight = if (message.isImportant) FontWeight.Bold else FontWeight.Normal
        )
    }
}
