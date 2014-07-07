/*
 * @author xuejiang
 * 2014.6.25
 * 
 * */

package Hierarchy;

import java.util.ArrayList;
import java.util.Iterator;

import Context.PContext;

public class Layer {
	private int layer_index;
	private ArrayList<PContext> s;
	
	public Layer() {
		s = new ArrayList<PContext>();
	}
	
	public Layer(int i) {
		layer_index = i;
		s = new ArrayList<PContext>();
	}
	
	public void add(PContext p) {
		s.add(p);
	}

	public void trainModel() {
		
	}
	
	public ArrayList<PContext> getPcList() {
		return s;
	}
	
	public int getLayerIndex() {
		return layer_index;
	}
	
	public int getMaxPcIndex() {
		int max = s.get(1).getHierarchyIndex(); //skip
		Iterator<PContext> it = s.iterator();
		PContext pc;
		
		while (it.hasNext()) {
			pc = it.next();
			if (pc.getHierarchyIndex() > max) {
				max = pc.getHierarchyIndex();
			}
		}
		
		return max;
	}
	
	public void quickSortChildrenByT(int begin, int end) {
		if (begin >= end) return;
		int privot = partition(s, begin, end);
		
		quickSortChildrenByT(begin, privot-1);
		quickSortChildrenByT(privot+1,end);
		
		
	}

    private int partition(ArrayList<PContext> input, int begin, int end) {
        PContext x = input.get(begin);
        int smallIndex = begin, bigIndex = begin;
        while(++bigIndex <= end) {
            if (input.get(bigIndex).getDate().compare(x.getDate()) >= 0) continue;
            else {
                smallIndex++;
                swap(input, smallIndex, bigIndex);
            }            
        }
        swap(input, begin, smallIndex);
        return smallIndex;
    }

    private void swap(ArrayList<PContext> input, int lhs, int rhs) {
        PContext temp = input.get(lhs);
        input.set(lhs, input.get(rhs));
        input.set(rhs, temp);
    }
}

