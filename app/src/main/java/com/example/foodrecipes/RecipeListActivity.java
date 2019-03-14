package com.example.foodrecipes;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;

import com.example.foodrecipes.adapters.OnRecipeListener;
import com.example.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.example.foodrecipes.util.Testing;
import com.example.foodrecipes.util.VerticalSpacingItemDecorator;
import com.example.foodrecipes.viewmodels.RecipeListViewModel;


public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";

    private RecipeListViewModel mRecipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recipe_list);

        mRecipeListViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);

        initRecyclerView();
        subscribeObservers();
        initSearchView();
        if (!mRecipeListViewModel.isViewingRecipes()) {
            displaySearchCategories();
        }
    }

    private void displaySearchCategories() {
        mRecipeListViewModel.setIsViewingRecipes(false);
        mAdapter.displaySearchCategories();
    }

    private void subscribeObservers() {

        mRecipeListViewModel.getRecipes().observe(this, recipes -> {
            if (recipes != null) {
                if (mRecipeListViewModel.isViewingRecipes()) {
                    Testing.printRecipes(recipes, TAG);
                    mAdapter.setRecipes(recipes);
                }
            }
        });
    }

//    private void testRetrofitRequest(){
//        mRecipeListViewModel.searchRecipeApi("chicken", 1);
//    }

    private void initRecyclerView() {
        mAdapter = new RecipeRecyclerAdapter(this);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initSearchView() {
        final SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mAdapter.displayLoading();

                // Search the database for a recipe
                mRecipeListViewModel.searchRecipeApi(query, 1);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                // Wait for the user to submit the search. So do nothing here.

                return false;
            }
        });
    }

    @Override
    public void onRecipeClick(int position) {
        Log.d(TAG, "onRecipeClick: clicked. " + position);
    }

    @Override
    public void onCategoryClick(String category) {
        mAdapter.displayLoading();

        // Search the database for a recipe
        mRecipeListViewModel.searchRecipeApi(category, 1);
    }

    @Override
    public void onBackPressed() {
        if (mRecipeListViewModel.onBackPressed()) {
            super.onBackPressed();
        } else {
            displaySearchCategories();
        }
    }
}
