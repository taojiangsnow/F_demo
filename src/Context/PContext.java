package Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import Utils.Date;

public class PContext implements Cloneable{
	private String context;
	private Date timestamp;
	private int id;
	private int layer;
	private int T;
	private PContext parent;
	
	private ArrayList<Context> children; //store initial each context's index
	private int hierarchy_index; //the index of the children
	
	public PContext() {
	}
	
	public PContext(String s, Date time, int i, int l, int t) {
		context = s;
		timestamp = time;
		id = i;
		layer = l;
		T = t;
		parent = null;
		
	}
	
	public PContext(String s, Date time, int i, int j, int l, int t) {
		context = s;
		timestamp = time;
		id = i;
		hierarchy_index = j;
		layer = l;
		T = t;
		parent = null;
	}
	
	public void setLayer(int i) {
		layer = i;
	}

	public ArrayList<Context> getChildren() {
		return children;
	}
	
	public void addChild(Context c) {
		children.add(c);
	}
	
	public int findChild(Context c){
		ArrayList<Integer> indexset = new ArrayList<Integer>();
		Iterator<Context> it = children.iterator();
		indexset.add(0); //¡°start¡± as default first child
		
		int i;
		while (it.hasNext()) {
			i = it.next().getIndex();
			if (!indexset.contains(i)) {
				indexset.add(i);
			}
		}

		return indexset.indexOf(c.getIndex());
	}

	public void outputChildren() {
		Iterator<Context> it = children.iterator();
		while (it.hasNext()) {
			it.next().output();
			System.out.println();
		}
	}
	
	public int getT() {
		return T;
	}
	
	public void setT(int t) {
		T = t;
	}

	public int getHierarchyIndex() {
		return hierarchy_index;
	}
	
	public Date getDate() {
		return timestamp;
	}
	
	public void setChildren(ArrayList<Context> array) {
		children = new ArrayList<Context>();
		Iterator<Context> it = array.iterator();
		while (it.hasNext()) {
			children.add(it.next());
		}
	}

	public void output() {
		this.timestamp.output();
		StringBuilder builder = new StringBuilder();
		builder.append(this.T);
		builder.append(" ");
		builder.append(this.hierarchy_index);
		System.out.print(builder);
	}

	public int getMaxChild() {
		int max = children.get(0).hierarchy_index;
		Iterator<Context> it = children.iterator();
		int index;
		while (it.hasNext()) {
			index = it.next().hierarchy_index;
			if (index > max) {
				max = index;
			}
		}
		
		return max;
	}
}
