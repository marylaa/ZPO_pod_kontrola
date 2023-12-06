package com.example.myapp.chat

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var rootView: View

    var receiverRoom: String? = null
    var senderRoom: String? = null
    private lateinit var senderUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val intent = intent
        val receiverUid = intent.getStringExtra("Id")

        senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sendButton)
        rootView = findViewById(android.R.id.content)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        mDbRef.child("chats").child(senderRoom!!).child("messages").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()

                for (postSnapshot in snapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)
                }

                messageAdapter.notifyDataSetChanged()

                // Przesuń RecyclerView na dół po zaktualizowaniu wiadomości
                scrollRecyclerViewToBottom()
            }

            override fun onCancelled(error: DatabaseError) {
                // Obsłuż błąd odczytu wiadomości
            }
        })

        sendButton.setOnClickListener {
            val messageText = messageBox.text.toString()

            if (messageText.isNotEmpty()) {
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale("pl", "PL")).format(Date())
                val messageObject = Message(messageText, senderUid, currentDate, currentTime)

                mDbRef.child("chats").child(senderRoom!!).child("messages").push().setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }

                messageBox.setText("")
            }
        }

        rootView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                val rootViewHeight = rootView.height
                val heightDiff = rootViewHeight - rootView.rootView.height

                if (heightDiff > 100) {
                    // Klawiatura jest widoczna, zmniejsz chatRecyclerView
                    adjustViewForKeyboard(true)
                } else {
                    // Klawiatura jest ukryta, przywróć pierwotny rozmiar chatRecyclerView
                    adjustViewForKeyboard(false)
                }
                return true
            }
        })
    }

    private fun adjustViewForKeyboard(isKeyboardVisible: Boolean) {
        val params = chatRecyclerView.layoutParams as ViewGroup.MarginLayoutParams

        if (isKeyboardVisible) {
            // Klawiatura jest widoczna, dostosuj widok chatRecyclerView
            params.bottomMargin = resources.getDimensionPixelSize(R.dimen.keyboard_height)
        } else {
            // Klawiatura jest ukryta, przywróć pierwotny rozmiar chatRecyclerView
            params.bottomMargin = 0
        }

        chatRecyclerView.layoutParams = params
    }

    private fun scrollRecyclerViewToBottom() {
        chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
    }
}
