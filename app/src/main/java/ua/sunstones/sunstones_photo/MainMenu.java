package ua.sunstones.sunstones_photo;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

public class MainMenu {

    public static Boolean onOptionsItemSelected(MenuItem item, Activity act){

        switch (item.getItemId()) {

            case R.id.menu_main_authorization:
                act.startActivity(new Intent(act, AutorizationActivity.class));
                return true;

//            case R.id.menu_main_synchronization:
//                act.startActivity(new Intent(act, SynchronizationActivity.class));
//                return true;
//
//            case R.id.menu_main_offers:
//                act.startActivity(new Intent(act, OffersActivity.class));
//                return true;
//
//            case R.id.menu_main_orders:
//                act.startActivity(new Intent(act, OrdersActivity.class));
//                return true;

//            case R.id.menu_main_assemblies:
//                act.startActivity(new Intent(act, AssembliesActivity.class));
//                return true;

        }

        return false;
    }
}
