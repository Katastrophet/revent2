package de.ur.mi.revent;

import android.app.Activity;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import de.ur.mi.revent.Download.DownloadListener;
import de.ur.mi.revent.Download.DownloadManager;
import de.ur.mi.revent.Menu._NavigationMenu;
import de.ur.mi.revent.Template.EventItem;
import de.ur.mi.revent.Template._EventItemArrayAdapter;

public class KommendeEvents extends Activity implements DownloadListener{
    private _NavigationMenu navigationMenu;
    //EventList
    private ArrayList<EventItem> table;
    private ListView eventList_KE;
    private _EventItemArrayAdapter aa;
    //
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ke);
        navigationMenu=new _NavigationMenu(this);

        setEventList();
    }

    private void setEventList(){
        eventList_KE=(ListView)findViewById(R.id.eventList_KE);
        table = new ArrayList<EventItem>();
        getDownloadData();
        Collections.sort(table);
        aa=new _EventItemArrayAdapter(this,R.layout.event_list_items,table);
        eventList_KE.setAdapter(aa);

        eventList_KE.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EventItem eventItem =(EventItem) eventList_KE.getItemAtPosition(i);
                navigationMenu.showEvent(eventItem.getTitle(),eventItem.getDate().toString(),eventItem.getTime().toString(),eventItem.getLocation(),eventItem.getOrganizer(),eventItem.getType(),eventItem.getNotes(),eventItem.getId());
            }
        });
    }

    private void getDownloadData(){
        //Startet den Download und setzt einen Listener an (auch wenn noch laufend),
        //sofern dieser nicht schon (erfolgreich) beendet wurde.
        try{
            switch (DownloadManager.getStatus()){
                case FINISHED:
                    table = DownloadManager.getResults();
                    break;
                case PENDING:
                    DownloadManager.startDownload();
                    DownloadManager.setListener(this);
                    break;
                case RUNNING:
                    DownloadManager.setListener(this);
                    break;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDownloadFinished() {
        try {
            table = DownloadManager.getResults();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        navigationMenu.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        navigationMenu.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
