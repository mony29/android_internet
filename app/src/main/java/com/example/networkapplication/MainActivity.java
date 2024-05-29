package com.example.networkapplication;

import static com.android.volley.Request.Method.GET;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Button btnJson;
    Button btnDownload;
    TextView jsonTextView;
    ImageView imageView;
    private static final String imageUrl = "https://images.pexels.com/photos/459653/pexels-photo-459653.jpeg?auto=compress&cs=tinysrgb&w=600";
    private static final String apiUrl = "https://mesh.if.iqiyi.com/portal/videolib/pcw/data?version=1.0&ret_num=30&page_id=1&device_id=781a214eb3398301b866a5c83cd0bb5a&passport_id=&watch_list=4527925399108400,6735,0&recent_selected_tag=%E7%BB%BC%E5%90%88%3B%E5%96%9C%E5%89%A7%3B%E7%88%B1%E6%83%85%3B%E5%8A%A8%E7%94%BB%3B%E5%86%85%E5%9C%B0%3B%E4%B8%B9%E9%BA%A6&recent_search_query=&ip=202.108.14.240&scale=150&dfp=a0401a7bbdbe484de1adbe1730b68025f04f703bdf3190269167b927c4a5c025d4&channel_id=1&tagName=&mode=24";
    private static final int MSG_GET_JSON = 1;
    private RequestQueue queue; // Declare a RequestQueue for Volley

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            URL url;
            HttpURLConnection httpURLConnection;
            InputStream inputStream;
            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                inputStream = httpURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException exception) {
                exception.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return bitmap;
        }
    }

    public void downloading(View view) {

        DownloadImage downloadImage = new DownloadImage();
        imageView = findViewById(R.id.imageView);

        try {
            Bitmap bitmap = downloadImage.execute(imageUrl).get();
            imageView.setImageBitmap(bitmap);
        } catch (ExecutionException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    // Handler to handle messages on the main thread
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_GET_JSON) {
                String json = (String) msg.obj;
                jsonTextView.setText(json);
            }
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnJson = findViewById(R.id.btnJson);
        btnDownload = findViewById(R.id.btnDownload);
        jsonTextView = findViewById(R.id.jsonTextView);

        // Initialize the RequestQueue for Volley
        queue = Volley.newRequestQueue(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchApiData();
            }
        });
    }

    private void fetchApiData() {
        // Build the API request with Volley
        StringRequest request = new StringRequest(GET, apiUrl, response -> {
            jsonTextView.setText(response);
        }, error -> {
            jsonTextView.setText("Error fetching data.");
            error.printStackTrace();
        });
        queue.add(request);
    }
}
