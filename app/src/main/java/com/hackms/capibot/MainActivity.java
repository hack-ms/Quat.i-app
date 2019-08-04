package com.hackms.capibot;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ACCESS_TOKEN = "c70a1c8319ec4c808b54c28264608682";

    private EditText consultaEdText;
    private TextView resultadoTxtView;

    private AIService aiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        consultaEdText = findViewById(R.id.consulta_edText);
        resultadoTxtView = findViewById(R.id.resultado_text);

        final AIConfiguration config = new AIConfiguration(CLIENT_ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.PortugueseBrazil,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
    }

    public void consultar(View view) {

        if (consultaEdText.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Digite algo", Toast.LENGTH_SHORT).show();
            return;
        }

        consultaEdText.setText("");

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(consultaEdText.getText().toString());

        new AsyncTask<AIRequest, Void, AIResponse>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                resultadoTxtView.setText("Consultado Dialogflow");
            }

            @Override
         protected AIResponse doInBackground(AIRequest... requests) {
             final AIRequest request = requests[0];
             try {
                 final AIResponse response = aiService.textRequest(aiRequest);
                 return response;
             } catch (AIServiceException e) {

             }
             return null;
         }
         @Override
            protected void onPostExecute(AIResponse aiResponse) {
             if (aiResponse == null) {
                 resultadoTxtView.setText("Ocorreu um erro!");
             } else {
                 resultadoTxtView.setText(aiResponse.getResult().getFulfillment().getSpeech());
             }
         }
        }.execute(aiRequest);

    }
}
