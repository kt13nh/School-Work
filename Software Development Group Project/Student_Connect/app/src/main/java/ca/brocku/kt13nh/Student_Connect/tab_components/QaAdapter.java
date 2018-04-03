


package ca.brocku.kt13nh.Student_Connect.tab_components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import ca.brocku.kt13nh.Student_Connect.R;
import ca.brocku.kt13nh.Student_Connect.chatroom_components.Chatroom;
import ca.brocku.kt13nh.Student_Connect.qa_components.QaDisplay;

public class QaAdapter extends
        RecyclerView.Adapter<QaViewHolder> {
    private ArrayList<Map<String, String>> arrayList;
    private Context context;


    public QaAdapter(Context context) {
        this.context = context;
        this.arrayList = new ArrayList<>();
    }

    public void addQuestion(Map<String, String> questionData) {
        arrayList.add(questionData);
    }


    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);

    }

    @Override
    public void onBindViewHolder(QaViewHolder holder,
                                 int position) {


        final QaViewHolder mainHolder = (QaViewHolder) holder;
        mainHolder.title.setText(arrayList.get(position).get("title"));
        mainHolder.course.setText(arrayList.get(position).get("course"));
    }


    /*
    * set the view onclick listeners and make sure they are transferring data correctly
    * */
    @Override
    public QaViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.qa_row, viewGroup, false);
        final QaViewHolder mainHolder = new QaViewHolder(mainGroup) {
            @Override
            public String toString() {
                return super.toString();
            }
        };
        //on click listener to create a map and send that data to the Q/A intent to be user to
        //display the information for the intent to use
        final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mainHolder.getLayoutPosition();
                Map<String, String> question = arrayList.get(position);
                Intent qaIntent = new Intent(context, QaDisplay.class);
                qaIntent.putExtra("questionID", question.get("questionID"));
                qaIntent.putExtra("title", question.get("title"));
                qaIntent.putExtra("course", question.get("course"));
                qaIntent.putExtra("description", question.get("description"));
                qaIntent.putExtra("askedBy", question.get("askedBy"));
                qaIntent.putExtra("time", question.get("time"));
                context.startActivity(qaIntent);
            }


        };
        mainGroup.setOnClickListener(mOnClickListener);


        return mainHolder;

    }


}