package com.indah.myinstacherry.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.indah.myinstacherry.Fragment.ProfileFragment;
import com.indah.myinstacherry.Model.user;
import com.indah.myinstacherry.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapter extends RecyclerView.Adapter<userAdapter.ViewHolder>{

    private Context mContext;
    private List<user> mUser;

    private FirebaseUser firebaseUser;

    public userAdapter(Context mContext, List<user> mUser) {
        this.mContext = mContext;
        this.mUser = mUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        return new userAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final user userr = mUser.get(i);

        viewHolder.btn_follow.setVisibility(View.VISIBLE);

        viewHolder.username2.setText(userr.getUsername());
        viewHolder.fullname2.setText(userr.getFullname());
        Glide.with(mContext).load(userr.getImageurl()).into(viewHolder.image_profile);
        isFollowing(userr.getId(), viewHolder.btn_follow);

        if (userr.getId().equals(firebaseUser.getUid())){
            viewHolder.btn_follow.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", userr.getId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.rv_container,
                        new ProfileFragment()).commit();
            }
        });

        viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.btn_follow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(userr.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userr.getId())
                            .child("Followers").child(firebaseUser.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(userr.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userr.getId())
                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username2;
        public TextView fullname2;
        public CircleImageView image_profile;
        public Button btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username2 = itemView.findViewById(R.id.username2);
            fullname2 = itemView.findViewById(R.id.fullname2);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(final String userId, final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userId).exists()){
                    button.setText("Following");
                }
                else{
                    button.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
