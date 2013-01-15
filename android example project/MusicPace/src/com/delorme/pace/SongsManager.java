//so this only finds mp3's in the base directory, not in /music or /amazon
package com.delorme.pace;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import android.os.Environment;
import android.util.Log;
 
public class SongsManager {
    // SDCard Path
    //final String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath(); 
    //final String MEDIA_PATH = "/mnt/sdcard/music";
    final String MEDIA_PATH = "/mnt/sdcard/amazonmp3/testing";
		
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    
    private HashMap<String, ArrayList<HashMap<String, String>>> songsList2 = new HashMap<String, ArrayList<HashMap<String, String>>>();
    private ArrayList<HashMap<String, String>> orderedSongList = new ArrayList<HashMap<String, String>>();
    private ArrayList<HashMap<String, String>> previousSongs = new ArrayList<HashMap<String, String>>();
 
    // Constructor
    public SongsManager(){
 
    }
 
    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     * */
    public ArrayList<HashMap<String, String>> getPlayList(){
        File home = new File(MEDIA_PATH);
        MusicMetadataSet src_set = null;
 
        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());
 
                //uses external api to access the mp3 ID3 tag
                try {
					 src_set = new MyID3().read(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                //if id3 exists, populate song/artist info (not using hack job filename shit
                if (src_set !=null)
                {
                	MusicMetadata metadata = (MusicMetadata) src_set.getSimplified();
                	song.put("songTitle", metadata.getSongTitle());
                	song.put("songArtist", metadata.getArtist());
                	song.put("songBPM", metadata.getComposer());

                }
                
                // Adding each song to SongList
                songsList.add(song);
            }
        }
        // return songs list array
        return songsList;
    }
    
    
    public HashMap<String, ArrayList<HashMap<String, String>>> getPlayList2()
    {
        File home = new File(MEDIA_PATH);
        MusicMetadataSet src_set = null;
        String BPM_FLOOR;
        ArrayList<HashMap<String, String>> songArray;
        HashMap<String, String> song;
 
        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());
 
                //uses external api to access the mp3 ID3 tag
                try {
					 src_set = new MyID3().read(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                //if id3 exists, populate song/artist info (not using hack job filename shit
                if (src_set !=null)
                {
                	MusicMetadata metadata = (MusicMetadata) src_set.getSimplified();
                	song.put("songTitle", metadata.getSongTitle());
                	song.put("songArtist", metadata.getArtist());
                	song.put("songBPM", metadata.getComposer());
                    BPM_FLOOR = String.valueOf(Integer.parseInt(metadata.getComposer()) / 10);
                    
                    
                    songArray = songsList2.get(BPM_FLOOR);
                    
                    //songsList2.
                    
                    //If no other data has been entered at this BPM we need to create the arraylist, then add the song
                    if (songArray == null)
                    {
                    	Log.v("stuff", "new entry for this BPM : " + BPM_FLOOR);
                    	songArray = new ArrayList<HashMap<String, String>>();
                    	songArray.add(song);
                    	songsList2.put(BPM_FLOOR, songArray);
                    }
                    //Otherwise just add the song to the existing list
                    else
                    {
                    	Log.v("stuff", "added entry at this BPM : " + BPM_FLOOR);
                    	songArray.add(song);                   	
                    }
                    
                    //add song to orderedlist (makes getting a random one much easier)
                    orderedSongList.add(song);
                }
            }
        }
        // return songs list array
        return songsList2;
    }
    
    public HashMap<String, String> getSongBySPM(int SPM)
    {
    	int SPMFloor;
    	ArrayList<HashMap<String, String>> songArrayPlus;
    	ArrayList<HashMap<String, String>> songArrayMinus;
    	ArrayList<HashMap<String, String>> songArray;
    	HashMap<String, String> song;
        Random random = new Random(); 

    	//you will at least see this happen for the first song played
    	if (SPM <= 0)
    	{
    		Log.v("stuff", "I used random");
			song = getRandomSong();
			previousSongs.add(song);
			return song;
    	}
   	
    	SPMFloor = SPM / 10;
    	Log.v("stuff", "didn't random my floor BPM was :" + String.valueOf(SPMFloor));
    	songArray = songsList2.get(String.valueOf(SPMFloor));
    	
    	//if there is no entry, go looking for one
    	if (songArray == null)
    	{
    		//try +/- 1
    		for (int i = 1; ; i++)
    		{
    			songArrayPlus = songsList2.get(String.valueOf(SPMFloor +i));
    			songArrayMinus = songsList2.get(String.valueOf(SPMFloor - i));
    			   			
    			//look 10 above and take it (prefer this option over the chance of hitting 0)
    			if (songArrayPlus != null)
    			{   
    				song = songArrayPlus.get(random.nextInt(songArrayPlus.size()));
    				previousSongs.add(song);
     	            return song;
    			}

    			//if we've hit 0 BPM, just get a random song
    			if ((SPMFloor - i) <= 0)
    			{
    				Log.v("stuff", "I randomed, because I somehow went under 0");
    				song = getRandomSong();
    				previousSongs.add(song);
    				return song;
    			}
    			
    			//look 10 below and take it 
    			if (songArrayMinus != null)
    			{   
    				song = songArrayMinus.get(random.nextInt(songArrayMinus.size()));
    				previousSongs.add(song);
     	            return song;
    			}
    		}   		
    	}
    	//otherwise take a random entry at this BPM
    	else
    	{          
    		Log.v("stuff", "I went where I was supposed to");

			song = songArray.get(random.nextInt(songArray.size()));
			previousSongs.add(song);
	        return song;
    	}
    }

    public HashMap<String, String> getPreviousSong()
    {
    	//always leave the first song in the list
    	if (previousSongs.size() == 1)
    		return previousSongs.get(0);

    	//remove last song (its the one that is playing right now, then go back to the last one we played
    	previousSongs.remove(previousSongs.size()-1);
    	return previousSongs.get(previousSongs.size()-1);
    }
    
//    public HashMap<String, String> getSongBySPMR(HashMap<String, String> song, int SPM)
    
    public HashMap<String, String> getRandomSong()
    {
        Random random = new Random();     
        return orderedSongList.get(random.nextInt(orderedSongList.size()));
    }
    
 
    /**
     * Class to filter files which are having .mp3 extension
     * */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
