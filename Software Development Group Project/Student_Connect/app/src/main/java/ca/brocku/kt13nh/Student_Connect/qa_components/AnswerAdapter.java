package ca.brocku.kt13nh.Student_Connect.qa_components;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import ca.brocku.kt13nh.Student_Connect.R;
import ca.brocku.kt13nh.Student_Connect.chatroom_components.Message;

/**
 * Author: Goal Diggers
 * ArrayAdapter that serves as a container for answer objects
 * Also sets the view for these answer ojects so that they are displayed properly
 */

public class AnswerAdapter extends ArrayAdapter<Answer> {
    public AnswerAdapter(Context context, int resource, List<Answer> objects) {
        super(context, resource, objects);
    }

    @Override
    /**
     * Provides a view for the AnswerAdapter
     * Will display name, time, and either a file link, picture, or a message
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.item_answer, parent, false);
        }

        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.qaPhotoImageView);
        TextView answerTextView = (TextView) convertView.findViewById(R.id.answerTextView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.qaUserTextView);
        TextView fileLinkTextView = (TextView) convertView.findViewById(R.id.qaFileLinkTextView);
        fileLinkTextView.setPaintFlags(fileLinkTextView.getPaintFlags() |
                Paint.UNDERLINE_TEXT_FLAG);

        TextView timeTextView = (TextView) convertView.findViewById(R.id.qaTimeTextView);

        final Answer answer = getItem(position);


        boolean isPhoto = answer.getPhotoUrl() != null;
        boolean isFileLink = answer.getFileUrl() != null;

        //Display changes based on whether answer contains picture, file link or just text
        if (isPhoto) {
            answerTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            fileLinkTextView.setVisibility(View.GONE);
            Glide.with(photoImageView.getContext())
                    .load(answer.getPhotoUrl())
                    .into(photoImageView);
        } else if (isFileLink) {
            answerTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.GONE);
            fileLinkTextView.setVisibility(View.VISIBLE);
            fileLinkTextView.setText(answer.getFileName());
            fileLinkTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(answer.getFileUrl()),
                                "application/*");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(),
                                "You must install the necessary app to view this file",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            answerTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            fileLinkTextView.setVisibility(View.GONE);
            answerTextView.setText(answer.getExplanation());
        }
        timeTextView.setText(answer.getTime().toString());
        authorTextView.setText(answer.getUser());

        return convertView;
    }
}
