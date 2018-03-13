//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package android.volley.request;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public abstract class Listener<T> implements Response.Listener<T> {
    public Listener() {
    }

    public void onPreExecute() {
    }

    public void onFinish() {
    }

    public abstract void onSuccess(T var1);

    public void onError(VolleyError var1) {
    }

    public void onCancel() {
    }

    public void onNetworking() {
    }

    public void onUsedCache() {
    }

    public void onRetry() {
    }

    public void onProgressChange(long var1, long var3) {
    }

    @Override
    public void onResponse(T response) {
    }
}
