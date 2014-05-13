package oj.game.surprise;


import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.os.Build;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null)
		{
			
		}

		Thread splashTimer = new Thread()
		{
			public void run()
			{

				try
				{
					sleep(4000);
					 startActivity(new Intent(MainActivity.this,
					 Splash.class));

				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				finally
				{
					finish();
				}
			}

		};

		splashTimer.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		return super.onOptionsItemSelected(item);
	}
	

}