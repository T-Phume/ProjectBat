package telecommunication.alliance.bat.com.projectbat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ProductViewHolder> {
    Context mCtx;

    private List<Friend> userList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public  FriendAdapter(Context mCtx, List<Friend> postList) {
        this.mCtx = mCtx;
        this.userList = postList;
    }

    @Override
    public FriendAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.feed_card, null);
        return new FriendAdapter.ProductViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(FriendAdapter.ProductViewHolder holder, int position) {
        //getting the product of the specified position
         Friend user = userList.get(position);
         holder.textViewName.setText(user.getName());
         Picasso.get().load(user.getUri()).fit().into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        ImageView imageView;
        Button button;

        public ProductViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            textViewName= itemView.findViewById(R.id.friend_name);
            imageView = itemView.findViewById(R.id.friend_profile_image);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
