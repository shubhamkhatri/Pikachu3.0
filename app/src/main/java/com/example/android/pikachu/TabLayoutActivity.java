package com.example.android.pikachu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

public class TabLayoutActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    private FirebaseAuth firebaseAuth;
    private LoginPreferences loginPreferences;
    private SkipPreferences skipPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);

        loginPreferences = new LoginPreferences(this);
        skipPreferences = new SkipPreferences(this);
        firebaseAuth = FirebaseAuth.getInstance();
        String folder_main = "PikachuDocument";

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        CategoryAdapter adapter = new CategoryAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(2);
    }

    public class CategoryAdapter extends FragmentPagerAdapter {

        private static final int POSITION_OCR = 0;
        private static final int POSITION_DONAR = 1;
        private static final int POSITION_MEDICINE = 2;
        private static final int POSITION_LOCATER= 3;
        private static final int NUMBER_OF_POSITIONS = 4;

        private Context mContext;

        public CategoryAdapter(Context context, FragmentManager fragmentManager) {
            super(fragmentManager);
            mContext = context;
        }


        @Override
        public Fragment getItem(int position) {
            if (POSITION_OCR == position) {
                return new OcrFragment();
            } else if (POSITION_MEDICINE == position) {
                return new TimerFragment();
            } else if(POSITION_DONAR==position) {
                return new DonarFragment();
            } else {
            return new BmiFragment();
             }
        }

        @Override
        public int getCount() {
            return NUMBER_OF_POSITIONS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (POSITION_OCR == position) {
                return "OCR";
            } else if (POSITION_MEDICINE == position) {
                return "MEDICINE";
            } else if(POSITION_DONAR==position) {
                return "DONAR";
             } else {
                  return "BMI";
             }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.account:
                Toast.makeText(TabLayoutActivity.this, "Account", Toast.LENGTH_SHORT).show();
                break;
            case R.id.feedback:
                startActivity(new Intent(TabLayoutActivity.this, FeedbackActivity.class));
                break;
            case R.id.logout:
                if (loginPreferences.DonarLaunch() == 1) {
                    firebaseAuth.signOut();
                    loginPreferences.setLaunch(0);
                    finish();
                    startActivity(new Intent(TabLayoutActivity.this, LoginActivity.class));
                } else {
                    skipPreferences.setLaunch(0);
                    startActivity(new Intent(TabLayoutActivity.this, LoginActivity.class));
                    finish();
                }
                break;
        }
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.logout);
        if (loginPreferences.DonarLaunch() == 1) {
            item.setTitle("Logout");
        } else
            item.setTitle("Login");
        return super.onPrepareOptionsMenu(menu);
    }

}
