package com.indah.myinstacherry.StartActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.indah.myinstacherry.Fragment.HomeFragment;
import com.indah.myinstacherry.Fragment.LikeFragment;
import com.indah.myinstacherry.Fragment.NotificationFragment;
import com.indah.myinstacherry.Fragment.ProfileFragment;
import com.indah.myinstacherry.Fragment.SearchFragment;
import com.indah.myinstacherry.R;

public class Main2Activity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        getFragmentPage(new HomeFragment());
        bottomNavbar();

        Bundle intent = getIntent().getExtras();
        if (intent != null){
            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profielid", publisher);
            editor.apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.rv_container,
                    new ProfileFragment()).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.rv_container,
                    new HomeFragment()).commit();
        }
    }

    private void bottomNavbar() {
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;

                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.nav_add :
                        fragment = new NotificationFragment();
                        break;
                    case R.id.nav_profile :
                        SharedPreferences.Editor editor = getSharedPreferences("PREFES", MODE_PRIVATE).edit();
                        editor.putString("profileId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        editor.apply();
                        fragment = new ProfileFragment();
                        break;
                    case R.id.nav_heart :
                        fragment = new LikeFragment();
                        break;
                    case R.id.nav_search :
                        fragment = new SearchFragment();
                    default:
                        break;
                }
            }
        });
    }
    private boolean getFragmentPage(Fragment fragment){
        if (fragment!=null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.rv_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }
}