package NewClient;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.WeakHashMap;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import org.apache.commons.io.IOUtils;
public class ClientSoundManager {

	private static String OS = System.getProperty("os.name").toLowerCase();
    private static String DEFAULT_SOUND_PATH = System.getProperty("user.dir") + (OS.indexOf("mac")>=0?"/ISTCG/data/sound/":"/data/sound/");
	public static final int MAX_PLAYERS = 5;
	public static ClientSoundManager m_Instance;
	public static ArrayList<WeakReference<Player>> players; //List of players
	public static WeakHashMap<String, Byte[]> cache; //cache for faster access of sounds
	public ClientSoundManager(){
		players = new ArrayList<WeakReference<Player>>();
		cache = new WeakHashMap<String, Byte[]>();
	}
	public static byte[] tobytes(Byte[] b){
		byte[] tmp = new byte[b.length];
		for(int i=0;i<tmp.length;i++){
			tmp[i]=b[i].byteValue();
		}
		return tmp;
	}
	public static Byte[] toBytes(byte[] b){
		Byte[] tmp = new Byte[b.length];
		for(int i=0;i<tmp.length;i++){
			tmp[i]=new Byte(b[i]);
		}
		return tmp;
	}
	public static ClientSoundManager get(){
		if(m_Instance==null)
			m_Instance = new ClientSoundManager();
		return m_Instance;
	}
	
	public static Byte[] readStream(InputStream in){
		try {
			return toBytes(IOUtils.toByteArray(in));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param fileName
	 * File to be loaded
	 */
	public void play(String prefileName){
		//check if the file is in our cache
		InputStream in = null;
		String fileName = DEFAULT_SOUND_PATH+prefileName;
		if(cache.containsKey(fileName)){
			in = new ByteArrayInputStream(tobytes(cache.get(fileName)));
		} else{
			//not in cache, load file and toss in cache
			try {
				in = new BufferedInputStream(new FileInputStream(fileName));
				//add to cache
				cache.put(fileName, readStream(in));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//continue with "in" for playing file
		try {
			Player tmp = new Player(in);
			//drop player in ArrayList to dodge garbage collection, but make availible for memory release
			players.add(new WeakReference<Player>(tmp));
			new Thread(){
				public void run(){
					try {
						players.get(players.size()-1).get().play(); //
					} catch (JavaLayerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch(NullPointerException e){
						System.err.println("Unsuitable heap size, please fix.");
						e.printStackTrace();
					}
				}
			}.run();
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}
