package telecommunication.alliance.bat.com.projectbat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class RoomListAdaptor extends ArrayAdapter<Person> {
    private Context context;
    private int res;
    public RoomListAdaptor(Context context, int resource, ArrayList<Person> list) {
        super(context, resource, list);
        this.context = context;
        this.res = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        String status = getItem(position).getStatus();


        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(res,parent,false);
        TextView nameView = (TextView)convertView.findViewById(R.id.nameLabel);
        TextView statusView = (TextView) convertView.findViewById(R.id.statusLabel);
        nameView.setText(name);
        statusView.setText(status);

        return convertView;

    }
}
