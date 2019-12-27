package com.cybavo.example.auth;

import android.os.Bundle;
import android.view.View;

import com.cybavo.example.auth.push.PushNotificationService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {

    NavController mNavController;
    BottomNavigationView mBottomNavigationView;
    Toolbar mToolbar;
    AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mBottomNavigationView = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(mBottomNavigationView, mNavController);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mAppBarConfiguration = new AppBarConfiguration
                .Builder(R.id.fragment_actions, R.id.fragment_settings) // top-level fragments, no back arrow
                .build();
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);

        mNavController.addOnDestinationChangedListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        // only show bottom-nav on top-level fragments
        mBottomNavigationView.setVisibility(
                destination.getId() == R.id.fragment_actions || destination.getId() == R.id.fragment_settings ? View.VISIBLE : View.GONE
        );
    }
}
