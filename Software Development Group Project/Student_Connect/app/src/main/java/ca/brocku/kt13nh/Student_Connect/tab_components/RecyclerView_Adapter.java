package ca.brocku.kt13nh.Student_Connect.tab_components;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.brocku.kt13nh.Student_Connect.R;
import ca.brocku.kt13nh.Student_Connect.chatroom_components.Chatroom;


public class RecyclerView_Adapter extends
        RecyclerView.Adapter<DemoViewHolder> {
    private List<Map<String, Object>> arrayList;
    private Context context;
    private OnRecyclerClickListener onRecyclerClickListener;

    private TextView Tv;
    private ImageView image;
    private boolean myfav1=false;
    private int k=0;


    public RecyclerView_Adapter(Context context) {
        this.context = context;
        this.arrayList = new ArrayList<>();

    }

    public void addChatroom(Map<String, Object> chatroomData){
        arrayList.add(chatroomData);
    }

    public void clearChatrooms(){
        arrayList.clear();
    }


    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);

    }

    @Override
    public void onBindViewHolder(DemoViewHolder holder,
                                 int position) {
        final DemoViewHolder mainHolder = (DemoViewHolder) holder;
        image=(ImageView) mainHolder.getView().findViewById(R.id.iv_image);
        boolean isPublic = (boolean) arrayList.get(position).get("isPublic");
        if (isPublic)
            image.setImageResource(R.mipmap.globe);
        else
            image.setImageResource(R.mipmap.lock);

        //Setting text over textview
        mainHolder.title.setText(arrayList.get(position).get("ChatName").toString());

    }

    @Override
    public DemoViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.item_row, viewGroup, false);

        Tv = (TextView) mainGroup.findViewById(R.id.cardTitle);

        //  ArrayList<String> arrayList = new ArrayList<>();
//        for (int i = 0; i <3; i++) {
//
//            //  arrayList.add(courses[i]);//Adding items to recycler view
//            if(arrayList.indexOf(getItemId(i))==1 || arrayList.indexOf(getItemId(i))==2)
//            {
//                // TextView Tv = (TextView) inflater.inflate(R.id.TextView);
//
//                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
//
//                Tv.setTypeface(boldTypeface);
//            }
//        }
        final DemoViewHolder mainHolder = new DemoViewHolder(mainGroup) {
            @Override
            public String toString() {
                return super.toString();
            }
        };


        k= mainHolder.getLayoutPosition();
//        image=(ImageView) mainGroup.findViewById(R.id.iv_image);
//        if(k >= 0) {
//            boolean isPublic = (boolean) arrayList.get(k).get("isPublic");
//            if (isPublic)
//                image.setBackgroundResource(R.mipmap.globe);
//            else
//                image.setBackgroundResource(R.mipmap.lock);
//        }


        //Opens the chatroom activity and provides all the data for the chatroom
        //such as chatID, chatName, etc.
        final View.OnClickListener mOnClickListener = new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                int position = mainHolder.getLayoutPosition();
                Map<String, Object> chatroom = arrayList.get(position);
                Intent chatroomIntent = new Intent(context, Chatroom.class);
                chatroomIntent.putExtra("chatID", (String)chatroom.get("ChatID"));
                chatroomIntent.putExtra("chatroomName", (String)chatroom.get("ChatName"));
                chatroomIntent.putExtra("isPublic", (boolean)chatroom.get("isPublic"));
                chatroomIntent.putExtra("admin", (String)chatroom.get("admin"));
                context.startActivity(chatroomIntent);


            }};



        mainGroup.setOnClickListener(mOnClickListener);

// 13805 ////////////////////////////////
        return mainHolder;

    }



}