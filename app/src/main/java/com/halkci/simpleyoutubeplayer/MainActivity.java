package com.halkci.simpleyoutubeplayer;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public final static String SEARCH_KEYWORD = "com.halkci.simpleyoutubeplayer";
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("==============onCreate called================");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView)MenuItemCompat.getActionView(menuItem);
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(false);

        searchView.setQuery("",true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
                intent.putExtra(SEARCH_KEYWORD,query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}
