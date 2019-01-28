package com.algol.project.algolsfa.async;

import android.content.Context;
import android.os.AsyncTask;

import com.algol.project.algolsfa.interfaces.APIInvocationListener;
import com.algol.project.algolsfa.others.Constants;
import com.algol.project.algolsfa.pojos.APIResponse;

/**
 * Created by Lykos on 27-Jan-19.
 */

public class AsyncAPIClient extends AsyncTask<String, Void, APIResponse> {
    private Context context;
    private APIInvocationListener apiInvocationListener;

    public AsyncAPIClient(Context context, APIInvocationListener apiInvocationListener) {
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
        if(apiResponse.getHttpStatus() == Constants.API_SUCCESS) {
            apiInvocationListener.onResponseSuccess(apiResponse.getApi(),apiResponse.getResponse());
        }
        else {
            apiInvocationListener.onResponseFailure(apiResponse.getHttpStatus());
        }
    }
}
