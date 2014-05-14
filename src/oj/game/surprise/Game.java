package oj.game.surprise;

import java.util.HashMap;
import java.util.Map;

import oj.game.physics.Field;
import oj.game.physics.LevelManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Game extends Activity implements Field.Delegate {

	Field field;
	LevelManager levelManager;

	FieldView fieldView;
	View menuView;
	TextView levelText;
	TextView livesText;
	int deaths;
	TextView statusText;
	TextView bestLevelText;
	TextView bestFreePlayLevelText;
	ImageButton continueFreePlayButton;
	View bestFreePlayLevelView;
	View bestLevelView;
	MenuItem endGameMenuItem;
	MenuItem selectBackgroundImageMenuItem;
	MenuItem preferencesMenuItem;

	private static final int ACTIVITY_PREFERENCES = 1;

	Handler messageHandler;

	int lives = 10;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		messageHandler = new Handler() {
			public void handleMessage(Message m) {
				processMessage(m);
			}
		};

		levelManager = new LevelManager();

		field = new Field();
		field.setDelegate(this);
		field.setLevelManager(levelManager);
		field.setMaxBullets(levelManager.numberOfBulletsForCurrentLevel());

		levelText = (TextView) findViewById(R.id.levelText);
		livesText = (TextView) findViewById(R.id.livesText);
		// statusText = (TextView)findViewById(R.id.statusText);

		// uncomment to clear high scores
		/*
		 * setBestLevel(true, 0); setBestLevel(false, 0);
		 */

		bestLevelText = (TextView)findViewById(R.id.bestLevelText);
		bestFreePlayLevelText = (TextView) findViewById(R.id.bestFreePlayLevelText);
		continueFreePlayButton = (ImageButton) findViewById(R.id.continueFreePlayButton);
		bestLevelView = findViewById(R.id.bestLevelView);
		bestFreePlayLevelView = findViewById(R.id.bestFreePlayLevelView);

		
		  ImageButton newGameButton =
		  (ImageButton)findViewById(R.id.normalButton);
		  newGameButton.setOnClickListener(new View.OnClickListener() { public
		  void onClick(View v) { doNewGame(); } });
		 

		ImageButton marathonButton = (ImageButton) findViewById(R.id.marathonButton);
		marathonButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doFreePlay(1);

			}
		});

		continueFreePlayButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doFreePlay(bestLevel(true));
			}
		});

		ImageButton aboutButton = (ImageButton) findViewById(R.id.helpButton);
		aboutButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doAbout();
			}
		});

		menuView = findViewById(R.id.menuView);
		updateBestLevelFields();
		menuView.requestFocus();

		fieldView = (FieldView) findViewById(R.id.fieldView);
		fieldView.setField(field);
		fieldView.setMessageHandler(messageHandler);

	}

	@Override
	public void onPause() {
		super.onPause();
		fieldView.stop();
	}

	@Override
	public void onResume() {
		super.onResume();
		fieldView.start();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		endGameMenuItem = menu.add(R.string.end_game);
		// preferencesMenuItem = menu.add(R.string.preferences);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if (item == endGameMenuItem) {
			doGameOver();
		}
		// else if (item==preferencesMenuItem) {
		// Intent settingsActivity = new Intent(getBaseContext(),
		// DodgePreferences.class);
		// startActivityForResult(settingsActivity, ACTIVITY_PREFERENCES);
		// }
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		switch (requestCode) {
		case ACTIVITY_PREFERENCES:
			//updateFromPreferences();
			break;
		}
	}

	boolean inFreePlay() {
		return (lives < 0);
	}

	// methods to store and retrieve the highest normal and free play levels
	// reached, using SharedPreferences
	int bestLevel(boolean freePlay) {
		String key = (freePlay) ? "BestFreePlayLevel" : "BestLevel";
		return getPreferences(MODE_PRIVATE).getInt(key, 0);
	}

	void setBestLevel(boolean freePlay, int val) {
		String key = (freePlay) ? "BestFreePlayLevel" : "BestLevel";
		SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
		editor.putInt(key, val);
		editor.commit();
	}

	void recordCurrentLevel() {
		if (levelManager.getCurrentLevel() > bestLevel(inFreePlay())) {
			setBestLevel(inFreePlay(), levelManager.getCurrentLevel());
		}
	}

	void updateBestLevelFields() {
		int bestNormal = bestLevel(false);
		 bestLevelText.setText((bestNormal>1) ? String.valueOf(bestNormal) :
		 getString(R.string.score_none));

		int bestFree = bestLevel(true);
		bestFreePlayLevelText.setText((bestFree > 1) ? String.valueOf(bestFree)
				: getString(R.string.score_none));
		continueFreePlayButton.setEnabled(bestFree > 1);
		menuView.forceLayout();
	}

	void processMessage(Message m) {
		String action = m.getData().getString("event");
		if ("goal".equals(action)) {
			levelManager.setCurrentLevel(1 + levelManager.getCurrentLevel());
			recordCurrentLevel();
			synchronized (field) {
				field.setMaxBullets(levelManager
						.numberOfBulletsForCurrentLevel());
			}
			updateScore();
		} else if ("death".equals(action)) {
			if (lives > 0) {
				lives--;
				
			}
			else
			{
				deaths++;
			}
			synchronized (field) {
				if (lives == 0) {
					field.removeDodger();
					doGameOver();
				} else {
					fieldView.startDeathAnimation(field.getDodger()
							.getPosition());
					field.createDodger();
				}
			}
			updateScore();
		}
	}

	void updateScore() {
		levelText.setText(getString(R.string.level_prefix)
				+ levelManager.getCurrentLevel());
		if (lives >= 0) {
			livesText.setText(getString(R.string.lives_prefix)
					+ ((lives >= 0) ? "" + lives
							: getString(R.string.free_play_lives)));
		}
		else{
			livesText.setText("Deaths: " + deaths);
		}

	}

	void doGameOver() {
		field.removeDodger();
		//statusText.setText(getString(R.string.game_over_message));
		updateBestLevelFields();
		menuView.setVisibility(View.VISIBLE);
	}

	void startGameAtLevelWithLives(int startLevel, int numLives) {
		levelManager.setCurrentLevel(startLevel);
		field.setMaxBullets(levelManager.numberOfBulletsForCurrentLevel());
		field.createDodger();
		menuView.setVisibility(View.INVISIBLE);
		lives = numLives;
		updateScore();
	}

	void doNewGame() {
		startGameAtLevelWithLives(1, 10);
	}

	void doFreePlay(int startLevel) {
		startGameAtLevelWithLives(startLevel, -1);
	}

	void doAbout() {
		Intent aboutIntent = new Intent(this, Help.class);
		this.startActivity(aboutIntent);
	}

	void sendMessage(Map params) {
		Bundle b = new Bundle();
		for (Object key : params.keySet()) {
			b.putString((String) key, (String) params.get(key));
		}
		Message m = messageHandler.obtainMessage();
		m.setData(b);
		messageHandler.sendMessage(m);
	}

	// Field.Delegate methods
	// these occur in a separate thread, so use Handler
	public void dodgerHitByBullet(Field theField) {
		Map params = new HashMap();
		params.put("event", "death");
		sendMessage(params);
	}

	public void dodgerReachedGoal(Field theField) {
		Map params = new HashMap();
		params.put("event", "goal");
		sendMessage(params);
	}
}