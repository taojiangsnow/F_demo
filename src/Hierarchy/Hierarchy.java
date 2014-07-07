/*
 * @author xuejiang
 * 2014.6.4
 * */


package Hierarchy;

import java.util.ArrayList;

import Utils.Sequence;

public abstract class Hierarchy {
	public static int layers;
	public static int inner_node_num;
	public static ArrayList<Layer> structure;
	
	public abstract void setInnerNodeNum(int i);

	public abstract void initial(ArrayList<Sequence> s);

	public abstract void output();
}
