package com.algol.project.algolsfa.async;

import android.content.Context;
import android.os.AsyncTask;

import com.algol.project.algolsfa.interfaces.APIInvocationListener;
import com.algol.project.algolsfa.pojos.APIResponse;

/**
 * Created by Lykos on 27-Jan-19.
 */

public class AsyncAPICaller extends AsyncTask<String, Void, APIResponse> {
    private Context context;
    private APIInvocationListener apiInvocationListener;

    public AsyncAPICaller(Context context, APIInvocationListener apiInvocationListener) {
        this.context = context;
        this.apiInvocationListener = apiInvocationListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected APIResponse doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(APIResponse apiResponse) {
        super.onPostExecute(apiResponse);
    }
}
