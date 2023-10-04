package gptalkai.gptalk.gptalk_ai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.gptalk_ai.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Brainshop_Api extends AppCompatActivity {
    EditText inputedittext;
    Button reponseButton;
    TextView reposeTextview;
    ScrollView scrollView;

    private OkHttpClient client = new OkHttpClient();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brainshop_api);

        inputedittext = findViewById(R.id.userEdittext);
        reponseButton = findViewById(R.id.getrreponseButton);
        reposeTextview = findViewById(R.id.reponsetextview);


        reponseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        String message = inputedittext.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }
        inputedittext.setText("");

        appendMessage("You: " + message);

        String apiUrl = "http://api.brainshop.ai/get?bid=178162&key=4z4e8AY4qg4OJD4H&uid=[uid]&msg=" + message;
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                appendMessage("Chatbot: Sorry, there was an error. Please try again later." + e.getMessage());
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        String chatbotResponse = jsonResponse.getString("cnt");
                        appendMessage("Chatbot: " + chatbotResponse);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                else {
                    appendMessage("Chatbot: Sorry, there was an error. Please try again later." + response.cacheResponse());
                }
            }
        });

    }

    private void appendMessage(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reposeTextview.append(message + "\n");
            }
        });
    }


}