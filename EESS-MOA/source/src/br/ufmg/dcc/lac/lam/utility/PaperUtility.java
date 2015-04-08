package br.ufmg.dcc.lac.lam.utility;

import br.ufmg.dcc.lac.Itemset;

public class PaperUtility extends UtilityFunction{

	private static final long serialVersionUID = 80706824704508614L;

	@Override
	public double utility(Itemset item) {
		
		double utility = (item.getSupport() - 1) * (item.getSize() - 1);
		
		return utility;
	}

	@Override
	public int ordering() {
		return -1;
	}

}
