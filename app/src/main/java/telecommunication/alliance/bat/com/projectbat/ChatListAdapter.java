package telecommunication.alliance.bat.com.projectbat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class ChatListAdapter extends ArrayAdapter<Messages> {
    private int res;
    private Context context;

    public ChatListAdapter(Context context, int resource, ArrayList<Messages> objects) {
        super(context, resource, objects);
        this.res = resource;
        this.context = context;
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String sender = getItem(position).getSender();
        String msg = getItem(position).getMsg();
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(res,parent,false);
        TextView msgLabel = (TextView)convertView.findViewById(R.id.messageLabel);
        TextView senderLabel = (TextView)convertView.findViewById(R.id.senderLabel);
        msgLabel.setText(msg);
        senderLabel.setText(sender);


        return convertView;

    }
}
