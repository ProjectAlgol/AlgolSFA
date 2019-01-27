package com.algol.project.algolsfa.interfaces;

/**
 * Created by Lykos on 27-Jan-19.
 */

public interface APIInvocationListener {
    void onResponseSuccess(String api, Object response);
    void onResponseFailure(int error);
}
