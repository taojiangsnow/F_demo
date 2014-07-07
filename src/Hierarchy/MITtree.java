package Hierarchy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import Main.Maskit;
import Utils.Sequence;
import Context.Context;
import Context.PContext;

public class MITtree extends Tree{
	public MITtree(int i) {
		super(i);
		structure = new ArrayList<Layer>();
	}
	
	public void initial(ArrayList<Sequence> s) {		
		HashMap<Integer,HashSet<Integer>> cell_tower_set = new HashMap<Integer,HashSet<Integer>>(); //store different contextes of each cell
		HashMap<Integer,ArrayList<Context>> cell_context_list = new HashMap<Integer, ArrayList<Context>>(); //store all contextes of each cell
		HashSet<Integer> diff_cell = new HashSet<Integer>();
		HashSet<Integer> diff_tower = new HashSet<Integer>();
		
		Sequence sq;
		Context c;
		Iterator<Integer> it;
		double[][][] t_tower_cell;
		double[][] t_tower;
		int cell,tower;
		
		structure = new ArrayList<Layer>();
		structure.add(new Layer(1)); // the bottom layer 
		Layer singlelayer = new Layer(2);
		structure.add(singlelayer);
		
		for (int i = 0; i < s.size(); i++) {
			sq = s.get(i);
			for (int j = 0; j < sq.getLength(); j++) {
				c = sq.getContext(j);
				cell = c.getCell();
				tower = c.getTower();
				//c.output();
				
				diff_cell.add(cell);
				diff_tower.add(tower);
				//System.out.println("  cell "+cell+" tower "+tower);
				
				if (!cell_tower_set.containsKey(cell)) {
					HashSet<Integer> set = new HashSet<Integer>();
					ArrayList<Context> array = new ArrayList<Context>();
					set.add(cell);
					array.add(c);
					cell_tower_set.put(cell, set);
					cell_context_list.put(cell, array);
				} else {
					cell_tower_set.get(cell).add(tower);
					cell_context_list.get(cell).add(c);
				}
			}
		}
		
		output(cell_tower_set);
		
		/*transfer the hashset to array*/
		ArrayList<Integer> cell_list = new ArrayList<Integer>(); 
		ArrayList<Integer> tower_list = new ArrayList<Integer>();

		it = diff_cell.iterator();
		while (it.hasNext()) {
			int key = it.next();
			cell_list.add(key);
		}
		it = diff_tower.iterator();
		while (it.hasNext()) {
			int key = it.next();
			tower_list.add(key);
		}
		
		setInnerNodeNum(cell_list.size());
		t_tower_cell = new double[Maskit.T][cell_list.size()+2][tower_list.size()+2]; //include "start" and "end" state
		t_tower = new double[Maskit.T][tower_list.size()+2];
		
		PContext pc;
		for (int i = 0; i < s.size(); i++) {
			sq = s.get(i);
			for (int j = 0; j < sq.getLength(); j++) {
				c = sq.getContext(j);
				pc = new PContext(null,c.getTimetmp(),c.getCell(),cell_list.indexOf(c.getCell())+1,2,c.getT());
				
				singlelayer.add(pc);
				
				/*construct childern of the parent*/
				//System.out.println(cell_context_list.get(c.getCell())==null);
				pc.setChildren(cell_context_list.get(c.getCell())); 
				
				c.setParent(pc);
				
				/*set the index in each parent's domain*/
				c.setHierarchyIndex(pc.findChild(c));
				
				c.output();
				System.out.println();
				
				if ((c.getT() != 0) && (c.getT() != Maskit.T)) {
					t_tower_cell[c.getT()][pc.getHierarchyIndex()][c.getHierarchyIndex()] += 1.0;
					t_tower[c.getT()][pc.getHierarchyIndex()] += 1.0;
				}
			}
		}
		
		/*sort PContext of the layer accroding to the date*/
		singlelayer.quickSortChildrenByT(0, singlelayer.getPcList().size()-1);
		
		System.out.println("*********************************************************");
		
		for (int i = 0; i < s.size(); i++) {
			sq = s.get(i);
			for (int j = 0; j < sq.getLength(); j++) {
				c = sq.getContext(j);
				pc = c.getParent();
				
				c.output();
				System.out.print("          "+pc.getHierarchyIndex()+" "+pc.getT());
				System.out.println();
				System.out.println(" children of "+" "+pc.getHierarchyIndex()+" "+pc.getT());
				pc.outputChildren();
			}
		}
		
		//outputStructure();
		
		/*get the probability of each tower in the cell*/
		for (int i = 0; i < s.size(); i++) {
			sq = s.get(i);			
			for (int j = 0; j < sq.getLength(); j++) {
				c = sq.getContext(j);
				pc = c.getParent();
				
				if ((c.getT() != 0) && (c.getT() != Maskit.T)) {
					if (t_tower[c.getT()][pc.getHierarchyIndex()] != 0) {
						//System.out.println(c.getT()+" "+pc.getHierarchyIndex()+" "+c.getHierarchyIndex());
						c.setProbToParent(t_tower_cell[c.getT()][pc.getHierarchyIndex()][c.getHierarchyIndex()]/t_tower[c.getT()][pc.getHierarchyIndex()]);
					}
				} else {
					c.setProbToParent(1.0);
				}
			}
		}
		
		/*
		it = cell_tower_list.keySet().iterator();
		while (it.hasNext()) {
			int key = it.next();
			System.out.println("******cell "+key+" *********");
			ArrayList<Integer> list = cell_tower_list.get(key);
			for (int j = 0; j < list.size(); j++) {
				System.out.print(list.get(j)+" ");
			}
			System.out.println();
		}
		*/	
	}
	
	public void output(HashMap<Integer,HashSet<Integer>> set) {
		Iterator<Integer> it = set.keySet().iterator();
		while (it.hasNext()) {
			int cell = it.next();
			System.out.println("******cell "+cell+" ******");
			int num = 0;
			for (Integer i: set.get(cell)) {
				if (num % 10 == 0) {
					System.out.println();
				}
				System.out.print(i+" ");
				num++;
			}
			System.out.println();
			System.out.println();
		}
	}

	@Override
	public void setInnerNodeNum(int i) {
		inner_node_num = i;
	}
	
	public void outputStructure() {
		Layer l;
		PContext pc;
		for (int i = 0; i < layers; i++) {
			System.out.println("*************layer "+i+"*********************");
			l = structure.get(i);
			Iterator<PContext> it = l.getPcList().iterator();
			while (it.hasNext()) {
				pc = it.next();
				System.out.println("***pc "+pc.getHierarchyIndex()+" T "+pc.getT()+"***********************");
				pc.outputChildren();
				System.out.println();
			}
			
		}
	}

	@Override
	public void output() {
		// TODO Auto-generated method stub
	}
}

