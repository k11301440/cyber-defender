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
 * Windows XP クラシックイベントビューア / Outlook Express風のタイムラインログ
 * （濃紺をやめ、XPらしい白地＋クラシックベベル枠に刷新。絵文字完全排除）
 */
@Composable
fun ChatTimelineView(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // XPクラシックな凹みベベル枠コンテナ
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .background(XpLogListBg) // 白地
            .border(2.dp, XpBorderShadow, RoundedCornerShape(2.dp))
    ) {
        // イベントビューア風ヘッダー
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(XpLogHeaderBg)
                .border(width = 1.dp, color = Color(0xFFACA899))
                .padding(horizontal = 8.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "イベントの種類 / 送信元",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = XpTextDark,
                fontFamily = PixelFontFamily
            )
            Text(
                text = "時刻",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = XpTextDark,
                fontFamily = PixelFontFamily
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatLogItem(message = msg)
            }
        }
    }
}

@Composable
fun ChatLogItem(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    val (tagBg, tagText, borderColor) = when (message.senderType) {
        ChatSenderType.SYSTEM -> Triple(Color(0xFFE2E8F0), Color(0xFF334155), Color(0xFF94A3B8))
        ChatSenderType.PHONE -> Triple(Color(0xFFEDE9FE), Color(0xFF5B21B6), Color(0xFF8B5CF6))
        ChatSenderType.EMAIL -> Triple(Color(0xFFE0F2FE), Color(0xFF0369A1), Color(0xFF38BDF8))
        ChatSenderType.AI -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), Color(0xFF22C55E))
        ChatSenderType.SIEM -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), Color(0xFFF59E0B))
        ChatSenderType.ALERT -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), Color(0xFFEF4444))
        ChatSenderType.COMPLAINT -> Triple(Color(0xFFFFE4E6), Color(0xFF881337), Color(0xFFF43F5E))
        ChatSenderType.ACTION -> Triple(Color(0xFFDBEAFE), Color(0xFF1E40AF), Color(0xFF3B82F6))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .background(Color(0xFFFAFAF9))
            .border(1.dp, borderColor, RoundedCornerShape(2.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 送信元タグ
                Box(
                    modifier = Modifier
                        .background(tagBg, RoundedCornerShape(2.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(2.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = message.senderType.tag,
                        color = tagText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                }

                Text(
                    text = message.speaker,
                    color = Color.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily
                )
            }

            Text(
                text = message.timestamp,
                color = Color(0xFF64748B),
                fontSize = 10.sp,
                fontFamily = PixelFontFamily
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = message.message,
            color = if (message.isImportant) Color(0xFF991B1B) else Color(0xFF1E293B),
            fontSize = 12.sp,
            fontFamily = PixelFontFamily,
            lineHeight = 17.sp,
            fontWeight = if (message.isImportant) FontWeight.Bold else FontWeight.Normal
        )
    }
}
