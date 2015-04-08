//package moa.classifiers.wrapper;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//import java.util.Vector;
//
//import br.ufmg.dcc.lac.KaldorHicksEfficiency;
//import br.ufmg.dcc.lac.LacInstance;
//import br.ufmg.dcc.lac.LacSupervised;
//import br.ufmg.dcc.lac.Skyline2Instance;
//import br.ufmg.dcc.lac.Training;
//
//import weka.classifiers.Classifier;
//import weka.core.EuclideanDistance;
//import weka.core.Instance;
//import weka.core.Instances;
//import moa.classifiers.AbstractClassifier;
//import moa.classifiers.eess.LacWeka;
//import moa.core.Measurement;
//import moa.options.FlagOption;
//import moa.options.WEKAClassOption;
//
//public class Pess  extends AbstractClassifier {
//
//private static final long serialVersionUID = 4740958383832856257L;
//	
//	public WEKAClassOption baseLearnerOption = new WEKAClassOption("baseLearner", 'l',
//            "Classifier to train.", weka.classifiers.Classifier.class, "weka.classifiers.bayes.NaiveBayes");
//	
//	public FlagOption eessPerClassOption = new FlagOption("EessPerClass", 'e', "Perform the EESS method per class.");
//	
//	public FlagOption kaldorHicksOption = new FlagOption("kaldorHicks", 'k', "Ativate sample selection by Kaldor-Hicks Efficiency.");
//	
//	public FlagOption considerFeaturesPositionOption = new FlagOption("considerFeaturesPosition", 'p', "Consider the position of features.");
//	
//	
//	protected Classifier classifier;
//	protected Instances train;
//	
//	protected final List<LacInstance> lacInstances;
//	protected final Set<LacInstance> previousRules;
//	protected final Training training;
//	protected final Random rand;
//	protected final LacSupervised lac;
//	
//	private final EuclideanDistance euclideanDist;
//	
//	protected int labeledAdded;
//	protected int timestamp;
//	
//	protected boolean manage;
//
//	protected int trainingSize;
//	
//	protected int skylineAvgSize;
//	protected int kaldorHicksAvgSize;	
//	protected int trainingAvgSize;
//	
//	protected int nsawInstances;
//	
//	public Pess(){
//		this.euclideanDist = new EuclideanDistance();
//		this.lacInstances = new Vector<LacInstance>();
//		this.previousRules = new HashSet<LacInstance>();
//		this.training = new Training();
//		this.rand = new Random(this.randomSeed);
//		this.lac = new LacSupervised();
//	}
//		
//	@Override
//	public void resetLearningImpl() {
//		try {
//            String[] options = weka.core.Utils.splitOptions(baseLearnerOption.getValueAsCLIString());
//            this.createWekaClassifier(options);
//        } catch (Exception e) {
//            System.err.println("Creating a new classifier: " + e.getMessage());
//        }
//
//		this.previousRules.clear();
//		this.lacInstances.clear();
//		this.labeledAdded = 0;
//		this.rand.setSeed(this.randomSeed);
//	
//		this.timestamp = 0;
//		
//		this.manage = false;
//		
//		this.trainingSize = 0;
//		
//		this.skylineAvgSize = 0;
//		this.kaldorHicksAvgSize = 0;
//		this.trainingAvgSize = 0;
//		
//		this.nsawInstances = 0;
//	}
//	
//	@Override
//	public void trainOnInstanceImpl(Instance arg0) {	
//		LacInstance inst = LacWeka.populateInstance(timestamp, arg0, this.considerFeaturesPositionOption.isSet());
//		this.timestamp++;
//		
//		if(this.manage == true){
//			this.nsawInstances++;
//			this.eess();
//			this.previousRules.clear();
//			
//			//Inserting instance
//			this.labeledAdded++;
//			this.train.add(arg0);
//			inst.instance = this.train.lastInstance();
//			this.lacInstances.add(inst);
//			
//		}else{
//			if(this.train == null) {
//				this.train = new Instances(arg0.dataset());
//			}
//			
//			this.train.add(arg0);
//			inst.instance = this.train.lastInstance();
//			this.lacInstances.add(inst);
//		}
//	}
//	
//	protected void eess(){				
//		List<LacInstance> skyline = new Vector<LacInstance>();
//		List<LacInstance> dominated = new Vector<LacInstance>();
//		
//		this.computeSkyline(skyline, dominated);	
//		
//		this.trainingSize = this.lacInstances.size();
//		this.trainingAvgSize += this.lacInstances.size();
//		
//		this.lacInstances.clear();
//		this.lacInstances.addAll(skyline);
//	}
//	
//	protected void computeSkyline(List<LacInstance> returnedSkyline, List<LacInstance> returnedDominated){
//		
//		double[] coverageRank = this.coverageRank();
//
//		List<LacInstance> insts = this.lacInstances;
//
//		final Skyline2Instance[] skyline = new Skyline2Instance[this.getModelContext().numClasses()];
//		
//		for(int s = 0; s < skyline.length; s++){
//			skyline[s] = new Skyline2Instance();
//		}
//		
//		int indexSkyline = 0;
//		for(int i = 0; i < insts.size(); i++){
//			LacInstance instance = insts.get(i); 
//
//			if(this.eessPerClassOption.isSet()){
//				indexSkyline = instance.getIndexedClass();
//			}
//			
//			instance.metrics = new double[3];
//			instance.metrics[0] = coverageRank[i];
//			instance.metrics[1] = (double)instance.tid/this.timestamp;
//			instance.metrics[2] = this.rand.nextDouble();
//			
//			skyline[indexSkyline].addPoint(instance);			
//		}
//		
//		for(int s = 0; s < skyline.length; s++){
//			this.skylineAvgSize += skyline[s].window.size();
//			
//			if(kaldorHicksOption.isSet()){
//				KaldorHicksEfficiency.frontier(skyline[s].window, skyline[s].removed);
//				this.kaldorHicksAvgSize += skyline[s].window.size();
//			}
//			
//			returnedSkyline.addAll(skyline[s].window);	
//		}	
//	}
//	
//	private double[] coverageRank(){
//		
//		
//		double[] coverage = new double[this.lacInstances.size()];
//		Arrays.fill(coverage, 0);
//		
//		LacInstance instance = null;
//		
//		for(int i = 0; i < this.lacInstances.size(); i++){
//			
//			instance = this.lacInstances.get(i);
//			
//			for(LacInstance r : this.previousRules){
//				
//				coverage[i] += 1.0/euclideanDist.distance(instance.instance, r.instance);
//			}
//			
//			coverage[i] /= (double)this.previousRules.size();
//		}
//		
//		return coverage;
//	}
//	
//	@Override
//	public double[] getVotesForInstance(Instance arg0) {
//		
//		double[] votes = new double[arg0.numClasses()];
//		 
//		if(this.manage == true){
//			this.buildClassifier();
//						
//			try {
//                votes = this.classifier.distributionForInstance(arg0);
//            } catch (Exception e) {}
//		}else{
//			double equalVotes = 1.0 / arg0.numClasses();
//			Arrays.fill(votes, equalVotes);
//			
//			this.manage = true;
//		}
//		
//		return votes;
//	}
//	
//	@Override
//	public boolean isRandomizable() {
//		return true;
//	}
//
//	@Override
//	public void getModelDescription(StringBuilder arg0, int arg1) {}
//	
//	@Override
//	protected Measurement[] getModelMeasurementsImpl() {
//		
//		Measurement[] m = new Measurement[]{
//				new Measurement("model training instances", this.labeledAdded),
//				new Measurement("window", this.trainingSize),
//				new Measurement("AvgWindow", (double)this.trainingAvgSize/this.nsawInstances),
//				new Measurement("AvgSkyline", (double)this.skylineAvgSize/this.nsawInstances),
//				new Measurement("AvgKaldor-Hicks", (double)this.kaldorHicksAvgSize/this.nsawInstances)};
//		
//		this.nsawInstances = 0;
//		this.trainingAvgSize = 0;
//		this.skylineAvgSize = 0;
//		this.kaldorHicksAvgSize = 0;
//		
//		return m;
//	}
//
//	private void buildClassifier() {
//        try {
//            Classifier auxclassifier = weka.classifiers.AbstractClassifier.makeCopy(classifier);
//            auxclassifier.buildClassifier(train);
//            classifier = auxclassifier;
//        } catch (Exception e) {
//            System.err.println("Building WEKA Classifier: " + e.getMessage());
//        }
//    }
//	
//	private void createWekaClassifier(String[] options) throws Exception {
//        String classifierName = options[0];
//        String[] newoptions = options.clone();
//        newoptions[0] = "";
//        this.classifier = weka.classifiers.AbstractClassifier.forName(classifierName, newoptions);
//    }
//}