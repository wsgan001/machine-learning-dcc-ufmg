package moa.classifiers.eess;

import br.ufmg.dcc.lac.AssociationRule;
import br.ufmg.dcc.lac.Lam;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.options.ClassOption;
import moa.options.IntOption;
import moa.tasks.TaskMonitor;

public class LamAssociationRule extends AbstractOptionHandler implements AssociationRuleInterface{

	private static final long serialVersionUID = -2887956593544754512L;
	
	public ClassOption utilityClassOption = new ClassOption(
			"utilityClass", 'a', "", UtilityClassInterface.class,
			"moa.classifiers.eess.AvgRelativeCompression");
	
	public IntOption numPassesOption = new IntOption("numPasses", 'p', "",
			1, 0, Integer.MAX_VALUE);
	
	public IntOption clusterThresholdOption = new IntOption("clusterThreshold",
			't', "", 1000, 2, Integer.MAX_VALUE);
	
	public IntOption numHashOption = new IntOption("numHash",
			'h', "", 8, 2, Integer.MAX_VALUE);

	@Override
	public AssociationRule getAssociationRule() {
		final UtilityClassInterface uci = (UtilityClassInterface) 
				getPreparedClassOption(this.utilityClassOption);
		Lam lam = new Lam(1.0, this.numPassesOption.getValue(), 
				this.clusterThresholdOption.getValue(), this.numHashOption.getValue(), uci.getUtilityClass());
		
		return lam;
	}
	
	@Override
	public void getDescription(StringBuilder arg0, int arg1) {}

	@Override
	protected void prepareForUseImpl(TaskMonitor arg0, ObjectRepository arg1) {}
}
