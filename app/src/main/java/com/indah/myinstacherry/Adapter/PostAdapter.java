package com.indah.myinstacherry.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.indah.myinstacherry.CommentsActivity;
import com.indah.myinstacherry.Fragment.PostDetailFragment;
import com.indah.myinstacherry.Fragment.ProfileFragment;
import com.indah.myinstacherry.Model.Post;
import com.indah.myinstacherry.Model.user;
import com.indah.myinstacherry.R;

import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(i);

        Glide.with(mContext).load(post.getPostimage()).into(viewHolder.post_image);

        if (post.getDescription().equals("")){
            viewHolder.description.setVisibility(View.GONE);
        }
        else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(post.getDescription());
        }

        publisherInfo(viewHolder.image_profile, viewHolder.username, viewHolder.publisher, post.getPublisher());
        isLiked(post.getPostid(), viewHolder.like);
        nrLikes(viewHolder.likes, post.getPostid());
        getComments(post.getPostid(), viewHolder.comments);
        isSaved(post.getPostid(), viewHolder.save);

        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profielid", post.getPublisher());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.bar,
                        new ProfileFragment()).commit();
            }
        });

        viewHolder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profielid", post.getPublisher());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.bar,
                        new ProfileFragment()).commit();
            }
        });

        viewHolder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.bar,
                        new PostDetailFragment()).commit();
            }
        });

        viewHolder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();
                }
            }
        });

        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });

        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_image, like, comment, save;
        public TextView username, likes, publisher, description, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            username = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
        }
    }

    private void getComments(String postid, final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View All" +dataSnapshot.getChildrenCount()+ "Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isLiked(String postid, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("Liked");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrLikes(final TextView likes, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+ "likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher,final String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user userr = dataSnapshot.getValue(user.class);
                Glide.with(mContext).load(userr.getImageurl()).into(image_profile);
                username.setText(userr.getUsername());
                publisher.setText(userr.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isSaved(final String postid, final ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");
                }
                else{
                    imageView.setImageResource(R.drawable.ic_savee_black);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
