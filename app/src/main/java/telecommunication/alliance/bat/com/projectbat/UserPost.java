package telecommunication.alliance.bat.com.projectbat;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.List;


public class UserPost extends RecyclerView.Adapter<UserPost.ProductViewHolder> {
    private Context mCtx;

    private List<Post> postList;

    public UserPost(Context mCtx, List<Post> postList) {
        this.mCtx = mCtx;
        this.postList = postList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.post_card_view, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        Post post = postList.get(position);

        //binding the data with the viewholder views
        holder.textViewTitle.setText(post.getTitle());
        holder.textViewStatus.setText(post.getState());
        holder.textViewLocation.setText(post.getLocation());
        Picasso.get().load(post.getUri()).fit().into(holder.imageView);
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewStatus, textViewLocation;
        ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.postTitle);
            textViewStatus = itemView.findViewById(R.id.postStatus);
            textViewLocation = itemView.findViewById(R.id.postLocation);
            imageView = itemView.findViewById(R.id.postImage);
        }
    }
}
