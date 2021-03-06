package edu.uw.tcss450.group6project.ui.chat;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.group6project.R;
import edu.uw.tcss450.group6project.databinding.FragmentChatlistCardBinding;
import edu.uw.tcss450.group6project.model.NewMessageCountViewModel;

/**
 * A RecyclerViewAdapter to create scrolling list view of chats.
 *
 * @author Robert M, Aaron L
 * @version 2 November 2020
 */
public class ChatListRecyclerViewAdapter extends
        RecyclerView.Adapter<ChatListRecyclerViewAdapter.ChatListViewHolder> {

    private ChatRoomViewModel chatRoomViewModel;
    private ChatSendViewModel chatSendViewModel;
    private NewMessageCountViewModel newMessageCountViewModel;

    /**
     * A list of chats.
     */
    private final List<ChatRoom> mChats;

    /**
     * User jwt token.
     */
    private final String jwt;

    /**
     * User email.
     */
    private final String email;

    /**
     * The fragment's context.
     */
    private Context mContext;

    /**
     * Parameterized constructor method taking a list of chats.
     *
     * @param chatRoomViewModel chat room view model
     * @param jwt user jwt
     * @param email user email
     */
    public ChatListRecyclerViewAdapter(
            ChatRoomViewModel chatRoomViewModel,
            ChatSendViewModel chatSendViewModel,
            NewMessageCountViewModel newMessageCountViewModel,
            final String jwt,
            final String email) {
        this.chatRoomViewModel = chatRoomViewModel;
        this.chatSendViewModel = chatSendViewModel;
        this.newMessageCountViewModel = newMessageCountViewModel;
        this.mChats = chatRoomViewModel.getChatRooms();
        this.jwt = jwt;
        this.email = email;
    }

    /**
     * Parameterized constructor method taking a list of chats.
     *
     * @param chatRoomViewModel chat room view model
     * @param jwt user jwt
     * @param email user email
     */
    public ChatListRecyclerViewAdapter(
            ChatRoomViewModel chatRoomViewModel,
            ChatSendViewModel chatSendViewModel,
            NewMessageCountViewModel newMessageCountViewModel,
            final List<ChatRoom> chats,
            final String jwt,
            final String email) {
        this.chatRoomViewModel = chatRoomViewModel;
        this.chatSendViewModel = chatSendViewModel;
        this.newMessageCountViewModel = newMessageCountViewModel;
        this.mChats = chats;
        this.jwt = jwt;
        this.email = email;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ChatListViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chatlist_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        holder.setChat(mChats.get(position));
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    /**
     * Represents an individual row View from the list of rows in the Chat Recycler View.
     *
     * @author Robert M
     * @version 3 November 2020
     */
    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        /** The view. */
        public final View mView;

        /** The binding for the chat list card. */
        public FragmentChatlistCardBinding binding;

        /** The chat in the card. */
        private ChatRoom mChat;

        /**
         * Constructs the Chat view.
         *
         * @param view the view
         */
        public ChatListViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatlistCardBinding.bind(view);
        }

        /**
         * Sets each card view for a chat in the recycler view.
         *
         * @param chat the chatroom to setup
         */
        void setChat(final ChatRoom chat) {
            int chatRoomId = chat.getChatRoomID();
            mChat = chat;
            List<ChatMessage> messages = chat.getMessages();
            binding.buttonFullChat.setOnClickListener(view -> {
                setMessageRead();
                Navigation.findNavController(mView).navigate
                        (ChatListFragmentDirections.actionNavigationChatToChatFragment(chatRoomId));
            });
            binding.buttonDeleteChat.setOnClickListener(view -> {
                chatSendViewModel.sendMessage(chatRoomId, jwt, email + " has left the chat.");
                chatRoomViewModel.deleteChatRoom(jwt, chatRoomId, email, this);
            });
            binding.buttonAddContact.setOnClickListener(view -> {
                Navigation.findNavController(view)
                        .navigate(ChatListFragmentDirections
                                .actionNavigationChatToChatContactAddFormFragment(chatRoomId));
            });
            binding.textParticipants.setText(chat.getRoomName());
            binding.textPreview.setText(chat.getLastMessage());
            if (!messages.isEmpty() && !messages.get(messages.size() - 1).isRead()) {
                binding.textPreview.setTypeface(null, Typeface.BOLD);
            }
        }

        void setMessageRead() {
            int numUnread = 0;
            for(ChatMessage cm: mChat.getMessages()) {
                if(!cm.isRead()) {
                    cm.setIsRead(true);
                    numUnread++;
                }
            }
            newMessageCountViewModel.decrement(numUnread);
        }

        /**
         * The callback of after removing chat room
         */
        public void deleteChatCallback() {
            Toast.makeText(mContext, R.string.chat_delete, Toast.LENGTH_SHORT).show();
        }
    }
}
