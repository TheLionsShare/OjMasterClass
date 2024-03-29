package oj.game.surprise;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.os.Build;

public class Splash extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		if (savedInstanceState == null)
		{
			
		}

		Thread splashTimer = new Thread()
		{
			public void run()
			{

				MediaPlayer drank = MediaPlayer.create(Splash.this,
						R.raw.splashsound);

				try
				{
					drank.start();
					sleep(5000);
					startActivity(new Intent(Splash.this,
							Game.class));

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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment
	{

		public PlaceholderFragment()
		{
		}

		
	}

}