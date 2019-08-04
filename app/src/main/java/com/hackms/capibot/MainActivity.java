package com.hackms.capibot;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class MainActivity extends AppCompatActivity implements AIListener {

    private static final String CLIENT_ACCESS_TOKEN = "c70a1c8319ec4c808b54c28264608682";
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 10000;

    private EditText consultaEdText;
    private TextView resultadoTxtView;

    private AIService aiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        consultaEdText = findViewById(R.id.consulta_edText);

        resultadoTxtView = findViewById(R.id.resultado_text);
        resultadoTxtView.setMovementMethod(new ScrollingMovementMethod());

        final AIConfiguration config = new AIConfiguration(CLIENT_ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.PortugueseBrazil,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);

        aiService.setListener(this);
    }

    public void consultar(View view) {

        if (consultaEdText.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Digite algo", Toast.LENGTH_SHORT).show();
            return;
        }

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(consultaEdText.getText().toString());

        new AsyncTask<AIRequest, Void, AIResponse>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                consultaEdText.setText("");
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

    @Override
    public void onResult(AIResponse result) {
        if (result != null && !result.isError()) {
            resultadoTxtView.setText(result.getResult().getFulfillment().getSpeech());
        }
    }

    @Override
    public void onError(AIError error) {
        resultadoTxtView.setText("Ocorreu um erro: " + error.getMessage());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        resultadoTxtView.setText("Escutando...");
    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    public void consultarVoz(View view) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                AlertDialog.Builder janela = new AlertDialog.Builder(this);
                janela.setTitle("Permissão necessária");
                janela.setMessage("Precisamos do microfone para que você possa sse coomunicar via voz");
                janela.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                    }
                });
                janela.show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            aiService.startListening();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                aiService.startListening();
            }
        }
    }
}

