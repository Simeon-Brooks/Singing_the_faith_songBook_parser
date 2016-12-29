package me.brooks107.stfprocessor;

/**
 * Represents a single verse in a hymn
 * 
 * @author Simeon Brooks (simeon26@gmail.com)
 *
 */
public class Verse extends SongBlock {
	
	// The verse number in the hymn (e.g. verse 2)
	private int mNumberInSong;
	
	/**
	 * Creates a single verse
	 * 
	 * @param text The words of the verse (without a tag)
	 * @param numberInSong The position of the verse in the song (only counting verses)
	 */
	public Verse(String text, int numberInSong) {
		super(text);
		mNumberInSong = numberInSong;
	}
	
	/**
	 * Creates an empty verse
	 * 
	 * @param numberInSong The position of the verse in the song (only counting verses)
	 */
	public Verse(int numberInSong) {
		this(new String(), numberInSong);
	}
	
	/**
	 * Gets where the verse falls in the hymn
	 * @return The verse number
	 */
	public int getNumberInSong() {
		return mNumberInSong;
	}
	
	/**
	 * Gets the words of the verse, including the tag and the verse number with an out of part
	 */
	@Override
	public String getBlock(int totalVerses) {
		return "Verse " + mNumberInSong + " (/" + totalVerses + ")\n" + mText + "\n";
	}
}
