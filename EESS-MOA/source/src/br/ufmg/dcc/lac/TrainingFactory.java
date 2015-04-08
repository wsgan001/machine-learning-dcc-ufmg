package br.ufmg.dcc.lac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TrainingFactory implements Serializable{
	
	
	private static final long serialVersionUID = 1279106127452583851L;
	
	public static final char ATTRIBUTE = 'w';
	public static final char CLASS = 'C';

	private static BufferedReader br;
	
	public static List<LacInstance> readLacFile(String path) throws Exception{
		File f = new File(path);
		
		if(!(f.exists() && f.isFile())){
			throw new FileNotFoundException();
		}
		
		br = new BufferedReader(new FileReader(f));
		List<LacInstance> training = new ArrayList<LacInstance>();
		
		while(br.ready()){
			String line = br.readLine();
			String lineWithoutSpace = line.trim();
			
			if(lineWithoutSpace.length() == 0){
				throw new Exception("Empty line");
			}
			
			String[] splitedLine = line.split(" ");
			
			int nfeatures = splitedLine.length - 2;
			
			String[] features = new String[nfeatures];
			int tid = Integer.parseInt(splitedLine[0]);
			String label = null;
			
			int featurePos = 0;
			for(int i = 1; i < splitedLine.length; i++){
				String attr = splitedLine[i];
				
				char attrType = attr.charAt(0);
				
				if(attrType == CLASS){
					label = attr.replace("CLASS=", "");
				}else if(attrType == ATTRIBUTE){
					features[featurePos] = attr;
					featurePos++;
				}else{
					throw new Exception("Invalid line format.");
				}
			}
			
			if(label == null){
				throw new Exception("Class label not found.");
			}
			
			LacInstance instance = new LacInstance(tid, features, label);
			
			training.add(instance);
		}
		
		br.close();		
		return training;
	}
	
//	public static void main(String[] args) throws Exception{
//		String path = "/home/rloliveirajr/exp/dataset/felipemeloIngles.lac";
//		
//		SymbolTable classesTable = SymbolTable.classesTable();
//		LacSupervised lac = new LacSupervised(0.01f, 0.01f, 0, 3);
//		
//		List<LacInstance> instances = readLacFile(path);
//		
//		Training training = new Training();
//		for(int i = 0; i < instances.size()/2; i++){
//			LacInstance instance = instances.get(i);
//			training.addTransaction(instance);
//		}
//
//		AssociationRule associationRule = new Eclat(0.01f, 3, training.getTidClassMap());
//
//		for(int i = ((instances.size()/2) + 1); i < ((instances.size()/2)+2); i++){
//			LacInstance instance = instances.get(i);
//			int indexedClass = classesTable.getId(instance.getLabel());
//			instance.setIndexedHiddenClass(indexedClass);
//			
//			double[] scores = new double[training.getNumberOfClasses()];
//						
//			lac.predict(training, instance, associationRule, scores);
//			
//			double normalizationFactor = 0;
//			System.out.println(Arrays.toString(scores));
//			for(double s : scores){
//				normalizationFactor += s;
//			}
//			
//			System.out.println("normalizationFactor: " + normalizationFactor);
//			
//			for(int s = 0; s < scores.length; s++){
//				scores[s] = scores[s]/normalizationFactor;
//			}
//			
//			System.out.println(Arrays.toString(scores));
//		}
//	}
	
	
}
