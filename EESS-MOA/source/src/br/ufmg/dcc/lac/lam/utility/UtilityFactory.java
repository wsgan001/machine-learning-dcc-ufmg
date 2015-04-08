package br.ufmg.dcc.lac.lam.utility;

public class UtilityFactory {
	
	public static UtilityFunction createUtilityFunction(String className) throws Exception{
		final Class<?> classObject = Class.forName(className);
		
		final Object object = classObject.newInstance();
		
		if(object instanceof UtilityFunction){
			final UtilityFunction utilityFunction = (UtilityFunction) object;
			
			return utilityFunction;
		}
		
		throw new Exception("Class must implement UtilityFunction interface");	
	}
	
	public static UtilitySpace createUtilitySpace(String className) throws Exception{
		final Class<?> classObject = Class.forName(className);
		
		final Object object = classObject.newInstance();
		
		if(object instanceof UtilitySpace){
			final UtilitySpace utilitySpace = (UtilitySpace) object;
			
			return utilitySpace;
		}
		
		throw new Exception("Class must implement UtilitySpace interface");	
	}
}
