package com.ldlywt.note.ui.page.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.ldlywt.note.R
import com.ldlywt.note.bean.Attachment
import com.ldlywt.note.utils.str
import kotlinx.coroutines.launch

@Composable
fun InputImage(
    attachment: Attachment,
    isEdit: Boolean,
    delete: (path: String) -> Unit,
    onclick: () -> Unit = {}
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box {
        AsyncImage(
            model = attachment.path,
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .zIndex(1f)
                .clip(RoundedCornerShape(2.dp))
                .clickable {
                    if (isEdit) {
                        menuExpanded = true
                    } else {
                        onclick()
                    }
                },
            contentScale = ContentScale.Crop
        )
        if (isEdit) {
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                properties = PopupProperties(focusable = false)
            ) {
                DropdownMenuItem(
                    text = { Text(R.string.delete.str) },
                    onClick = {
                        scope.launch {
                            delete(attachment.path)
                            menuExpanded = false
                        }
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = null
                        )
                    })
            }
        }
    }
}