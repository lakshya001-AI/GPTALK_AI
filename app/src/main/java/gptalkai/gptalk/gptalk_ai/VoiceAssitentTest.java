package gptalkai.gptalk.gptalk_ai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gptalk_ai.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VoiceAssitentTest extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcometextview;
    private ImageButton voiceButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;

    private SpeechRecognizer speechRecognizer;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();
    OkHttpClient client1 = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_assitent_test);
        getSupportActionBar().setTitle("");

        messageList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        welcometextview = findViewById(R.id.welcome_text);
        voiceButton = findViewById(R.id.roundImageButton);

        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
                speechRecognizer.setRecognitionListener(new RecognitionListener() {
                    @Override
                    public void onReadyForSpeech(Bundle params) {

                    }

                    @Override
                    public void onBeginningOfSpeech() {

                    }

                    @Override
                    public void onRmsChanged(float rmsdB) {

                    }

                    @Override
                    public void onBufferReceived(byte[] buffer) {

                    }

                    @Override
                    public void onEndOfSpeech() {

                    }

                    @Override
                    public void onError(int error) {

                    }

                    @Override
                    public void onResults(Bundle results) {
                        ArrayList<String> voiceResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        if (voiceResults != null){
                            String recognizedText = voiceResults.get(0);
                            addToChat(recognizedText, Message.Sent_By_bot);
                            sendMessage(recognizedText);
                            welcometextview.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onPartialResults(Bundle partialResults) {

                    }

                    @Override
                    public void onEvent(int eventType, Bundle params) {

                    }
                });
                welcometextview.setVisibility(View.GONE);
            }
        });

    }
    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");
        try {
            startActivityForResult(intent,1);
        } catch (ActivityNotFoundException e) {
            // Handle exception if no speech recognition service is present
            addResponse("Error in response: " + e.getLocalizedMessage());
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            ArrayList<String> voiceResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (voiceResults != null) {
                String recognizedText = voiceResults.get(0).toString();// Get the first result
                addToChat(recognizedText, Message.Sent_By_Me);
                sendMessage(recognizedText);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
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