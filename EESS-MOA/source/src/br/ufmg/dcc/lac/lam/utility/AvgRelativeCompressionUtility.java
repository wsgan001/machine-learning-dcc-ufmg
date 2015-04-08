package br.ufmg.dcc.lac.lam.utility;

import br.ufmg.dcc.lac.Itemset;

public class AvgRelativeCompressionUtility extends UtilityFunction{

	private static final long serialVersionUID = 80706824704508614L;

	@Override
	public double utility(Itemset item) {
		
		return item.relativeCompression/item.getSize();
	}

	@Override
	public int ordering() {
		return -1;
	}

}
