package com.jardenconsulting.chameleon;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class ChameleonActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ChameleonActivity";
    private static final String[] phones = {
            "07874619507", // John
            "07980354695", // Julie
            "07753744117", // Jackie
            "07739398461"  // Sam
    };
    private static final String[] tvShows = {
            "Friends", "Sex and the City", "Star Trek", "Sherlock",
            "You've been Framed", "Four in a Bed", "The Chase", "The Simpsons",
            "The Chase", "X-Factor", "Strictly Come Dancing", "Dad's Army",
            "News", "Match of the Day", "The Voice", "Loose Women"
    };
    private static final String[] toys = {
            "Lego", "Rocking Horse", "Scalextric", "Doll",
            "Rubik's Cube", "Etch a Sketch", "Teddy Bear", "Play Doh",
            "Yo-yo", "Frisbee", "Barbie", "Hula Hoop",
            "Spinning Top", "Dinosaur", "Construction Kit", "Jenga"
    };
    private static final String[] theArts = {
            "Painting", "Sculpture", "Architecture", "Dance",
            "Literature", "Opera", "Stand-up,", "Comic Books",
            "Illustration", "Music", "Theatre", "Cinema",
            "Video Games", "Graffiti", "Fashion", "Photography"
    };
    private static final String[] food = {
            "Pizza", "Chips", "Fish", "Cake",
            "Pasta", "Salad", "Soup", "Bread",
            "Eggs", "Cheese", "Fruit", "Chicken",
            "Sausage", "Ice Cream", "Chocolate", "Beef"
    };
    private static final String[] drinks = {
            "Coffee", "Tea", "Lemonade", "Coke",
            "Wine", "Beer", "Punch", "Prosecco",
            "Hot Chocolate", "Milkshake", "Water", "Milk",
            "Orange Juice", "Red Wine", "Gin and Tonic", "Ginger Beer"
    };
    private static final String[] inventions = {
            "Matches", "Gunpowder", "Wheel", "Printing",
            "Computer", "Internet", "Compass", "Plane",
            "TV", "Bread Maker", "Writing", "Steam Engine",
            "Car", "Telephone", "Camera", "Radio"
    };
    private static final String[] filmGenres = {
            "Horror", "Action", "Thriller", "Sci-Fi",
            "Rom-Com", "Western", "Comedy", "Historical",
            "Gangster", "Foreign Language", "War", "Documentary",
            "Musical", "Animation", "True Crime", "Sport"
    };
    private static final String[] fictionalCharacters = {
            "Indiana Jones", "Popeye", "Spiderman", "Darth Vader",
            "Sherlock Holmes", "Poirot", "Superman", "Batman",
            "James Bond", "Homer Simpson", "Frankenstein", "Robin Hood",
            "Super Mario", "Tarzan", "Hercules", "Iron Man"
    };
    private static final String[] jobs = {
            "Fireman", "Gardener", "Nurse", "Waiter",
            "Caretaker", "Secretary", "Accountant", "Teacher",
            "Lorry Driver", "Security Guard", "Chef", "Architect",
            "Police Officer", "Lawyer", "Carpenter", "Butcher"
    };
    private static final String[] musicals = {
            "West Side Story", "Cats", "Jersey Boys", "School of Rock",
            "Phantom of the Opera", "Les Miserables", "Oliver", "Chicago",
            "Sound of Music", "Annie", "Mamma Mia", "Oklahoma",
            "Wicked", "Cats", "Miss Saigon", "Carousel"
    };
    private static final String[] bands = {
            "The Beatles", "The Rolling Stones", "AC/DC", "Nirvana",
            "Fleetwood Mac", "Queen", "The Beach Boys", "Jackson 5",
            "The Eagles", "The Who", "Abba", "Led Zeppelin",
            "Pink Floyd", "The Band", "Cold Play", "Blondie"
    };
    private static final String[] schoolSubjects = {
            "Maths", "Chemistry", "Physics", "Biology",
            "History", "Philosopy", "Geography", "English",
            "Economics", "Spanish", "Art", "Music",
            "Gym", "Latin", "Religion", "Technology"
    };
    private class Topic {
        String name;
        String[] choices;
        Topic(String name, String[] choices) {
            this.name = name;
            this.choices = choices;
        }
    }
    // "Politicians"
    private Topic[] topics = {
            new Topic("TV Shows", tvShows),
            new Topic("Toys", toys),
            new Topic("The Arts", theArts),
            new Topic("Food", food),
            new Topic("Drinks", drinks),
            new Topic("Inventions", inventions),
            new Topic("Fictional Characters", fictionalCharacters),
            new Topic("Jobs", jobs),
            new Topic("Bands", bands),
            new Topic("Musicals", musicals),
            new Topic("School Subjects", schoolSubjects),
            new Topic("Film Genres", filmGenres)
    };
    private TextView textViewTopic;
    private Button buttonSend;
    private ListView listView;
    private ArrayAdapter<String> listAdapter;

    private SmsManager smsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chameleon);
        textViewTopic = findViewById(R.id.textTopic);
        buttonSend = findViewById(R.id.buttonSend);
        listView = findViewById(R.id.list);
        buttonSend.setOnClickListener(this);
        listAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(listAdapter);
        smsManager = SmsManager.getDefault();
    }

    @Override
    public void onClick(View view) {
        Random random = new Random();
        int topicId = random.nextInt(topics.length);
        Topic topic = topics[topicId];
        String topicName = topic.name;
        textViewTopic.setText(topicName);
        int wordId = random.nextInt(topic.choices.length);
        String word = topic.choices[wordId];
        int chameleonId = random.nextInt(phones.length);
        for (int i = 0; i < phones.length; i++) {
            String message = "Chameleon; the topic is " + topicName;
            if (i == chameleonId) {
                message += ", and you are the Chameleon!";
            } else {
                message += " and the word is " + word;
            }
            sendSMS(phones[i], message);
        }
        this.listAdapter.setNotifyOnChange(false);
        this.listAdapter.clear();
        for (String choice: topic.choices) {
            this.listAdapter.add(choice);
        }
        this.listAdapter.notifyDataSetChanged();

    }
    private void sendSMS(String phoneNo, String sms) {
        if (BuildConfig.DEBUG) Log.d(TAG, "sendSMS(" + phoneNo +
                ", " + sms);
        try {
            //!! SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            if (BuildConfig.DEBUG) Log.d(TAG, "message sent");
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(TAG, "SMS failed");
        }

    }
}
