package com.berkaykbl.poppy.manager

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GroupMessageManger {

    fun sendMessage(chatDataRef: DatabaseReference?, chat_id: String, message: String, time: String, senderId: String) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference()

        val messageHash = HashMap<String, String>()
        messageHash.put("chat_id", chat_id)
        messageHash.put("message", message)
        messageHash.put(
            "sender_id",
            senderId
        )

        val chatMessage = ref.child("GroupMessages").child(chat_id).push()
        messageHash.put("message_id", chatMessage.key!!)
        chatMessage.setValue(messageHash)
        chatDataRef!!.child("last_message_id").setValue(chatMessage.key!!)
        chatDataRef.child("last_message").setValue(message)
        chatDataRef.child("last_message_sender_id").setValue(senderId)
    }

    fun readMessage(chatId: String, messageId:String, userId:String) {
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference()
        ref.child("Chats").child(chatId).child("members").child(userId).child("last_read_message_id").setValue(messageId)
    }
}