package com.example.cyberdefender.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberdefender.model.StoryDialogChoice
import com.example.cyberdefender.model.StoryDialogStep
import com.example.cyberdefender.ui.theme.*

/**
 * 第1話突入前のXPチャット風プロローグ会話画面 (Windows Messengerスタイル)
 */
@Composable
fun StoryDialog(
    step: StoryDialogStep,
    onChoiceSelected: (StoryDialogChoice) -> Unit
) {
    XpClassicDialog(
        title = "Windows Messenger - [社内会話] ${step.speaker}",
        onDismissRequest = {
            // スキップ選択肢（最後の選択肢）を実行
            onChoiceSelected(step.choices.last())
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // メッセンジャーのヘッダー風
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8EEF7))
                    .border(1.dp, Color(0xFFB0C4DE))
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color(0xFF0055EA), RoundedCornerShape(2.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MSN",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PixelFontFamily
                        )
                    }
                    Text(
                        text = "送信者: ${step.speaker} (内線: 101 / オンライン)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003399),
                        fontFamily = PixelFontFamily
                    )
                }
            }

            // 課長からのメッセージ表示 (白地クラシック会話エリア)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFF808080), RoundedCornerShape(3.dp))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${step.speaker} の発言:",
                        color = Color(0xFF0055EA),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PixelFontFamily
                    )
                    Text(
                        text = step.message,
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontFamily = PixelFontFamily,
                        lineHeight = 18.sp
                    )
                }
            }

            // 自分の発言の選択肢 (操作域拡大ボタン)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "あなたの返答を選択してください:",
                    color = XpTextDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PixelFontFamily
                )

                step.choices.forEach { choice ->
                    XpButton(
                        text = choice.text,
                        onClick = { onChoiceSelected(choice) },
                        modifier = Modifier.fillMaxWidth(),
                        isPrimary = step.isFinalStep,
                        fontSize = 12,
                        minHeight = 44
                    )
                }
            }
        }
    }
}
