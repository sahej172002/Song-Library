package comparator;

import java.util.Comparator;
import objects.Song;

/**
 * @author      Sahej Bansal
 * @author		Arian Baoas
 */

public class SongComparator implements Comparator<Song> {
	
	@Override
	public int compare(Song song1, Song song2) {
		
		int val = 0;
		val = song1.getTitle().toLowerCase().compareTo(song2.getTitle().toLowerCase());
		if(val == 0) {
			val = song1.getArtist().toLowerCase().compareTo(song2.getArtist().toLowerCase());
		}
		return val;
	}
	
}
