package fr.unice.platdujour.application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a facility to create new data to inject in a {@link GuideMichelin}.
 * A call to getNewData will always produce new data, or updated data.
 */
public class DataGenerator {
	
	/** Name of the file that contains the dish of restaurants */
	private static final String DATA_FILENAME = "src/data";
	
	/** String used to separate dish names from restaurant names in the file */
	private static final String SEPARATOR = ";";
	
	/** Number of new data that will be generated at each getNewData call */
	private final int batchsize;
	
	/** In memory version of all (restaurant, daily special) couples */
	private Map<String, String> entries;
	
	/** List of all restaurants */
	private List<String> restaurants;
	
	/** Current index of new data */
	private int pointer;
	
	
	public DataGenerator(int batchSize) {
		this.entries = new HashMap<String, String>();
		this.pointer = 0;
		this.batchsize = batchSize;
		this.initialize();
	}
	
	/**
	 * Reads (restaurant, daily special) couples from file and store them in memory.
	 */
	private void initialize() {
		BufferedReader reader = null;
		String line;
		String[] members;
		
		try {
			reader = new BufferedReader(new FileReader(DATA_FILENAME));
			while ((line = reader.readLine()) != null) {
				members = line.split(SEPARATOR);
				this.entries.put(members[0], members[1]);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		this.restaurants = Arrays.asList(
				this.entries.keySet().toArray(new String[0]));
	}
	
	/** 
	 * Each call to this method will return a different set of 
	 * (restaurant, daily special) couples. It reads the internal map circularly so 
	 * that it always return updated daily specials for restaurants.
	 * @return A new map from restaurants to daily specials whose size is 
	 * {@link batchSize}. 
	 */
	public Map<String, String> getNewData() {
		Map<String, String> newData = new HashMap<String, String>();
		int start = this.pointer;
		String restaurant;
		String dailySpecial;
		
		for (int i = 0 ; i < this.batchsize ; i++) {
			restaurant = this.restaurants.get((start + i) % this.entries.size());
			dailySpecial = this.entries.get(restaurant);
			newData.put(restaurant, dailySpecial);
		}
		
		this.pointer = (start + this.batchsize) % this.entries.size();
		return newData;
	}

}
