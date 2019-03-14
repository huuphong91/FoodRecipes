package com.example.foodrecipes.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.repositories.RecipeRepository;

import java.util.List;

public class RecipeListViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository;
    private boolean mIsViewingRecipes;

    public RecipeListViewModel() {
        mRecipeRepository = RecipeRepository.getInstance();
    }


    public LiveData<List<Recipe>> getRecipes() {
        return mRecipeRepository.getRecipes();
    }

    public void searchRecipeApi(String query, int pageNumber) {
        mIsViewingRecipes = true;
        mRecipeRepository.searchRecipeApi(query, pageNumber);
    }

    public boolean isViewingRecipes() {
        return mIsViewingRecipes;
    }

    public void setIsViewingRecipes(boolean isViewingRecipes) {
        mIsViewingRecipes = isViewingRecipes;
    }

    public boolean onBackPressed() {
        if (mIsViewingRecipes) {
            mIsViewingRecipes = false;
            return false;
        }
        return true;

    }
}