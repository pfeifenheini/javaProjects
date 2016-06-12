import java.util.*;

public class FoodAssignment {
	private Map<String, Set<String>> foodMap = new HashMap<String, Set<String>>();
	
	public FoodAssignment() {
		
		Set<String> nahrung = new HashSet<String>();
		nahrung.add("Äpfel");
		nahrung.add("Bananen");
		nahrung.add("Kiwis");
		nahrung.add("Oliven");
		nahrung.add("Sardellen");
		foodMap.put("Max", nahrung);
		
		nahrung = new HashSet<String>();
		
		nahrung.add("Birnen");
		nahrung.add("Kapern");
		nahrung.add("Orangen");
		nahrung.add("Äpfel");
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
		nahrung.add("Äpfel");
		nahrung.add("Sardellen");
		nahrung.add("Paprike");
		foodMap.put("Richard", nahrung);
		
		nahrung = new HashSet<String>();
		
		nahrung.add("Möhren");
		foodMap.put("Hasi", nahrung);
	}
	
	public Map<String,Set<String>> transpose() {
		Map<String, Set<String>> foodConsumption = new HashMap<String, Set<String>>(); // Initialisiere die Map zur Rückgabe
		
		for(String name : foodMap.keySet()) { // Über alle Schlüssel laufen
			for(String essen : foodMap.get(name)) { // Über alle Elemente der Liste zu einem Schlüssel laufen
				if(foodConsumption.containsKey(essen)) { // Gibt es in der Map schon den Schlüssel zum aktuellen Essen
					foodConsumption.get(essen).add(name); // Füge den Namen zur Liste des aktuellen Essens hinzu
				}
				else { // Das Aktuelle Essen befindet sich noch nicht als Schlüssel in der Map
					Set<String> tmpSet = new HashSet<String>(); // Erstelle eine Neue Liste für die Namen
					tmpSet.add(name); // Füge den Aktuellen Namen zu dieser Liste hinzu
					foodConsumption.put(essen, tmpSet); // Füge den Aktuellen Schlüssel und die Neue Liste zur Map hinzu
				}
			}
		}
		
		return foodConsumption;
	}
	
	public void print(Map<String, Set<String>> foodConsumption) {
		String ausgabe = "[ "; // Ausgabestring
		for(String essen : foodConsumption.keySet()) { // Über alle Schlüssel
			for(String name : foodConsumption.get(essen)) { // Über alle Elemente der Liste zum Schlüssel
				ausgabe += name + " "; // Ausgabe um Namen ergänzen
			}
			ausgabe += "] : [ " + essen + " ]"; // Ausgabe um Essen ergänzen
			System.out.println(ausgabe); // Ausgabe ausgeben 
			ausgabe = "[ "; // Ausgabesring neu setzen
		}
		System.out.println(); // Leerzeile
	}
	
	public Map<String, Set<String>> removeFood(Map<String, Set<String>> foodConsumption, String[] foodList) {
		Map<String, Set<String>> returnMap = new HashMap<String, Set<String>>();
		
		// foodConsumption wird in returnMap kopiert
		for(String essen : foodConsumption.keySet()) { // Über alle Schlüssel 
			Set<String> tmpSet = new HashSet<String>(); // Neues String - Set anlegen
			for(String name : foodConsumption.get(essen)) { // Über alle Elemente zu einem Schlüssel
				tmpSet.add(name); // Aktuellen Namen zum String Set hinzufügen
			}
			returnMap.put(essen, tmpSet); // return Map um aktuelles Essen und zugehörige Namensliste ergänzen
		}
		
		for(int i=0;i<foodList.length;i++) { // Über alle einträge der foodList
			if(returnMap.containsKey(foodList[i])) { // Enthält die Map den aktuellen Schlüssel?
				returnMap.remove(foodList[i]); // Entferne den Schlüssel
			}
			else { // Schlüssel ist nicht in Map enthalten
				System.out.println("Schlüssel '" + foodList[i] + "' ist nicht enthalten"); // Warnmeldung
			}
		}
		System.out.println(); // Leerzeile
		return returnMap; // Bereinigkte Kopie zurückgeben
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