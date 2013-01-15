package com.delorme.pace;

//general stuff
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings.TextSize;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

//for accelerometer
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

//for mp3 player
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

//for mp3 player -- misc
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.cmc.music.metadata.ImageData;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;


public class PlayerActivity extends Activity implements SensorEventListener, OnCompletionListener, SeekBar.OnSeekBarChangeListener {

	//sensor for accelerometer
	private SensorManager sensorManager;
	// mp3 player
	private MediaPlayer mediaPlayer;
	private SongsManager songsManager;
	//private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private HashMap<String, ArrayList<HashMap<String, String>>> songsList2 = new HashMap<String, ArrayList<HashMap<String, String>>>();
	// misc functions
	private MiscFunctions miscFunctions;
	
	//mp3 buttons
	private ImageButton btnPlay;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	
	//text fields
	private TextView txtSongTitle;
	private TextView txtTimeElapsed;
	private TextView txtDuration;
	private TextView txtSPM;
	private TextView txtSPS;
	private TextView txtBPM;
	
	
	//image fields
	private ImageView imgAlbumArt;
	
	//seekbar
	private SeekBar sbSong;
	
	//handler for threads
	private Handler mHandler;
	
	//steps in current song
	private int currentStep = 0;
	//song progress in miliseconds
	private int currentSongProgress = 0;
	
	private boolean over2G = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
        
        //disable screen rotation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        //registers sensor manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
       
        // mp3
        mediaPlayer = new MediaPlayer();
        songsManager = new SongsManager();
        miscFunctions = new MiscFunctions();
        //songsList = songsManager.getPlayList();
        songsList2 = songsManager.getPlayList2();
        
        //buttons
        btnPlay = (ImageButton)findViewById(R.id.imageButtonPlay);
        btnNext = (ImageButton)findViewById(R.id.imageButtonNext);
        btnPrevious = (ImageButton)findViewById(R.id.imageButtonPrevious);
        
        //text
        txtSongTitle = (TextView)findViewById(R.id.textSongTitle);
        txtDuration = (TextView)findViewById(R.id.textViewDuration);
        txtTimeElapsed = (TextView)findViewById(R.id.textViewElapsed);
        txtSPM = (TextView)findViewById(R.id.textViewSPM);
        txtSPS = (TextView)findViewById(R.id.textViewSPS);
        txtBPM = (TextView)findViewById(R.id.textViewBPM);
        
        //images
        imgAlbumArt = (ImageView)findViewById(R.id.imageViewAlbumArt);
        
        //seekbar
        sbSong = (SeekBar)findViewById(R.id.seekBar1);
        
        //handler for threads
        mHandler = new Handler();
        
        //listener for seekbar
        sbSong.setOnSeekBarChangeListener(this);
        
        //listener for song completion
        mediaPlayer.setOnCompletionListener(this);
        
        //play a random song
        playSong();
        
		sensorManager.registerListener(this,
		        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		        SensorManager.SENSOR_DELAY_FASTEST);
        
