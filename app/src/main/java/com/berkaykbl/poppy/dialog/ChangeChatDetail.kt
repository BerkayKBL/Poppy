package com.berkaykbl.poppy.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.databinding.FragmentChatNameBinding

class ChangeChatDetail(type: String, canEmpty: Boolean = false) : DialogFragment() {

    private lateinit var binding : FragmentChatNameBinding
    private lateinit var callback : CallbackChatDetail
    private val changeType = type
    private val canEmpty = canEmpty

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private var mListener: CallbackChatDetail? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as CallbackChatDetail
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MyDialogListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view : View = inflater.inflate(R.layout.fragment_chat_name, container, false)
        val newName : TextView = view.findViewById<TextView>(R.id.editTextName)
        val confirm : TextView = view.findViewById<TextView>(R.id.confirm)
        val cancel : TextView = view.findViewById<TextView>(R.id.cancel)

        cancel.setOnClickListener {
            dismiss()
        }

        confirm.setOnClickListener {
            if (canEmpty || newName.text.isNotEmpty()) {
                mListener?.onChatNameChange(newName.text.toString(), changeType)
                dismiss()
            }
        }
        return view
    }



}

interface CallbackChatDetail {
    fun onChatNameChange(name : String, type: String)
}