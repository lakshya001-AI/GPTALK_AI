package gptalkai.gptalk.gptalk_ai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gptalk_ai.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class chatbotpage extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcometextview;
    EditText messageEdittext;
    ImageButton sendButton;
    List<Message> messageList; // this contains the list of messages that is being sent
    MessageAdapter messageAdapter;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();
    OkHttpClient client1 = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbotpage);
        getSupportActionBar().setTitle("");

        messageList = new ArrayList<>(); // message list assigned to the arraylist

        recyclerView = findViewById(R.id.recycler_view);
        welcometextview = findViewById(R.id.welcome_text);
        messageEdittext = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        //setup the recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = messageEdittext.getText().toString().trim(); // getting the requirred text from the edit text
                addToChat(question, Message.Sent_By_Me);
                messageEdittext.setText("");
                sendMessage(question);
                welcometextview.setVisibility(View.GONE);
            }
        });
    }
    void addToChat(String message, String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

    void addResponse(String reponse){
        messageList.remove(messageList.size()-1);
        addToChat(reponse, Message.Sent_By_bot);
    }

     //calling okhttp here
    void callAPI(String question){
        //okHttp
        messageList.add(new Message("Typing... ",Message.Sent_By_bot));

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model", "text-davinci-003");
            jsonBody.put("prompt", question);
            jsonBody.put("max_tokens",16);
            jsonBody.put("temperature",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization", "Bearer  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")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    JSONObject jsonObject = null;
                    try {
                        //make it a string
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getString("text");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    addResponse("Failed to load response due to " + response.body().string());
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());
            }
        });
    }

    void sendMessage(String question){
        messageList.add(new Message("Typing... ",Message.Sent_By_bot));
        String apiUrl = "http://api.brainshop.ai/get?bid=178162&key=4z4e8AY4qg4OJD4H&uid=[uid]&msg=" + question;
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client1.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        String chatbotResponse = jsonResponse.getString("cnt");
                        addResponse(chatbotResponse.trim());
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                else {
                    addResponse("Failed to load response due to " + response.body().string());
                }

            }
        });





    }
}