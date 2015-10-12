package edu.virginia.cs.uvathingstodo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> itemList = new ArrayList<String>();
    ArrayList<String[]> itemList2 = new ArrayList<String[]>();
    ArrayAdapter<String> adapter;

    int ItemActivityRequestCode;
    private ListView listView;
    DBHelper mydb;
    String username;

    LocationManager mlocManager;
    LocationListener mlocListener;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ItemActivityRequestCode && resultCode == RESULT_OK && data != null) {
            String number = data.getStringExtra("item_completed");
            int position = Integer.parseInt(number);
            String oldText = itemList.get(position);
            itemList.set(position, oldText + " (Completed)");
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_main_horizontal);
        }
        else {
            setContentView(R.layout.activity_main);
        }

        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();

        try {
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        }
        catch (SecurityException se) {

        }

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            username = extras.getString("username");
        }

        mydb = new DBHelper(this);
        if (mydb.numberOfRows(username) == 0) {
            addItems();
            for (String[] item : itemList2) {
                mydb.insertItem(item[0], item[1], username);
            }
        }

        ArrayList array_list = mydb.getAllItems(username);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);

        TextView welcomeText = (TextView)findViewById(R.id.welcome);
        welcomeText.setText("Hello, " + username);

        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int id_To_Search = position + 1;

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);
                dataBundle.putString("username", username);

                Intent intent = new Intent(getApplicationContext(), ItemActivity.class);

                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList array_list = mydb.getAllItems(username);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array_list);
        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int id_To_Search = position + 1;

            Bundle dataBundle = new Bundle();
            dataBundle.putInt("id", id_To_Search);
            dataBundle.putString("username", username);

            Intent intent = new Intent(getApplicationContext(), ItemActivity.class);

            intent.putExtras(dataBundle);
            startActivity(intent);
            }
        });

        // Handle Profile Section
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            TextView prof_username = (TextView) findViewById(R.id.profile_username);
            TextView numCompleted = (TextView) findViewById(R.id.profile_numTasks);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

            prof_username.setText("Username: " + username);

            Cursor rs;
            int taskCount = 0;
            int numRows = mydb.numberOfRows(username);
            for (int i = 1; i <= numRows; i++) {
                rs = mydb.getData(i, username);
                rs.moveToFirst();
                String completed = rs.getString(rs.getColumnIndex(DBHelper.ITEMS_COLUMN_COMPLETED));
                if (completed.equals("Completed!")) {
                    taskCount++;
                }

                if (!rs.isClosed()) {
                    rs.close();
                }
            }

            numCompleted.setText("Tasks completed: " + taskCount + "/100");
            progressBar.setProgress(taskCount);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected  void onDestroy() {
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            mlocManager.removeUpdates(mlocListener);
        }
        catch (SecurityException se) {

        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // If not horizontal, create menu button for profile
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MenuItem profileMenu = menu.findItem(R.id.profile);
            profileMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.profile) {
            Bundle dataBundle = new Bundle();
            dataBundle.putString("username", username);

            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);

            intent.putExtras(dataBundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void randomItem(View view) {
        Random rand = new Random();
        int position = rand.nextInt(mydb.numberOfRows(username));

        Bundle dataBundle = new Bundle();
        dataBundle.putInt("id", position);
        dataBundle.putString("username", username);

        Intent intent = new Intent(getApplicationContext(), ItemActivity.class);

        intent.putExtras(dataBundle);
        startActivity(intent);
    }

    public void addItems() {
        itemList2.add(new String[] {"Nab the #1 Ticket at Bodo's!", "Get up early and snag the first Bodo's ticket.  Thousands of customers come to this corner shop every day.  Can you beat the crowd?"});
        itemList2.add(new String[] {"Dining Hall Marathon", "Go eat at all three dining halls in one day!"});
        itemList2.add(new String[] {"See a Horse at Foxfield", "Going to Foxfield can be a blast, but make sure you try to remember to see a horse! It is a race after all"});
        itemList2.add(new String[] {"Paint Beta Bridge", "Beta Bridge has een painted over so many times that it has almost half a foot of paint built up on its walls.  Leave your mark on this landmark!"});
        itemList2.add(new String[] {"Take a Professor to Lunch", "You sit in their class.  You may visit their Office Hours.  But to really have the UVA experience Jefferson wanted, get to know them with a free lunch (yes, ODOS gives you the money)."});
        itemList2.add(new String[] {"Play in Mad Bowl", "Don't let that beautiful pit go to waste.  Grab some friends and a ball of some sort and play a game to enjoy a nice day, or wait til it snows and bring a sled."});
        itemList2.add(new String[] {"Go Streaking", "A tradition for the past 40 years, the academical village serves a perfect place for a late night run."});
        itemList2.add(new String[] {"Eat at Bellair Market", "Just a little trip down Ivy, this small gas station has amazing sandwiches.  Recommendation: the Ednam"});
        itemList2.add(new String[] {"Go to Rotunda Sing", "UVA has tons of talented A Capella groups.  Hear them all debut on the steps of the Rotunda to see which is your favorite."});
        itemList2.add(new String[] {"Eat a Gus Burger", "Though I don't recommend it for the taste, getting a Gus Burger from the White Spot is a tradition very old and true at the University."});
        itemList2.add(new String[] {"Swim at Blue Hole", "Go take a drive to the depths of nature and take a two mile hike to this wonderful waterfall swimming area!"});
        itemList2.add(new String[] {"Chow Down at Crozet Pizza", "And by this, we mean the ORIGINAL Crozet Pizza.  Though the one on the Corner is still delicious and has great drink options, the original is worth the trek."});
        itemList2.add(new String[] {"Attend Restoration Ball", "This annual event hosted by the Jefferson Literary and Debating Society brings people from all over the University together for a formal ball, and Dean Groves may teach the Virginia Reel."});
        itemList2.add(new String[] {"Go to a Frat Party", "You may think these are for the younger generations, but relive your first year weekend nights by taking a stroll down Rugby Road and walk through an overcrowded house that reeks of kegs and old pizza."});
        itemList2.add(new String[] {"Make a Purchase at Shady Grady", "Remember that place down on Preston Ave that was the only place that didn't care about IDs? It is still there and still just as shady."});
        itemList2.add(new String[] {"Go to Friday’s After Five", "Head to the Downtown mall and enjoy local bands and artists perform.  And, it’s free!"});
        itemList2.add(new String[] {"Visit Monticello", "Go visit the home of our founder, Thomas Jefferson. Did you know you can see the Rotunda from his yard?"});
        itemList2.add(new String[] {"Climb Humpback Rock", "This is a great location for either a sunrise or sunset hike."});
        itemList2.add(new String[] {"Tube along the James", "Grab a few friends and spend an afternoon rafting down the James River. A few companies will provide the tubes and rides for a reasonable price."});
        itemList2.add(new String[] {"Visit your First Year Dorm", "Remember all those great times, when you didn’t need to worry about getting a job.  Go visit your dorm and drop off a gift or piece of advice."});
        itemList2.add(new String[] {"Attend an A Capella Concert", "Did you know Pitch Perfect is based off of three real A Capella groups including the Hullabahoos?"});
        itemList2.add(new String[] {"Study in the Dome Room", "They said it should be completed by graduation… Good luck with this one."});
        itemList2.add(new String[] {"Go Wine Tasting", "There are tons of wineries around Charlottesville. Grab some friends and check them out, but make sure to have a sober driver."});
        itemList2.add(new String[] {"Pick Apples at Carter’s Mountain", "After you finish, try some of their apple cider and freshly made donuts.  They are to die for."});
        itemList2.add(new String[] {"Take a Historical Tour of Grounds", "UVA has a ton of fascinating history behind it.  Go spend an hour on the lawn learning about this unique story."});
        itemList2.add(new String[] {"Enjoy the Downtown Farmer’s Market", "Each Saturday morning, tons of food stands arrive at the Downtown Mall to show you their unique take on different tastes."});
        itemList2.add(new String[] {"Chill in the Gardens", "Each Garden has a unique style to it.  Check them out – there are 10, pick your favorite."});
        itemList2.add(new String[] {"Jump on Ruffner Bridge", "This bridge was made by an Engineer from VT. You better test the structural integrity.  Grab a few friends and jump three times, then wait."});
        itemList2.add(new String[] {"Climb O-Hill", "And I mean climb the hill to the top, not just to Kellogg. This is a great nature hike and a good workout too."});
        itemList2.add(new String[] {"Order Delivery to Clemons", "Next time you are doing an all-nighter, order some food to help keep you awake.  Little John’s doesn’t deliver past 12, but they can be bribed."});
        itemList2.add(new String[] {"Attend an Event at JPJ", "Concerts and performers come by every year to our basketball stadium.  Check one of these fun events out!"});
        itemList2.add(new String[] {"Sled anywhere on Grounds", "Personal favorites include the Rotunda steps and under McCormick bridge, but next time it snows grab a sled, or a cardboard box, and have a blast."});
        itemList2.add(new String[] {"Attend Final Fridays at the Art Museum", "Did you know we have our own art museum, right there on Rugby Road? You need to check this event out!"});
        itemList2.add(new String[] {"Go Steam Tunneling", "This is not a school sanctioned tradition, so I warn you – be careful.  There is some really cool graffiti down there, but don’t get lost"});
        itemList2.add(new String[] {"See a Show at the Paramount", "Another great venue that we have so close to our University.  Take advantage of the big names that come each year."});
        itemList2.add(new String[] {"Eat Spudnuts", "Do it. Enough said."});
        itemList2.add(new String[] {"Experience Lighting of the Lawn", "This event brings thousands of the University together to enjoy the end of the first semester and an awesome lightshow as well."});
        itemList2.add(new String[] {"Eat at Christian’s", "This late night pizza spot is great to end a night on the corner.  I suggest their tortellini slice."});
        itemList2.add(new String[] {"Play Broomball", "Go to the ice skating rink and play this intense version of hockey. You use brooms, and shoes."});
        itemList2.add(new String[] {"Eat Dumplings on the Corner", "Marco and Luca’s or Got Dumplings.  What’s your preference?"});
        itemList2.add(new String[] {"Visit the Special Collections Library", "Did you know we have one of the original drafts of the Declaration of Independence?"});
        itemList2.add(new String[] {"Pull an All-Nighter in Clemons", "Though this might not be the most fun item on the list, it is a staple for the UVA experience."});
        itemList2.add(new String[] {"Eat Breakfast at Pigeon Hole", "This quaint little stop on the corner makes some mean eggs.  It is small, so be prepared to wait."});
        itemList2.add(new String[] {"Go to Drag Bingo", "Who doesn’t like Bingo? And this event brings lots of different communities of UVA together."});
        itemList2.add(new String[] {"Watch a Movie on the Lawn", "Grab some blankets and pull up your roommate’s brother’s ex’s Netflix account that you have no shame you aren’t paying for."});
        itemList2.add(new String[] {"Join the Alumni Association", "It’s free while you are a student and you get lots of deals and free things, so why not?"});
        itemList2.add(new String[] {"See the Sunrise on the Lawn", "Another beautiful morning can be spent right here on our central grounds as you watch the sun rise over the Academical Village."});
        itemList2.add(new String[] {"Relax in the AFC Hot Tub", "It is the biggest hot tub east of the Mississippi River, FYI. And they stream movies."});
        itemList2.add(new String[] {"Participate in a 5K", "There isn’t a chance you have walked across Grounds and not been handed a flyer for one of these.  Actually do one!"});
        itemList2.add(new String[] {"Play Bocce on the Lawn", "Want to feel even more pretentious.  Grab some Bocce balls and start a game.  Or better – Croquet."});
        itemList2.add(new String[] {"See Tom Deluca Perform", "Every year for fall orientation, this hypnotist amazes the crowds.  Get there early and be loud if you want to be one of his victims."});
        itemList2.add(new String[] {"Eat at Arch’s Ice Cream", "Try the gooey brownie.  Seriously."});
        itemList2.add(new String[] {"Check out a Book from the Library", "We have 17 libraries and 6 million books.  I’m sure at least one of them is interesting."});
        itemList2.add(new String[] {"Trick-or-Treat on the Lawn", "Come help as UVA opens its doors to the Charlottesville community and hundreds of locals trick-or-treat on the lawn. What is your favorite costume?"});
        itemList2.add(new String[] {"Play an IM Sport", "Flag Football, Ultimate Frisbee, Innertube Waterpolo, etc.  Grab some friends and see if you can be the champion."});
        itemList2.add(new String[] {"Go to the Virginia Film Festival", "Tons of movies to choose from, and they are all free!"});
        itemList2.add(new String[] {"Tailgate on the Lawn", "Guys in Ties, Girls in Pearls, and tons of rowdy students preparing for a football game on the Lawn!"});
        itemList2.add(new String[] {"Ride the Trolley", "This free trolley goes from UVA to the Downtown Mall. Complete this in conjunction with another item on the list."});
        itemList2.add(new String[] {"Attend a Concert in Old Cabell", "Our music department is very impressive.  Go see a performance in this beautiful space."});
        itemList2.add(new String[] {"Get a Hug from Miss Kathy", "Literally the nicest person you will ever meet. Let her make your day."});
        itemList2.add(new String[] {"See the River on the lawn", "Lay down backwards on the steps and tilt your head back.  It may take a while to notice but once you see it you will know."});
        itemList2.add(new String[] {"Hop around at Jump CVille", "This building is filled entirely with trampolines.  Might be meant for kids, but we are still kids at heart."});
        itemList2.add(new String[] {"Half-price Thursday at IHOP", "Yes, IHOP gives you half off as a student if you go on Thursday.  You need to order a beverage though."});
        itemList2.add(new String[] {"Visit Carr’s Hill", "This is where Teresa Sullivan and the presidents of UVA’s past have all lived. She may give you ice cream."});
        itemList2.add(new String[] {"Sing the Good Ole Song", "If you haven’t done this yet, are you really even a student at the University of Virginia?"});
        itemList2.add(new String[] {"Study in the McGregor Room", "Commonly referred to as the Harry Potter Room, this dimly lit study space is perfect to catch up on a much needed nap."});
        itemList2.add(new String[] {"Rent a Movie from Clemons", "The third floor is home to much of our media studies material. They have a vast DVD collection."});
        itemList2.add(new String[] {"Get Sweaty at Hot Yoga", "Doing Yoga is fun, but doing it in 100 plus degree temperatures is great! Check this out downtown."});
        itemList2.add(new String[] {"Tell a Secret at the Whispering Wall", "Right by Newcomb Hall is a large, curved bench.  Whisper secrets to a friend standing on the other side."});
        itemList2.add(new String[] {"Read a book in the Amphitheater", "First, reading books is good to do.  Second, this is a cool area on Grounds, so why not combine the two."});
        itemList2.add(new String[] {"High-five Cavman", "Meet our mascot on any game day.  Can you figure out who wears the suit?"});
        itemList2.add(new String[] {"High-five Dean Groves", "Our Dean of Students has style.  Ask him how many bow ties he has in his collection."});
        itemList2.add(new String[] {"Experience Late Night Little John’s", "Eat one of these delicious sandwiches.  Try the Chipotle Chicken or Wild Turkey."});
        itemList2.add(new String[] {"Watch a Lacrosse Game", "Our Lacrosse team is incredibly talented. Go support them at Klockner Stadium."});
        itemList2.add(new String[] {"Take a Ride down Skyline Drive", "This scenic road is full of amazing views of the surrounding mountains and nature of Charlottesville."});
        itemList2.add(new String[] {"Go Saki-bombing at Kuma", "Or, if you are feeling really brave, take on the Kuma Bomb.  This $25 drink is free if you can finish in 23 seconds."});
        itemList2.add(new String[] {"Order a Crepe from The Flat", "This hole in the wall serves delicious crepes which can be eaten for a meal or desert. Cash only."});
        itemList2.add(new String[] {"Attend Trivia Night", "Go to Mellow Mushroom on any Wednesday night to play trivia against a bustling crowd. Go early to get a seat."});
        itemList2.add(new String[] {"Eat at Poe’s", "No. 3, Eddy’s, Poe’s, the Jabberwock, whatever the place wants to call itself, go try their chicken sandwich."});
        itemList2.add(new String[] {"See a Probate", "These events don’t happen as frequently as others, but when one is advertised, make sure to attend.  They are really cool to see."});
        itemList2.add(new String[] {"Visit Ash Lawn", "This venue is a great destination for formal events, but it is also a nice place to visit any day of the week."});
        itemList2.add(new String[] {"Recycle", "Because we care about the environment."});
        itemList2.add(new String[] {"Take an Exam Outside", "Take advantage of the Community of Trust and the Honor System and take one of your exams outdoors."});
        itemList2.add(new String[] {"Laugh at the Yellow Journal", "This satirical publication comes out a couple times each year and is full of humor about our student body."});
        itemList2.add(new String[] {"Engage in Dialogue", "Whether you are involved in Sustained Dialogue or not, having insightful conversations around Grounds is important for our community."});
        itemList2.add(new String[] {"Slackline on the Lawn", "People will constantly throw up a line between the trees on the lawn.  Give it a try. They have an email list if you want updates on when to go."});
        itemList2.add(new String[] {"Meet Someone New", "Even if this is your last year at this school, try to find someone you don’t know and make a new friend."});
        itemList2.add(new String[] {"Go for a Run", "Take an afternoon, put on some running clothes, and take a jog around Grounds. Explore the areas you are less familiar."});
        itemList2.add(new String[] {"Jump in Dell Pond", "On second thought, don’t.  Please don’t do this. Seriously, you will regret it."});
        itemList2.add(new String[] {"Call a Parent or Family Member", "Remember who cared for you the past 18 years before you left for college and tell them that you love them. Then hope they send you that care package for making an effort."});
        itemList2.add(new String[] {"Read the Cavalier Daily", "Check out the journalistic skills of your peers and stay up to date with the topics in our community."});
        itemList2.add(new String[] {"Volunteer through Madison House", "Madison House has a variety of amazing volunteering opportunities – from helping kids to spending time with the elderly."});
        itemList2.add(new String[] {"Give Back to Charlottesville", "Remember how great a time this city has given you and give back to the community in some way."});
        itemList2.add(new String[] {"Indulge at Pint Night", "Head to Mellow on any Tuesday night after 8pm for $2.50 pints.  They always have 39 different beers on tap, and finishing them all gets you a special mug."});
        itemList2.add(new String[] {"Go to an International Party", "These can get WILD. You won’t experience a late night out until you hit one of these parties up."});
        itemList2.add(new String[] {"Check out the Music Library", "It doesn’t matter what major, anyone can enjoy the study spaces in the music library."});
        itemList2.add(new String[] {"Eat a Pancake for Parkinson’s", "Help a great cause and enjoy a delicious breakfast pastry on the South end of the Lawn."});
        itemList2.add(new String[] {"Attend the Last Lecture Series", "Hear some of the greatest lectures given by renowned professors.  It will be memorable."});
        itemList2.add(new String[] {"Pay it Forward", "Help out an underclassman.  Remember anything someone did for you and help make someone else’s experience here great!"});
        itemList2.add(new String[] {"Go on a Real Date", "This one might be one of the hardest on the list, but there is so many options so find that special person and take them out."});

        for (int i = 0; i < itemList2.size(); i++) {
            itemList.add(itemList2.get(i)[0]);
        }
    }

    public class MyLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location loc)
        {

        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(),
                    "Gps Disabled",
                    Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Toast.makeText(getApplicationContext(),
                    "Gps Enabled",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }
    }/* End of Class MyLocationListener */
}
