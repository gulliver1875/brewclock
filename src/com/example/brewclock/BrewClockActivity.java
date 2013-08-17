package com.example.brewclock;

import android.app.*;
import android.database.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;


public class BrewClockActivity extends Activity implements OnItemSelectedListener, OnClickListener
 {
  /** Properties **/
  protected Button brewAddTime;
  protected Button brewDecreaseTime;
  protected Button startBrew;
  protected TextView brewCountLabel;
  protected TextView brewTimeLabel;
  protected Spinner teaSpinner;
  protected TeaData teaData;

  protected int brewTime = 3;
  protected CountDownTimer brewCountDownTimer;
  protected int brewCount = 0;
  protected boolean isBrewing = false;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
	
    // Connect interface elements to properties
    brewAddTime = (Button) findViewById(R.id.brew_time_up);
    brewDecreaseTime = (Button) findViewById(R.id.brew_time_down);
    startBrew = (Button) findViewById(R.id.brew_start);
    brewCountLabel = (TextView) findViewById(R.id.brew_count_label);
    brewTimeLabel = (TextView) findViewById(R.id.brew_time);
	teaSpinner = (Spinner) findViewById(R.id.tea_spinner);
	
    // Setup ClickListeners
    brewAddTime.setOnClickListener(this);
    brewDecreaseTime.setOnClickListener(this);
    startBrew.setOnClickListener(this);
    
    // Set the initial brew values
    setBrewCount(0);
    setBrewTime(3);
	
	// Set data source
	teaData = new TeaData(this);
	
	// Add some default teas
	if(teaData.count() == 0) {
		teaData.insert("Earl Gray", 3);
		teaData.insert("Matcha", 1);
		teaData.insert("Jasmine", 2);
		teaData.insert("Ruibos", 4);
	}
	
	// Get a Cursor with all data
	Cursor cursor = teaData.all(this);
	
	SimpleCursorAdapter teaCursorAdapter = new SimpleCursorAdapter(
		this,
		android.R.layout.simple_spinner_item,
		cursor,
		new String[] { TeaData.NAME },
		new int[] { android.R.id.text1 }
	);
	
	teaCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	teaSpinner.setAdapter(teaCursorAdapter);
	teaSpinner.setOnItemSelectedListener(this);
  }
  
  /** Methods **/
  
  /**
   * Set an absolute value for the number of minutes to brew. Has no effect if a brew
   * is currently running.
   * @param minutes The number of minutes to brew.
   */
  public void setBrewTime(int minutes) {
    if(isBrewing)
      return;
    
    brewTime = minutes;
    
    if(brewTime < 1)
      brewTime = 1;

    brewTimeLabel.setText(String.valueOf(brewTime) + "m");
  }
  
  /**
   * Set the number of brews that have been made, and update the interface. 
   * @param count The new number of brews
   */
  public void setBrewCount(int count) {
    brewCount = count;
    brewCountLabel.setText(String.valueOf(brewCount));
	//Log.e("setcount", "error");
  }
  
  /**
   * Start the brew timer
   */
  public void startBrew() {
    // Create a new CountDownTimer to track the brew time
    brewCountDownTimer = new CountDownTimer(brewTime * 60 * 1000, 1000) {
      @Override
      public void onTick(long millisUntilFinished) {
        brewTimeLabel.setText(String.valueOf(millisUntilFinished / 1000) + "s");
      }
      
      @Override
      public void onFinish() {
        isBrewing = false;
        setBrewCount(brewCount + 1);
        
        brewTimeLabel.setText("Brew Up!");
        startBrew.setText("Start");
      }
    };
    
    brewCountDownTimer.start();
    startBrew.setText("Stop");
    isBrewing = true;
  }
  
  /**
   * Stop the brew timer
   */
  public void stopBrew() {
    if(brewCountDownTimer != null)
      brewCountDownTimer.cancel();
    
    isBrewing = false;
    startBrew.setText("Start");
  }
  
  /** Interface Implementations **/
  /* (non-Javadoc)
   * @see android.view.View.OnClickListener#onClick(android.view.View)
   */
  public void onClick(View v) {
    if(v == brewAddTime)
      setBrewTime(brewTime + 1);
    else if(v == brewDecreaseTime)
      setBrewTime(brewTime -1);
    else if(v == startBrew) {
      if(isBrewing)
        stopBrew();
      else
        startBrew();
    }
  }
  
  public void onItemSelected(AdapterView <?> spinner, View view, int position, long id){
	  if(spinner == teaSpinner) {
		  // Update brew time
		  Cursor cursor = (Cursor) spinner.getSelectedItem();
		  setBrewTime(cursor.getInt(2));
	  }
  }
  
  public void onNothingSelected(AdapterView adapterView){
	  
  }
}
