package moa.classifiers.eess;

import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.tasks.TaskMonitor;

public class LengthFrequency extends AbstractOptionHandler implements UtilityClassInterface{

	private static final long serialVersionUID = 1L;

	@Override
	public void getDescription(StringBuilder arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUtilityClass() {
		return "br.ufmg.dcc.lac.lam.utility.PaperUtility";
	}

	@Override
	protected void prepareForUseImpl(TaskMonitor arg0, ObjectRepository arg1) {
		// TODO Auto-generated method stub
		
	}

}
