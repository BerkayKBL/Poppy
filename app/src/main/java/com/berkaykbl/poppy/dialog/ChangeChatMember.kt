package com.berkaykbl.poppy.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.berkaykbl.poppy.R
import com.berkaykbl.poppy.databinding.FragmentChatMemberBinding

class ChangeChatMember(userId: String, isAdmin: Boolean) : DialogFragment() {

    private lateinit var binding: FragmentChatMemberBinding
    private lateinit var callback: CallbackChatMember
    private var member_id = userId
    private var memberIsAdmin = isAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private var mListener: CallbackChatMember? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as CallbackChatMember
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement MyDialogListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_chat_member, container, false)
        val kick: TextView = view.findViewById(R.id.kick)
        val changeAdmin: TextView = view.findViewById(R.id.changeAdmin)
        val cancel: TextView = view.findViewById(R.id.cancel)

        cancel.setOnClickListener {
            dismiss()
        }



        if (memberIsAdmin) {
            changeAdmin.setText(R.string.dismiss_admin)
        } else {
            changeAdmin.setText(R.string.make_admin)
        }

        kick.setOnClickListener {
            mListener!!.onChatMemberChange(member_id, "kick")
            dismiss()
        }

        changeAdmin.setOnClickListener {
            mListener!!.onChatMemberChange(
                member_id, if (memberIsAdmin) {
                    "dismiss_admin"
                } else {
                    "make_admin"
                }
            )
            dismiss()
        }
        return view
    }


}

interface CallbackChatMember {
    fun onChatMemberChange(member_id: String, action: String)
}