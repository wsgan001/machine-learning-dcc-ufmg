package br.ufmg.dcc.gdafe.ml.weka.lac.test;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.rules.LAC;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class Main
{
	static DecimalFormat pctFormat = new DecimalFormat("##.##", new DecimalFormatSymbols(new Locale("pt", "br")));
	
	static String dir = "D:/Desenvolvimento/workspaces/helios-weka-3.7/LAC-Weka-3.7/test-cases/path";
	static Integer classIndex = -1;
	static Double minSupport = 0.0;
	static Double minConfidence = 0.0;
	static int repeat = 1;
	static int crossValidationFolds = 5;
	static int[] maxRuleSizes = {10};

	private static boolean verbose;

	public static void main(String[] args)
	{
		try
		{
			File[] files = getFilesFromInputDir();
			
			for(int m : maxRuleSizes)
			{
				for(File file : files)
				{
					for(int i = 0; i < repeat; i++)
					{
						try
						{
							runLAC(file, m);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Runs LAC against the given file.
	 *  
	 * @param file
	 * @param maxRuleLength
	 * @throws Exception
	 */
	private static void runLAC(File file, int maxRuleLength) throws Exception
	{
		LAC lac = new LAC();
		lac.setMinConfidence(minConfidence);
		lac.setMinSupport(minSupport);
		lac.setMaxRuleSize(maxRuleLength);
		
		String simpleResults = Arrays.toString(lac.getOptions()) + "\t" + file.getName().replace("_", "-") + "\t";
		
		ArffLoader loader = new ArffLoader();
		loader.setFile(file);
		Instances instances = loader.getDataSet();
		instances.setClassIndex(classIndex >= 0 ? classIndex : instances.numAttributes() - 1);

		Date start = new Date();
		Evaluation eval = new Evaluation(instances);
		eval.crossValidateModel(lac, instances, crossValidationFolds, new Random());
		Date end = new Date();
		
		if(verbose)
		{
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toMatrixString());
		}
		
		simpleResults += pctFormat.format(eval.pctCorrect()) + "\t" + (end.getTime() - start.getTime()) + "\t" + eval.unweightedMacroFmeasure();
		System.out.println(simpleResults);
	}

	/**
	 * Read all test files from a directory
	 * @return
	 */
	private static File[] getFilesFromInputDir()
	{
		File[] files = new File(dir).listFiles(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return name.endsWith(".arff");
			}
		});
		return files;
	}
}
