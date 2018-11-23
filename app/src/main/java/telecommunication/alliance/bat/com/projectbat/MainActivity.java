package telecommunication.alliance.bat.com.projectbat;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "MAIN";
    private FeedFragment feedFragment;
    private InboxFragment inboxFragment;
    private ProfileFragment profileFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener
            =  new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_feed:
                    fragmentTransaction.hide(inboxFragment);
                    fragmentTransaction.hide(profileFragment);
                    fragmentTransaction.show(feedFragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_inbox:
                    fragmentTransaction.hide(feedFragment);
                    fragmentTransaction.hide(profileFragment);
                    fragmentTransaction.show(inboxFragment);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_profile:
                    fragmentTransaction.hide(feedFragment);
                    fragmentTransaction.hide(inboxFragment);
                    fragmentTransaction.show(profileFragment);
                    fragmentTransaction.commit();
                    return true;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        feedFragment = new FeedFragment();
        inboxFragment = new InboxFragment();
        profileFragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, feedFragment)
                .add(R.id.fragment_container, inboxFragment)
                .add(R.id.fragment_container, profileFragment)
                .hide(profileFragment)
                .show(feedFragment).commit();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(navListener);
    }
}
