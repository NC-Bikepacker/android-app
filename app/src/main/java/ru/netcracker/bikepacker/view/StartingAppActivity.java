package ru.netcracker.bikepacker.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ru.netcracker.bikepacker.R;
import ru.netcracker.bikepacker.databinding.ActivityMainBinding;
import ru.netcracker.bikepacker.manager.SessionManager;

public class StartingAppActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static int TIME_OUT = 1500;
    private SessionManager sessionManager;
    private Context context;
    private NavController navController;
    private FragmentManager fragmentManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        fragmentManager = this.getSupportFragmentManager();

        context = getApplicationContext();
        sessionManager = SessionManager.getInstance(context);

        if (sessionManager.isEmpty()) {
            navController.navigate(R.id.action_fragmentLoadingScreen_to_logInFragment);
        }
        else if(false){
            navController.navigate(R.id.confirmEmailFragment);
        }
        else {
            Toast.makeText(this.getApplicationContext(),"dsgjkytyj", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(StartingAppActivity.this, MainNavigationActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}