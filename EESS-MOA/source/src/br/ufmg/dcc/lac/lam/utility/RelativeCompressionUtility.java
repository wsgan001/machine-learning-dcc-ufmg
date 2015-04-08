package br.ufmg.dcc.lac.lam.utility;

import br.ufmg.dcc.lac.Itemset;

public class RelativeCompressionUtility extends UtilityFunction{

	private static final long serialVersionUID = 80706824704508614L;

	@Override
	public double utility(Itemset item) {
		
		return item.relativeCompression;
	}

	@Override
	public int ordering() {
		return -1;
	}

}
