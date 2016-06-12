
public class SortetList {
	ListElement first;
	
	SortetList() {
		first = null;
	}
	
	SortetList(int length) {
		if(length <= 0) {
			first = null;
			return;
		}
		first = new ListElement(1);
		ListElement temp = first;
		for(int i=2;i<=length;i++) {
			temp.next = new ListElement(i,temp);
			temp = temp.next;
		}
	}
	
	public int pop(int index) {
		ListElement temp = first;
		for(int i=1;i<index;i++) {
			if(temp == null) {
				System.out.println("Fehler!");
				System.exit(1);
			}
			temp = temp.next;
		}
		if(temp.next == null) {
			if(temp.prev == null) {
				first = null;
				return temp.entry;
			}
			else {
				temp.prev.next = null;
				return temp.entry;
			}
		}
		else{
			if(temp.prev == null) {
				first = temp.next;
				temp.next.prev = null;
				return temp.entry;
			}
			else {
				temp.prev.next = temp.next;
				temp.next.prev = temp.prev;
				return temp.entry;
			}
		}
	}
	
	public String toString() {
		if(first == null) return "Liste is leer!";
		String returnString = "";
		returnString = returnString + "Liste = [ ";
		ListElement temp = first;
		while(temp.next != null) {
			returnString = returnString + temp.entry + ", ";
			temp = temp.next;
		}
		returnString = returnString + temp.entry + " ]";
		return returnString;
	}
	
	public int getLength() {
		int length = 0;
		ListElement temp = first;
		while(temp != null) {
			temp = temp.next;
			length++;
		}
		return length;
	}
	
	private class ListElement {
		int entry;
		ListElement next;
		ListElement prev;
		
		ListElement(int entry) {
			this.entry = entry;
			next = null;
			prev = null;
		}
		
		ListElement(int entry, ListElement prev) {
			this.entry = entry;
			this.prev = prev;
			next = null;
		}
	}
}
