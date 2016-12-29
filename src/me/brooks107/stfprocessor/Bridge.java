package me.brooks107.stfprocessor;

/**
 * Represents a bridge in a hymn
 * 
 * @author Simeon Brooks (simeon26@gmail.com)
 *
 */
public class Bridge extends SongBlock {

	/**
	 * Creates a bridge with the given {@link String} as the words
	 * 
	 * @param text The text of the bridge (without a tag)
	 */
	protected Bridge(String text) {
		super(text);
	}
	
	/**
	 * Gets the block with it's tag (Bridge)
	 */
	@Override
	public String getBlock(int totalVerses) {
		return "Bridge\n" + mText + "\n";
	}
	
}
