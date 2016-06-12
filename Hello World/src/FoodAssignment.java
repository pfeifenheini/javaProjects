import java.util.*;

public class FoodAssignment {
	private Map<String, Set<String>> foodMap = new HashMap<String, Set<String>>();
	
	public FoodAssignment() {
		
		Set<String> nahrung = new HashSet<String>();
		nahrung.add("�pfel");
		nahrung.add("Bananen");
		nahrung.add("Kiwis");
		nahrung.add("Oliven");
		nahrung.add("Sardellen");
		foodMap.put("Max", nahrung);
		
		nahrung = new HashSet<String>();
		
		nahrung.add("Birnen");
		nahrung.add("Kapern");
		nahrung.add("Orangen");
		nahrung.add("�pfel");
		nahrung.add("Oliven");
		foodMap.put("Anton", nahrung);
		
		nahrung = new HashSet<String>();
		
		nahrung.add("Spargel");
		nahrung.add("Sardellen");
		nahrung.add("Bananen");
		nahrung.add("Hering");
		foodMap.put("Bernd", nahrung);
		
		nahrung = new HashSet<String>();
		
		nahrung.add("Bananen");
		nahrung.add("Sellerie");
		nahrung.add("Kiwis");
		foodMap.put("Klaus", nahrung);
		
		nahrung = new HashSet<String>();
		
		nahrung.add("Trauben");
		nahrung.add("�pfel");
		nahrung.add("Sardellen");
		nahrung.add("Paprike");
		foodMap.put("Richard", nahrung);
		
		nahrung = new HashSet<String>();
		
		nahrung.add("M�hren");
		foodMap.put("Hasi", nahrung);
	}
	
	public Map<String,Set<String>> transpose() {
		Map<String, Set<String>> foodConsumption = new HashMap<String, Set<String>>(); // Initialisiere die Map zur R�ckgabe
		
		for(String name : foodMap.keySet()) { // �ber alle Schl�ssel laufen
			for(String essen : foodMap.get(name)) { // �ber alle Elemente der Liste zu einem Schl�ssel laufen
				if(foodConsumption.containsKey(essen)) { // Gibt es in der Map schon den Schl�ssel zum aktuellen Essen
					foodConsumption.get(essen).add(name); // F�ge den Namen zur Liste des aktuellen Essens hinzu
				}
				else { // Das Aktuelle Essen befindet sich noch nicht als Schl�ssel in der Map
					Set<String> tmpSet = new HashSet<String>(); // Erstelle eine Neue Liste f�r die Namen
					tmpSet.add(name); // F�ge den Aktuellen Namen zu dieser Liste hinzu
					foodConsumption.put(essen, tmpSet); // F�ge den Aktuellen Schl�ssel und die Neue Liste zur Map hinzu
				}
			}
		}
		
		return foodConsumption;
	}
	
	public void print(Map<String, Set<String>> foodConsumption) {
		String ausgabe = "[ "; // Ausgabestring
		for(String essen : foodConsumption.keySet()) { // �ber alle Schl�ssel
			for(String name : foodConsumption.get(essen)) { // �ber alle Elemente der Liste zum Schl�ssel
				ausgabe += name + " "; // Ausgabe um Namen erg�nzen
			}
			ausgabe += "] : [ " + essen + " ]"; // Ausgabe um Essen erg�nzen
			System.out.println(ausgabe); // Ausgabe ausgeben 
			ausgabe = "[ "; // Ausgabesring neu setzen
		}
		System.out.println(); // Leerzeile
	}
	
	public Map<String, Set<String>> removeFood(Map<String, Set<String>> foodConsumption, String[] foodList) {
		Map<String, Set<String>> returnMap = new HashMap<String, Set<String>>();
		
		// foodConsumption wird in returnMap kopiert
		for(String essen : foodConsumption.keySet()) { // �ber alle Schl�ssel 
			Set<String> tmpSet = new HashSet<String>(); // Neues String - Set anlegen
			for(String name : foodConsumption.get(essen)) { // �ber alle Elemente zu einem Schl�ssel
				tmpSet.add(name); // Aktuellen Namen zum String Set hinzuf�gen
			}
			returnMap.put(essen, tmpSet); // return Map um aktuelles Essen und zugeh�rige Namensliste erg�nzen
		}
		
		for(int i=0;i<foodList.length;i++) { // �ber alle eintr�ge der foodList
			if(returnMap.containsKey(foodList[i])) { // Enth�lt die Map den aktuellen Schl�ssel?
				returnMap.remove(foodList[i]); // Entferne den Schl�ssel
			}
			else { // Schl�ssel ist nicht in Map enthalten
				System.out.println("Schl�ssel '" + foodList[i] + "' ist nicht enthalten"); // Warnmeldung
			}
		}
		System.out.println(); // Leerzeile
		return returnMap; // Bereinigkte Kopie zur�ckgeben
	}
	
	public static void main(String[] args) {
		
		FoodAssignment a = new FoodAssignment();
		
		Map<String,Set<String>> b = a.transpose();
		
		a.print(b);
		
		String[] noFruits = {"Sardellen", "Kapern", "Spargel", "Hering", "Essig"}; 
		
		Map<String, Set<String>> c = a.removeFood(b, noFruits);
		
		a.print(c);
	}
}