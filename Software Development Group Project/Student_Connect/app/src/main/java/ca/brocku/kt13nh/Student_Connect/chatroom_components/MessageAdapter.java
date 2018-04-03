package ca.brocku.kt13nh.Student_Connect.chatroom_components;

/**
 * Created by Goal Diggers
 * MessageAdapter that conatins messages to be displayed in chatroom along with the views for
 * these messages
 */
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

public class MessageAdapter extends ArrayAdapter<Message> {
    public MessageAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
    }

    @Override
    /**
     * Provides the view for the adapter. Will display name, time, and a file link, picture or
     * a message
     * Clicking ont he file link will allow user to download the file
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.item_message, parent, false);
        }

        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.chatPhotoImageView);
        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.chatUserTextView);
        TextView fileLinkTextView = (TextView) convertView.findViewById(R.id.chatFileLinkTextView);
        fileLinkTextView.setPaintFlags(fileLinkTextView.getPaintFlags() |
                Paint.UNDERLINE_TEXT_FLAG);
        TextView timeTextView = (TextView) convertView.findViewById(R.id.chatTimeTextView);

        final Message message = getItem(position);
        boolean isPhoto = message.getPhotoUrl() != null;
        boolean isFileLink = message.getFileUrl() != null;

        //Display changes based on whether message contains picture, file link or just text
        if (isPhoto) {
            messageTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);
            fileLinkTextView.setVisibility(View.GONE);
            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(photoImageView);
        } else if (isFileLink) {
            messageTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.GONE);
            fileLinkTextView.setVisibility(View.VISIBLE);
            //final String fileUrl = message.getFileUrl();
            fileLinkTextView.setText(message.getFileName());
            fileLinkTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(message.getFileUrl()),
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
            messageTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            fileLinkTextView.setVisibility(View.GONE);
            messageTextView.setText(message.getText());
        }
        timeTextView.setText(message.getTime().toString());
        authorTextView.setText(message.getName());

        return convertView;
    }
}
