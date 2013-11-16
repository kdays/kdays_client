package com.kdclient.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.kdclient.R;


/**
 * A base activity that handles common functionality in the app.
 */
public abstract class BaseActivity extends SherlockFragmentActivity {

    private static final int TOAST_DURATION = Toast.LENGTH_SHORT;
    ActionBarSherlock mSherlock = ActionBarSherlock.wrap(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	setTheme(R.style.Theme_Sherlock_Light); 
        super.onCreate(savedInstanceState);
        
        mSherlock.setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.help_info_menu_item:
            	Intent mainIntent = new Intent(BaseActivity.this,
        				LicenseActivity.class);
        		
            	startActivity(mainIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**  
     * @param resId resource id
     */
    public void displayToast(int resId) {
        Toast.makeText(this, resId, TOAST_DURATION).show();
    }    

    /**  
     * @param text desplay text
     */
    public void displayToast(CharSequence text) {
        Toast.makeText(this, text, TOAST_DURATION).show();
    }
}