        btnPlay.setOnClickListener( new View.OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
			   	if (mediaPlayer.isPlaying())
			   	{
			   		mediaPlayer.pause();
			   		btnPlay.setImageResource(R.drawable.play);
   		
			   	}
			   	else
			   	{
		   		   mediaPlayer.start();
		   		   btnPlay.setImageResource(R.drawable.pause);
			   	}
			}
		});    	


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	mHandler.removeCallbacks(mUpdateTimeTask);
                playSong();
            }
        });
        
        btnPrevious.setOnClickListener(new View.OnClickListener() {
        	        	
            @Override
            public void onClick(View arg0) {
            	
            	mHandler.removeCallbacks(mUpdateTimeTask);
            	playPreviousSong();
            }
        });
        
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onPause(){
		super.onPause();
		sensorManager.unregisterListener(this);
		System.exit(0);
	}
	
	public void onResume()
	{
		super.onResume();
	}


	  @Override
	  public void onSensorChanged(SensorEvent event) {
	    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	      getAccelerometer(event);
	    }

	  }

	  
	  //play previous song.. kinda overkill 
	  public void playPreviousSong()
	  {
		  
	        try {
  	
	        	//get the previous song
	        	HashMap<String, String> song = songsManager.getPreviousSong();       	
	        	
	            mediaPlayer.reset();
	            mediaPlayer.setDataSource(song.get("songPath"));
	            mediaPlayer.prepare();
	            mediaPlayer.start();
	                                   
	            String songTitle = song.get("songTitle");
	            String songArtist = song.get("songArtist");
	            String songBPM = song.get("songBPM");
	            
	            txtSongTitle.setText(songArtist + " - " + songTitle);
	            txtBPM.setText("BPM: " + songBPM);

	            File songFile = new File(song.get("songPath"));
	            MusicMetadataSet src_set = null;
	            
                //uses external api to access the mp3 ID3 tag
                try {
					 src_set = new MyID3().read(songFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                Vector v = null;
                
                //if id3 exists, populate song/artist info (not using hack job filename shit
                if (src_set !=null)
                {
                	MusicMetadata metadata = (MusicMetadata) src_set.getSimplified();
                	v = metadata.getPictureList();
                	
                }
	 
                if (v.size() > 0)
                {
                	ImageData id = (ImageData)v.get(0);
                	Bitmap b = BitmapFactory.decodeByteArray(id.imageData, 0, id.imageData.length);
                	
                	imgAlbumArt.setImageBitmap(b);

                }
                
	            // Changing Button Image to pause image
	            btnPlay.setImageResource(R.drawable.pause);
	 
	            // set Progress bar values
	            sbSong.setProgress(0);
	            sbSong.setMax(10000);
	            
	            //kicks off seekbar listener
	            mHandler.postDelayed(mUpdateTimeTask, 500);
	            currentStep = 0; // reset current steps
	            currentSongProgress = 0; //reset how long song has been playing
	            
	            // Updating progress bar
	            //updateProgressBar();
	        } catch (IllegalArgumentException e) {
	            e.printStackTrace();
	        } catch (IllegalStateException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		  
		  
		  
	  }
	  
	  
	    /**
	     * Function to play a song
	     * @param songIndex
	     * */
	    public void  playSong(){
	        // Play song
	        try {
	        	
                double SPM = ((double)currentStep / (double)(currentSongProgress / 1000d)) * 60d ;       	
	        	//use SPM to determine the next song ( or first song )
	        	HashMap<String, String> song = songsManager.getSongBySPM((int)Math.floor(SPM));
	        	//Toast.makeText(this, String.valueOf((int)Math.floor(SPM)), Toast.LENGTH_SHORT).show();
	        	
	        	
	            mediaPlayer.reset();
	            mediaPlayer.setDataSource(song.get("songPath"));
	            mediaPlayer.prepare();
	            mediaPlayer.start();
	                                   
	            String songTitle = song.get("songTitle");
	            String songArtist = song.get("songArtist");
	            String songBPM = song.get("songBPM");
	            
	            txtSongTitle.setText(songArtist + " - " + songTitle);
	            txtBPM.setText("BPM: " + songBPM);

	            File songFile = new File(song.get("songPath"));
	            MusicMetadataSet src_set = null;
	            
                //uses external api to access the mp3 ID3 tag
                try {
					 src_set = new MyID3().read(songFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                Vector v = null;
                
                //if id3 exists, populate song/artist info (not using hack job filename shit
                if (src_set !=null)
                {
                	MusicMetadata metadata = (MusicMetadata) src_set.getSimplified();
                	v = metadata.getPictureList();
                	
                }
	 
                if (v.size() > 0)
                {
                	ImageData id = (ImageData)v.get(0);
                	Bitmap b = BitmapFactory.decodeByteArray(id.imageData, 0, id.imageData.length);
                	
                	imgAlbumArt.setImageBitmap(b);

                }
                
	            // Changing Button Image to pause image
	            btnPlay.setImageResource(R.drawable.pause);
	 
	            // set Progress bar values
	            sbSong.setProgress(0);
	            sbSong.setMax(10000);
	            
	            //kicks off seekbar listener
	            mHandler.postDelayed(mUpdateTimeTask, 500);
	            currentStep = 0; // reset current steps
	            currentSongProgress = 0; //reset how long song has been playing
	            
	            // Updating progress bar
	            //updateProgressBar();
	        } catch (IllegalArgumentException e) {
	            e.printStackTrace();
	        } catch (IllegalStateException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	  private void getAccelerometer(SensorEvent event) {
	    float[] values = event.values;
	    // Movement
	    float x = values[0];
	    float y = values[1];
	    float z = values[2];

	    float accelationSquareRoot = (x * x + y * y + z * z)
	        / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);    
	    
	    //we've got 2g's record this value
	    if (accelationSquareRoot >= 2 && over2G != true) //
	    {
	      
	      //don't update the beats if the song is paused
	      if (mediaPlayer.isPlaying())
	      {
	         currentStep++;
	      }

	      over2G = true;
	    }
	    else if (accelationSquareRoot < .75)
	    {
	    	over2G = false;
	    }
	  }

	@Override
	public void onCompletion(MediaPlayer mp) {
        playSong();
	}
	
	/**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 500);
    }  
 
    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
           public void run() {
               long totalDuration = mediaPlayer.getDuration();
               long currentDuration = mediaPlayer.getCurrentPosition();
               double SPM; //steps per minute
 
               // Displaying Total Duration time
               txtDuration.setText(""+miscFunctions.milliSecondsToTimer(totalDuration));
               // Displaying time completed playing
               txtTimeElapsed.setText(""+miscFunctions.milliSecondsToTimer(currentDuration));
 
               // Updating progress bar
               int progress = (int)(miscFunctions.getProgressPercentage(currentDuration, totalDuration));
               //Log.d("Progress", ""+progress);
               sbSong.setProgress(progress);
               
               //don't increment current song duration or steps per minute if the song is paused
               if (mediaPlayer.isPlaying())
               {
                  //increment current song progress by 100ms, this is more accurate than using getDuration, incase user seeks in song.
                  currentSongProgress = currentSongProgress + 500;
                  SPM = ((double)currentStep / (double)(currentSongProgress / 1000d)) * 60d ;
	              txtSPM.setText(String.valueOf((int)Math.floor(SPM)));
               }
 
               // Running this thread after 100 milliseconds
               mHandler.postDelayed(this, 500);
               
           }
        };
      
        
    /**
     * not used for seekbar
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
 
    }
 
    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }
 
    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = miscFunctions.progressToTimer(seekBar.getProgress(), totalDuration);
 
        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);
 
        // update timer progress again
        updateProgressBar();
    }
	
   
}
