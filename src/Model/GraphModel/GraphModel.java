package Model.GraphModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import Main.Maskit;
import Model.Model;
import Model.MarkovModel.*;
import SuppressionVector.SupVec;
import SuppressionVector.SupVec.PartialOutSeq;
import Utils.Sequence;
import Context.PContext;
import Hierarchy.Hierarchy;
import Hierarchy.Layer;

public class GraphModel extends Model{
	private ArrayList<Model> each_layer_transition; //for each layer of PContexts
	private HashMap<Integer,ArrayList<Model>> each_layer_inner_transition; //for each innernode's children
	private Hierarchy h;
	
	public GraphModel(int cycle, ArrayList<Sequence> s, Hierarchy hi) {
		super(cycle,s);
		h = hi;
		each_layer_transition = new ArrayList<Model>();
		each_layer_inner_transition = new HashMap<Integer,ArrayList<Model>>();
	}

	/*two parts:
	 * 1:train each_layer model
	 * 2:train bottom layer
	 * */
	public void train() {
		/*part 1*/
		Layer l;
		Model lm,im;
		Iterator<Layer> it = Hierarchy.structure.iterator();
		Iterator<PContext> ip;
		PContext pc;
		
		while (it.hasNext()) {
			l = it.next();
			if (l.getLayerIndex() != 1) { //skip the bottom layer
				lm = new FirstOrderMarkovModel(l.getPcList(),Maskit.T);
				((FirstOrderMarkovModel)lm).setMN(l.getMaxPcIndex()+2); //include "start" and "end"
				lm.trainForLayer();
				
				each_layer_transition.add(lm);
				
				each_layer_inner_transition.put(l.getLayerIndex(), new ArrayList<Model>()); //include "start" and "end"
				
				each_layer_inner_transition.get(l.getLayerIndex()).add(null);
				for (int i = 1; i < l.getMaxPcIndex()+1; i++) {
					each_layer_inner_transition.get(l.getLayerIndex()).add(new FirstOrderMarkovModel());
				}
				each_layer_inner_transition.get(l.getLayerIndex()).add(null);
				
				System.out.println("each_layer_inner_transition"+each_layer_inner_transition.get(l.getLayerIndex()).size());
				/*part 2*/
				ip = l.getPcList().iterator();
				HashSet<Integer> phindexlist = new HashSet<Integer>();
				while (ip.hasNext()) {
					pc = ip.next();
					
					/*we store one model of PContexts that have the same index, different T*/
					if (phindexlist.contains(pc.getHierarchyIndex())) {
						continue;
					}
					
					im = new FirstOrderMarkovModel();
					im.setContextList(pc.getChildren());
					((FirstOrderMarkovModel)im).setT(Maskit.T);
					((FirstOrderMarkovModel)im).setMN(pc.getMaxChild()+2);
					
					System.out.print("pc.hindex "+pc.getHierarchyIndex()+" "+" children.size "+(pc.getMaxChild()+2));
					im.trainForCList();
					//System.out.println("sdf"+each_layer_inner_transition.get(l.getLayerIndex()).size());
					each_layer_inner_transition.get(l.getLayerIndex()).set(pc.getHierarchyIndex(), im);
					
					phindexlist.add(pc.getHierarchyIndex());
				}
			} else { // for bottom layer
				each_layer_transition.add(null);
			}
		}
	}	
	
	@Override
	public double getProb(int t, int index, int start, int index2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPriorProb(int t, int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPosterior(int index, int t, Sequence pos, int cj) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPosterior(int index, int t, PartialOutSeq s, SupVec sv) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPosterior(int t, int index, int start, int index1,
			int end, int index2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int[] Reached(int t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] Reached(int t, int index1, int step) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void trainForLayer() {

	}
	
	public Model getLayerTransition(int i) {
		return each_layer_transition.get(i);
	}

	public void setHierarchy(Hierarchy i) {
		h = i;
	}
	
	public Hierarchy getHierarchy() {
		return h;
	}

	@Override
	public void trainForCList() {
		// TODO Auto-generated method stub
		
	}

	public void output() {
		Iterator<Integer> it = each_layer_inner_transition.keySet().iterator();
		ArrayList<Model> mlist;
		Iterator<Model> it2;
		int indexlayer;
		Model m;
		
		while (it.hasNext()) {
			indexlayer = it.next();
			
			if (indexlayer == 1) {
				continue;
			}
			
			mlist = each_layer_inner_transition.get(indexlayer);
			
			it2 = mlist.iterator();
			
			System.out.println("*************layer "+indexlayer+" transition "+"**************************");
			((MarkovModel)each_layer_transition.get(indexlayer-1)).output();
			
			System.out.println("*************layer "+indexlayer+" inner_transition "+"**************************");
			int index2 = 0;
			while (it2.hasNext()) {
				m = it2.next(); 
				if (m == null) {
					index2++;
					continue;
				}

				System.out.println("**********PC "+index2);
				((MarkovModel)m).output();
				index2++;
			}
		}		
	}
}
