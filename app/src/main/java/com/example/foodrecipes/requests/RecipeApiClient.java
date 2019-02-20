package com.example.foodrecipes.requests;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.foodrecipes.AppExecutors;
import com.example.foodrecipes.models.Recipe;
import com.example.foodrecipes.requests.responses.RecipeSearchResponse;
import com.example.foodrecipes.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class RecipeApiClient {

    private static final String TAG = "RecipeApiClient";

    private static RecipeApiClient sInstance;
    private MutableLiveData<List<Recipe>> mRecipes;
    private RetrieveRecipeApi retrieveRecipeApi;

    public static RecipeApiClient getInstance() {
        if (sInstance == null) {
            sInstance = new RecipeApiClient();
        }
        return sInstance;
    }

    private RecipeApiClient() {
        mRecipes = new MutableLiveData<>();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipes;
    }

    public void searchRecipesApi(String query, int pageNumber) {
        if (retrieveRecipeApi != null) {
            retrieveRecipeApi = null;
        }
        retrieveRecipeApi = new RetrieveRecipeApi(query, pageNumber);

        final Future handler = AppExecutors.getInstance().networkIO().submit(retrieveRecipeApi);

        AppExecutors.getInstance().networkIO().schedule(() -> {
            //let the user know its timed out
            handler.cancel(true);
        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    private class RetrieveRecipeApi implements Runnable {

        private String query;
        private int pageNumber;
        boolean cancelRequest;

        public RetrieveRecipeApi(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
            cancelRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getRecipes(query, pageNumber).execute();
                if (cancelRequest) {
                    return;
                }
                if (response.code() == 200) {
                    List<Recipe> list = new ArrayList<>(((RecipeSearchResponse) response.body()).getRecipes());
                    if (pageNumber == 1) {
                        mRecipes.postValue(list);
                    } else {
                        List<Recipe> currentRecipes = mRecipes.getValue();
                        currentRecipes.addAll(currentRecipes);
                        mRecipes.postValue(currentRecipes);
                    }
                } else {
                    String error = response.errorBody().string();
                    Log.e(TAG, "run: " + error);
                    mRecipes.postValue(null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mRecipes.postValue(null);
            }
        }

        private Call<RecipeSearchResponse> getRecipes(String query, int pageNumber) {
            return ServiceGenerator.getRecipeApi().searchRecipe(Constants.API_KEY, query, String.valueOf(pageNumber));
        }

        private void cancelRequest() {
            Log.d(TAG, "cancelRequest: Canceling the search request.");
            cancelRequest = true;
        }
    }
}
