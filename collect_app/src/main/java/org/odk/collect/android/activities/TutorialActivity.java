package org.odk.collect.android.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;



import org.odk.collect.android.R;

import java.util.ArrayList;

import timber.log.Timber;

public class TutorialActivity extends CollectAbstractActivity {

    private static final String TAG = "TutorialActivity";
    private ArrayAdapter adapter;

    private WebView webView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_activity);

      /*  Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getString(R.string.tuto));
        setSupportActionBar(toolbar);*/

        ListView list = findViewById(R.id.tutorial);


        Timber.d("onCreate: Started");

        //Add the Tutorial objects (questions) to an ArrayList
        final ArrayList<String> question = new ArrayList<>();
        question.add(getString(R.string.question1));
        question.add(getString(R.string.question2));
        question.add(getString(R.string.question3));
      //  question.add(getString(R.string.question4));
       // question.add(getString(R.string.question5));
     //   question.add(getString(R.string.question6));
       // question.add(getString(R.string.question7));



        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, question);//this table has a standard android layout
        list.setAdapter(adapter);



        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: question: " + question.get(position));

                if (position + 1 == 1) {

                    setContentView(R.layout.activity_web_view);
                    webView = (WebView) findViewById(R.id.webView);
                    WebSettings webSettings = webView.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    webView.loadUrl("https://hedera.online");


                }
                if (position + 1 == 2) {
                  //  Intent intent = new Intent(TutorialActivity.this, activity_imagen.class);
                  //  startActivity(intent);


                }
                if (position + 1 == 3) {
                    ///super.onCreate(savedInstanceState);
                   // Intent intent = new Intent(TutorialActivity.this, question1.class);
              //      startActivity(intent);

                }

              /*  if (position+1==4)
                {

                    //Toast.makeText(ii_esQuestions.this, "Has seleccionado: " + question.get(position), Toast.LENGTH_SHORT).show();
                }
                if (position + 1 == 5) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:+57 310 4163099"));
                    startActivity(intent);
                }
                if (position + 1 == 6) {

                }

                if (position + 1 == 7) {

                }*/

            }


        });
    }


}
