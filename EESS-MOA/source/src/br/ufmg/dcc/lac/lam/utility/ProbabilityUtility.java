package br.ufmg.dcc.lac.lam.utility;

import java.util.List;

import br.ufmg.dcc.lac.Itemset;

public class ProbabilityUtility extends UtilityFunction{

	private static final long serialVersionUID = 1L;

	@Override
	public double utility(Itemset item) {
		double probability = 1;
		
		final List<Integer> itemIds = item.featureIds;

		double itemFrequency;
		for(int itemId : itemIds){
			itemFrequency = super.itemsFrequency.get(itemId);
			probability *= itemFrequency;
		}
		
		return probability;

	}

	@Override
	public int ordering() {
		return -1;
	}
}
