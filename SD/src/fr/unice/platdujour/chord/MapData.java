package fr.unice.platdujour.chord;

/**
 * Classe representant la structure de donnees utilisee pour representer le couple restaurant-plat du jour
 * @author Laurent
 *
 */

public class MapData{

	String restaurant;
	String dailySpecial;
	
	public MapData(String a, String b){
		this.restaurant = a;
		this.dailySpecial = b;
	}

	/**
	 * 
	 * @return Renvoie une String contenant le restaurant d'une donnee
	 */
	public String getRestaurant() {
		return restaurant;
	}

	/**
	 * 
	 * @return Renvoie une String contenant le plat du jour d'une donnee
	 */
	public String getDailySpecial() {
		return dailySpecial;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dailySpecial == null) ? 0 : dailySpecial.hashCode());
		result = prime * result
				+ ((restaurant == null) ? 0 : restaurant.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapData other = (MapData) obj;
		if (dailySpecial == null) {
			if (other.dailySpecial != null)
				return false;
		} else if (!dailySpecial.equals(other.dailySpecial))
			return false;
		if (restaurant == null) {
			if (other.restaurant != null)
				return false;
		} else if (!restaurant.equals(other.restaurant))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MapData [restaurant=" + restaurant + ", dailySpecial="
				+ dailySpecial + "]";
	}

}
