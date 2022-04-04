package github.acodervic.mod.net.http;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyCallback implements Callback {
    int a= 12;
    @Override
    public void onFailure(Call arg0, IOException arg1) {

    }

    @Override
    public void onResponse(Call arg0, Response arg1) throws IOException {

    }

}