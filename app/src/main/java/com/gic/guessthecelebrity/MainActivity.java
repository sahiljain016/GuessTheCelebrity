package com.gic.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
//GLOBAL VARIABLES AND INITIALIZATIONS
ArrayList<String> CelebUrls = new ArrayList<String>();
ArrayList<String> CelebNames = new ArrayList<String>();
int chosenCeleb =0;
String[] answers = new String[4];
int locationOfCorrectAnswer;
ImageView imageView;
Button button;
Button button2;
Button button3;
Button button4;
//CODE FOR ANSWER BUTTONS TO SEE IF RIGHT OR WRONG ANSWER HAS BEEN CHOOSEN
    public void ChosenCeleb(View view){

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)) ){
            Toast.makeText(this, "CONGRATS THIS IS THE RIGHT ANSWER!", Toast.LENGTH_SHORT).show();
        }
else{
            Toast.makeText(this, "Oh no! Wrong answer. This is "+ CelebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }
nextquestion();
    }
//CODE TO DOWNLOAD IMAGE FROM URL
    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream is = urlConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(is);
                return myBitmap;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }
    //CODE TO DOWNLOAD WEBSITES HTML
public class DownloadTask extends AsyncTask<String, Void , String>{
    @Override
    protected String doInBackground(String... urls) {
        String result="";
        URL url;
        HttpURLConnection URLconnection=null;
        try{
            url = new URL(urls[0]);
            URLconnection = (HttpURLConnection) url.openConnection();
            InputStream in = URLconnection.getInputStream();
            InputStreamReader reader= new InputStreamReader(in);
            int data=reader.read();
            while(data!=-1){

                char current = (char) data;
                result+=current;
             data= reader.read();

            }
            return result;
        }catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
// CODE TO GENERATE NEXT QUESTION AND SET IMAGE IN IMAGEVIEW AND ANSWERS IN BUTTONS

    public void nextquestion(){
try {
    Random rand = new Random();

    chosenCeleb = rand.nextInt(CelebUrls.size());

    ImageDownloader imageTask = new ImageDownloader();

    Bitmap celebImage = imageTask.execute(CelebUrls.get(chosenCeleb)).get();

    imageView.setImageBitmap(celebImage);

    locationOfCorrectAnswer = rand.nextInt(4);

    int locationOfIncorrectAnswer;
    for(int i=0; i < 4; i++)
    {
       if(i == locationOfCorrectAnswer){

           answers[i] = CelebNames.get(chosenCeleb);
       }
       else
           {
               locationOfIncorrectAnswer = rand.nextInt(CelebUrls.size());

               while(locationOfIncorrectAnswer == chosenCeleb){

               locationOfIncorrectAnswer = rand.nextInt(CelebUrls.size());

               }
 answers[i] = CelebNames.get(locationOfIncorrectAnswer);
       }
        button.setText(answers[0]);
        button2.setText(answers[1]);
        button3.setText(answers[2]);
        button4.setText(answers[3]);
    }


} catch (Exception e){

    e.printStackTrace();
}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
imageView=(ImageView) findViewById(R.id.imageView);
button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);

        DownloadTask task = new DownloadTask();
        String result= null;
        try{
            result = task.execute("https://www.bollywoodhungama.com/celebrities/top-100/").get();
            String[] splitString = result.split("<li class=\"nav-left slick-arrow\" style=\"display: inline-block;\"></li>");

            Pattern p= Pattern.compile("img src=\"(.*?)\" width");
            Matcher m= p.matcher(splitString[0]);

while(m.find()){
CelebUrls.add(m.group(1));
}
             Pattern p1 = Pattern.compile("title=\"(.*?)\" srcset");
             Matcher m1 = p1.matcher(splitString[0]);

while(m1.find()){
    CelebNames.add(m1.group(1));
}
nextquestion();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}