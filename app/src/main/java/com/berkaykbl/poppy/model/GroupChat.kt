package com.berkaykbl.poppy.model

class GroupChat(
    val chat_id: String = "",
    val chat_name: String = "",
    val chat_description: String = "",
    val chat_image: String = "",
    val last_message: String = "",
    val last_message_time: String = "",
    val last_message_id: String = "",
    val last_message_sender_id: String = "",
    val members: HashMap<String, HashMap<String, Any>> = HashMap(),
)