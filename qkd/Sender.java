package qkd;

import java.util.ArrayList;
import java.util.List;

public class Sender extends Participant{
	private List<Integer> siftedKey;
	
	public Sender(String name) {
		super(name);
		siftedKey = new ArrayList<Integer>();
	}
	
	public void addToSiftedKey(int val) {
		siftedKey.add(val);
	}
	
	public List<Integer> getSiftedKey(){
		return siftedKey;
	}
	
	public String getSiftedKeyString() {
		StringBuilder builder = new StringBuilder();
		builder.append('[');
		for(int val : siftedKey) {
			if(val < 0) {
				builder.append("- ");
			}else {
				builder.append(val + " ");
			}
		}
		builder.setCharAt(builder.length() - 1, ']');
		return builder.toString();
	}
	
	@Override
	public String toString() {
		return super.toString() + "\n" + getSiftedKeyString();
	}
}
