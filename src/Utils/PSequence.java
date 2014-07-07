package Utils;

import java.util.ArrayList;

import Context.PContext;

public class PSequence {
	private ArrayList<PContext> sq;
	
	public PSequence() {
		sq = new ArrayList<PContext>();
	}
	
	public void addPContext(PContext c) {
		sq.add(c);
	}
	
	public int getLength() {
		return sq.size();
	}
	
	public PContext getPContext(int i) {
		return sq.get(i);
	}
}
