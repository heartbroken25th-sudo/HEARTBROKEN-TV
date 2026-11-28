package com.heartbroken.tv.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heartbroken.tv.ui.theme.Gold
import com.heartbroken.tv.ui.theme.Obsidian700
import com.heartbroken.tv.ui.theme.Obsidian800
import com.heartbroken.tv.ui.theme.Obsidian900
import com.heartbroken.tv.ui.theme.TextSecondary

data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun HelpSupportScreen() {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val messages = remember { 
        mutableStateListOf(
            ChatMessage("Hello! I am the HEARTBROKEN TV AI assistant. How can I help you today?", false)
        ) 
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian900)
            .padding(16.dp)
    ) {
        // Contact Buttons
        Text(
            text = "Contact Developer Directly",
            style = MaterialTheme.typography.titleMedium,
            color = Gold,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ContactButton(
                text = "Email",
                icon = Icons.Default.Email,
                modifier = Modifier.weight(1f)
            ) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:aiheartbroken@gmail.com")
                    putExtra(Intent.EXTRA_SUBJECT, "HEARTBROKEN TV Support")
                }
                context.startActivity(intent)
            }
            ContactButton(
                text = "WhatsApp",
                icon = Icons.Default.SupportAgent,
                modifier = Modifier.weight(1f)
            ) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/256740779838")
                }
                context.startActivity(intent)
            }
            ContactButton(
                text = "Call",
                icon = Icons.Default.Phone,
                modifier = Modifier.weight(1f)
            ) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:0769618922")
                }
                context.startActivity(intent)
            }
        }

        Spacer(Modifier.height(24.dp))

        // AI Chat Area
        Text(
            text = "AI Support Chat",
            style = MaterialTheme.typography.titleMedium,
            color = Gold,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))

        // Chat History
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Obsidian800, RoundedCornerShape(12.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(msg)
            }
        }

        Spacer(Modifier.height(8.dp))

        // Chat Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask AI...", color = TextSecondary) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold,
                    unfocusedBorderColor = Obsidian700,
                    focusedContainerColor = Obsidian800,
                    unfocusedContainerColor = Obsidian800,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        val userMsg = inputText
                        messages.add(ChatMessage(userMsg, true))
                        inputText = ""
                        
                        // AI Logic
                        val response = getAiResponse(userMsg)
                        messages.add(ChatMessage(response, false))
                    }
                },
                modifier = Modifier
                    .background(Gold, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Obsidian900)
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val bgColor = if (message.isUser) Gold else Obsidian700
    val textColor = if (message.isUser) Obsidian900 else Color.White
    val alignment = if (message.isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .background(bgColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ContactButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(Obsidian800, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = text, tint = Gold)
        Spacer(Modifier.height(4.dp))
        Text(text, color = Color.White, fontSize = 12.sp)
    }
}

// The Local AI Logic (Offline)
private fun getAiResponse(query: String): String {
    val lowerQuery = query.lowercase()
    return when {
        lowerQuery.contains("hello") || lowerQuery.contains("hi") -> 
            "Hello! How can I help you today?"
        
        lowerQuery.contains("channel") || lowerQuery.contains("play") || lowerQuery.contains("stream") -> 
            "If a channel is not playing, it may be temporarily offline. Try pressing the Refresh button at the top right of the Live TV screen. If it still fails, it's a server issue."
        
        lowerQuery.contains("zoom") || lowerQuery.contains("rotate") || lowerQuery.contains("screen") -> 
            "While watching a video, tap the screen once to show the controls. You will see Zoom and Rotate buttons in the top right corner."
        
        lowerQuery.contains("update") || lowerQuery.contains("version") -> 
            "You are using the latest version of HEARTBROKEN TV. Updates are delivered automatically via the app."
        
        lowerQuery.contains("developer") || lowerQuery.contains("contact") || lowerQuery.contains("human") -> 
            "You can reach the developer directly via Email (aiheartbroken@gmail.com), WhatsApp (0740779838), or Call (0769618922). Use the buttons at the top of this screen."

        else -> 
            "I'm not sure about that specific question. The developer might be offline, but you can reach them directly using the Email, WhatsApp, or Call buttons above!"
    }
}
