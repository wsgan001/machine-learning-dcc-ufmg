package br.ufmg.dcc.lac.lam.utility;

import java.util.List;

import br.ufmg.dcc.lac.Itemset;

public class EntropyUtility extends UtilityFunction{

	private static final long serialVersionUID = 560311519945969789L;

	double entropy = -1;
	
	@Override
	public double utility(Itemset item) {
		double entropy = 0;
		
		final int N = super.itemsFrequency.size();
		final List<Integer> itemIds = item.featureIds;
		
		double itemFrequency;
		double prob;
		for(int itemId : itemIds){
			itemFrequency = super.itemsFrequency.get(itemId);
			prob = itemFrequency/N;
			entropy += prob * Math.log(prob);
		}
		
		entropy = - entropy;

		return entropy;
	}
	@Override
	public int ordering() {
		return -1;
	}

}
