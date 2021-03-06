package de.ur.mi.revent;

import android.app.Activity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import de.ur.mi.revent.Download.DownloadListener;
import de.ur.mi.revent.Download.DownloadManager;
import de.ur.mi.revent.LocalDatabase.LocalDatabase;
import de.ur.mi.revent.Menu._NavigationMenu;
import de.ur.mi.revent.Template.EventItem;
import de.ur.mi.revent.Template._EventItemArrayAdapter;

public class MainActivity extends Activity{
    private _NavigationMenu navigationMenu;
    private static final int PERMISSIONS_REQUEST_CODE = 0;
    private static LocalDatabase markedEventsDatabase;
    private ListView eventList_MainMarkedEvents;
    private ArrayList<EventItem>markedEventsList;
    private _EventItemArrayAdapter aa;
    private Button button_UpcomingEvents;
    private Button button_RecommendedEvents;

    @Override
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
            init();
        } else {
            init();
        }
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    private void init() {
        initDatabase();
        initUI();
        DownloadManager.startDownload();
    }

    private void initUI(){
        setContentView(R.layout.activity_main);
        navigationMenu=new _NavigationMenu(this);

        eventList_MainMarkedEvents=(ListView)findViewById(R.id.eventList_mainMarkedEvents);
        button_UpcomingEvents=(Button)findViewById(R.id.button_UpcomingEvents);
        button_RecommendedEvents=(Button)findViewById(R.id.button_RecommendedEvents);

        markedEventsList=markedEventsDatabase.getAllEventItems();
        aa=new _EventItemArrayAdapter(this,R.layout.event_list_items,markedEventsList);
        eventList_MainMarkedEvents.setAdapter(aa);
        eventList_MainMarkedEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventItem eventItem =(EventItem) eventList_MainMarkedEvents.getItemAtPosition(i);
               navigationMenu.showEvent(eventItem.getTitle(),eventItem.getDate().toString(),eventItem.getTime().toString(),eventItem.getLocation(),eventItem.getOrganizer(),eventItem.getType(),eventItem.getNotes(),eventItem.getId());
           }
        });

        button_UpcomingEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationMenu.showKommendeEvents();
            }
        });
        button_RecommendedEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationMenu.showVorgeschlageneEvents();
            }
        });
    }

    private void initDatabase() {
        markedEventsDatabase = new LocalDatabase(this);
        markedEventsDatabase.open();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        navigationMenu.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        navigationMenu.onOptionsItemSelected(item);
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                //Sollte die Berechtigung nicht erteilt werden, so ist der Array leer.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public static LocalDatabase getMarkedEventsDatabase(){
        return markedEventsDatabase;
    }

    @Override
    public void onBackPressed() {
        //Bei Betätigen des Backbuttons innerhalb der MainActivity wird die App beendet.
        super.onBackPressed();
        finishAffinity();
    }
}
