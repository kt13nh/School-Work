package ca.brocku.kt13nh.scheduler;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
/**
 * Created by Tram on 26/11/2017.
 */


public class AllFragment extends Fragment {
    String id="";
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] fields=new String[]{"id","date","room","time"};
        ListView lv= (ListView)getView().findViewById(R.id.listView);
        ArrayList<String> entries=new ArrayList<>();

        DataHelper dh=new DataHelper(getActivity());
        SQLiteDatabase datareader=dh.getReadableDatabase();

        Cursor cursor=datareader.query(DataHelper.DB_TABLE,fields,
                null,null,null,null,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            //System.out.println(Integer.toString(cursor.getInt(0))+", "+
            // cursor.getString(1)+", "+cursor.getString(2));
            entries.add(cursor.getInt(0)+". Date: "+
                    cursor.getString(1)+", Room: "+cursor.getString(2)+", Time:"+cursor.getString(3));
            cursor.moveToNext();
        }

        if (cursor!=null&&!cursor.isClosed())
            cursor.close();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,entries);
        lv.setAdapter(adapter);
        registerForContextMenu(lv);
        datareader.close();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.listView) {
            ListView lv=(ListView)v;
            AdapterView.AdapterContextMenuInfo cmi=
                    (AdapterView.AdapterContextMenuInfo) menuInfo;
            String entry=(String)lv.getItemAtPosition(cmi.position);
            menu.setHeaderTitle(entry);
            menu.add("Delete");
            menu.add("Cancel");
            id = ""+entry.charAt(0);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        DataHelper dh=new DataHelper(getActivity());
        SQLiteDatabase datareader=dh.getReadableDatabase();
        if(item.getTitle().equals("Delete")) {
            try {
                System.out.println(id);
                String where = "ID" + "=" + id;
                refresh();
                return datareader.delete(DataHelper.DB_TABLE, where, null) != 0;
            }catch(Exception ex){};
        }
        return true;
    }
    public void refresh(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.all_fragment, container, false);
    }
}