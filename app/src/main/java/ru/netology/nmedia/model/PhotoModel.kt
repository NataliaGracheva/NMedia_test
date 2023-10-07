package ru.netology.nmedia.model

import android.net.Uri
import ru.netology.nmedia.enumeration.AttachmentType
import java.io.InputStream

data class PhotoModel(val uri: Uri? = null, val stream: InputStream? = null, val type: AttachmentType? = null)