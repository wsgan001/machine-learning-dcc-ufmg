package br.ufmg.dcc.lac.lam.utility;

import br.ufmg.dcc.lac.Itemset;

public class LengthFrequencySpace extends UtilitySpace{

	private static final long serialVersionUID = -8039256602985753980L;

	@Override
	public double[] getDimensionValues(Itemset item) {
		return new double[]{item.getSupport(), item.getSize()};
	}

}
