package com.cometchat.pro.androiduikit.ComponentFragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.Fragment;

import com.cometchat.pro.androiduikit.R;
import com.cometchat.pro.androiduikit.databinding.FragmentConversationListBinding;
import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.ConversationsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.User;

import java.util.List;

import constant.StringContract;
import listeners.OnItemClickListener;
import screen.messagelist.CometChatMessageListActivity;

public class ConversationListViewFragment extends Fragment {

    FragmentConversationListBinding conversationBinding;
    ObservableArrayList<Conversation> conversationlist = new ObservableArrayList<>();
    ConversationsRequest conversationsRequest;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        conversationBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_conversation_list,container,false);
        getConversations();
        conversationBinding.setConversationList(conversationlist);
        conversationBinding.cometchatConversationList.setItemClickListener(new OnItemClickListener<Conversation>()
        {
            @Override
            public void OnItemClick(Conversation conversation, int position) {
                Intent intent = new Intent(getContext(), CometChatMessageListActivity.class);
                intent.putExtra(StringContract.IntentStrings.TYPE,conversation.getConversationType());
                if (conversation.getConversationType().equals(CometChatConstants.CONVERSATION_TYPE_GROUP))
                {
                    intent.putExtra(StringContract.IntentStrings.NAME,((Group)conversation.getConversationWith()).getName());
                    intent.putExtra(StringContract.IntentStrings.GUID,((Group)conversation.getConversationWith()).getGuid());
                    intent.putExtra(StringContract.IntentStrings.GROUP_OWNER,((Group)conversation.getConversationWith()).getOwner());
                    intent.putExtra(StringContract.IntentStrings.AVATAR,((Group)conversation.getConversationWith()).getIcon());

                }
                else
                {
                    intent.putExtra(StringContract.IntentStrings.NAME,((User)conversation.getConversationWith()).getName());
                    intent.putExtra(StringContract.IntentStrings.UID,((User)conversation.getConversationWith()).getUid());
                    intent.putExtra(StringContract.IntentStrings.AVATAR,((User)conversation.getConversationWith()).getAvatar());
                    intent.putExtra(StringContract.IntentStrings.STATUS,((User)conversation.getConversationWith()).getStatus());
                }
                startActivity(intent);
            }

            @Override
            public void OnItemLongClick(Conversation var, int position) {
                super.OnItemLongClick(var, position);
            }
        });
        return conversationBinding.getRoot();
    }

    private void getConversations() {
        if (conversationsRequest==null)
        {
            conversationsRequest = new ConversationsRequest.ConversationsRequestBuilder().setLimit(30).build();
        }
        conversationsRequest.fetchNext(new CometChat.CallbackListener<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                conversationBinding.contactShimmer.stopShimmer();
                conversationBinding.contactShimmer.setVisibility(View.GONE);
                conversationlist.addAll(conversations);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e( "onError: ",e.getMessage());
            }
        });
    }
}
