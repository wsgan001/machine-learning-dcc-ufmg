package br.ufmg.dcc.lac.lam.utility;

import java.io.Serializable;
import java.util.Map;

import br.ufmg.dcc.lac.Itemset;

public abstract class UtilityFunction implements Serializable{
	
	private static final long serialVersionUID = 1169402082827134955L;
	
	public Map<Integer,Integer> itemsFrequency;
	
	public void setDatabase(Map<Integer,Integer> itemsFrequency){
		this.itemsFrequency = itemsFrequency;
	}
	
	public abstract int ordering();
	public abstract double utility(Itemset item);
	
	
	
}
