package gptalkai.gptalk.gptalk_ai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gptalk_ai.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ktx.Firebase;

import java.util.Calendar;

public class Mainpage extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextView messageTextview;

    Button ChatbotButton, VoiceButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setIcon(getDrawable(R.drawable.chatbotimage1));
            alertDialogBuilder.setTitle("Logout App");
            alertDialogBuilder.setMessage("Are You Sure You Want Logout ?  ");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), getstarted_page.class);
                    startActivity(intent);
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        mAuth = FirebaseAuth.getInstance();
        messageTextview = findViewById(R.id.wishtext);
        ChatbotButton = findViewById(R.id.chatbotbutton);
        VoiceButton = findViewById(R.id.voicebutton);

        getSupportActionBar().setTitle("GPTALK_AI");

        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        String greetingMessage;
        if (hourOfDay >= 0 && hourOfDay < 12) {
            greetingMessage = "Good Morning,";
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            greetingMessage = "Good Afternoon,";
        } else {
            greetingMessage = "Good Evening,";
        }
        messageTextview.setText(greetingMessage);


        ChatbotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mainpage.this , Brainshop_Api.class);
                startActivity(intent);
            }
        });

        VoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}

