package myapp.training.sriram.com.firstapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.ElasticSearchResultText);
        editText = findViewById(R.id.editText);
    }

    public void submitClicked(View view) {
        String END_POINT = "https://search-sri-ram-app-6pq5opizwzbk7ixbjl5u3zpu6i.us-east-2.es.amazonaws.com/book/_doc/_search";
        try {
            String queryParam = editText.getText().toString();
            //Wildcard matching
            queryParam = "*"+queryParam+"*";

            //Query Object

            /*
             * Sample Query Object:
             *  {"query":{
             *     "query_string":{"query":"William"}
             *   }
             * }
             */
            JsonObject jsonQueryTermObject = new JsonObject();
            jsonQueryTermObject.addProperty("query",queryParam);
            JsonObject jsonQueryStringObject = new JsonObject();
            jsonQueryStringObject.add("query_string",jsonQueryTermObject);
            final JsonObject jsonQueryObject = new JsonObject();
            jsonQueryObject.add("query",jsonQueryStringObject);


            Ion.with(this)
                    .load(END_POINT)
                    .setJsonObjectBody(jsonQueryObject)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            String data = "";
                            JsonArray jsonArray= ((JsonObject)result.get("hits")).get("hits").getAsJsonArray();
                            for(JsonElement jsonElement : jsonArray){
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                data = data.concat("Book Name is : "+jsonObject.getAsJsonObject("_source").get("name")+"\n"
                                        +"Author Name is: " + jsonObject.getAsJsonObject("_source").get("author")+"\n"
                                        +"Language Name is: " + jsonObject.getAsJsonObject("_source").get("language") +"\n"
                                        +"\n---------------------------------------------------\n");
                            }
                            data+= "**********End Of Results***********";
                            textView.setText(data);
                        }
                    });
        } catch(Exception e){
        }
    }
}
