/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package edu.cmu.pocketsphinx.demo;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class MainActivity extends Activity implements
        RecognitionListener {

    private static final String TAG = "anxii";
    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String FORECAST_SEARCH = "forecast";
    private static final String DIGITS_SEARCH = "digits";
    private static final String PHONE_SEARCH = "phones";
    private static final String MENU_SEARCH = "menu";
    
    /* Keyword we are looking for to activate menu */
    //private static final String KEYPHRASE = "oh mighty computer";
    private static final String KEYPHRASE = "oh mighty computer one two three four five six seven eight elephant foot ball";

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;
    final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        // Prepare the data for UI
        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(DIGITS_SEARCH, R.string.digits_caption);
        captions.put(PHONE_SEARCH, R.string.phone_caption);
        captions.put(FORECAST_SEARCH, R.string.forecast_caption);
        setContentView(R.layout.main);
        ((TextView) findViewById(R.id.caption_text))
                .setText("Preparing the recognizer");

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Date date = new Date();
                    Log.d(TAG,"doInBackground time : "+sdf.format(date));
                    Assets assets = new Assets(MainActivity.this);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                Date date = new Date();
                Log.d(TAG,"onPostExecute time : "+sdf.format(date));
                if (result != null) {
                    ((TextView) findViewById(R.id.caption_text))
                            .setText("Failed to init recognizer " + result);
                } else {
                    recognizer.stop();
                    Log.e(TAG,"start record time is :"+sdf.format(new Date()));
                    recognizer.startListening(KWS_SEARCH,30000);
                    makeText("可以开始说了...");
                }
            }
        }.execute();
    }


    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = defaultSetup()
                //.setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setAcousticModel(new File(assetsDir, "zh"))
                //.setDictionary(new File(assetsDir, "cmudict-en-us.dict"))
                .setDictionary(new File(assetsDir, "my.dict"))

                // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                //.setRawLogDir(assetsDir)

                // Threshold to tune for keyphrase to balance between false alarms and misses
                .setKeywordThreshold(1e-6f)

                // Use context-independent phonetic search, context-dependent is too slow for mobile
                .setBoolean("-allphone_ci", true)

                .getRecognizer();
        recognizer.addListener(this);

        /** In your application you might not need to add all those searches.
         * They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        //recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        recognizer.addKeywordSearch(KWS_SEARCH,new File(assetsDir,"book2.txt"));

        // Create grammar-based search for selection between demos
/*        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);*/

        // Create grammar-based search for digit recognition
        //File digitsGrammar = new File(assetsDir, "digits.gram");
        //recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);

        // Create language model search
        //File languageModel = new File(assetsDir, "weather.dmp");
        //recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);

        // Phonetic search
        //File phoneticModel = new File(assetsDir, "en-phone.dmp");
        //recognizer.addAllphoneSearch(PHONE_SEARCH, phoneticModel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recognizer.cancel();
        recognizer.shutdown();
    }
    
    /**
     * In partial result we get quick updates about current hypothesis. In
     * keyword spotting mode we can react here, in other modes we need to wait
     * for final result in onResult.
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
    	    return;



        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE)){

        }

        ((TextView) findViewById(R.id.result_text)).setText(text);
        //makeText("onPartialResult : "+text);
        //Log.d(TAG,"onPartialResult : "+text);
    }

    Toast toast;
    public void makeText(String content){
        if(toast!= null)toast.cancel();
        toast = Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) findViewById(R.id.result_text)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText("onResult "+text);
            Log.d(TAG,"onResult : "+text);
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        //makeText("onBeginningOfSpeech");
        Log.e(TAG,"onBeginningOfSpeech");
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        //makeText("onEndOfSpeech");
        Log.e(TAG,"onEndOfSpeech");
    }


    @Override
    public void onError(Exception error) {
        makeText("onError");
    }

    @Override
    public void onTimeout() {
        makeText("onTimeOut");
        Log.e(TAG,"onTimeOut");
        if(recognizer!= null){
            recognizer.stop();
            recognizer.shutdown();
            Log.e(TAG,"stop record time is :"+sdf.format(new Date()));
        }
    }
}
