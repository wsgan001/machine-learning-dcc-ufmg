package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Vector;

class KaldorHicksEfficiency {

	final static void frontier(List<Point> skyline, List<Point> dominated){
		
		double k = skyline.get(0).getMetrics()[0] + skyline.get(0).getMetrics()[1];
		
		for(Point i : skyline){
			double v = i.getMetrics()[0] + i.getMetrics()[1];
			
			if(Double.compare(v, k) < 0){
				k = v;
			}
		}
		
		for(int i = dominated.size() - 1; i > -1 ; i--){
			Point pi = dominated.get(i);
			double v = pi.getMetrics()[0] + pi.getMetrics()[1];
			
			if(Double.compare(v, k) > -1){
				skyline.add(pi);
				dominated.remove(i);
			}
		}
		
		System.out.printf("#Function\nf(x) = %f - x\n", k);
	}
}

class Point{
	public final double x;
	public final double y;
	public final int index;
	public final String key;
	public Point(String key, int index, double x, double y){
		this.index = index;
		this.x = x;
		this.y = y;
		this.key = key;
	}
	public double[] getMetrics(){
		return new double[]{this.x, this.y};
	}
	
	public String toString(){
		return String.format("%s %.2f %.2f", this.key, 1-this.x, 1-this.y);
	}
}

class Skyline {

	private enum RELATION {DOMINATED, DOMINATES, INCOMPARABLE};
	
	protected List<Point> window;
	protected List<Point> removed;
	protected int inserted;
	public Skyline(){
		window = new Vector<Point>();
		removed = new Vector<Point>();
	}
	
	public void addPoint(Point point){
		for(int i = window.size() - 1; i > -1 ; --i){
			Point rule = this.window.get(i);
			RELATION r = this.compare(point, rule);
			
			if(r == RELATION.DOMINATED){
				removed.add(point);
				return;
			}else if(r == RELATION.DOMINATES){
				Point inst = this.window.remove(i);
				removed.add(inst);
			}
		}
		
		this.window.add(point);
	}
	
	private Skyline.RELATION compare(Point point1, Point point2){
		int i = 0;
		
		double[] metricsPoint1 = point1.getMetrics();
		double[] metricsPoint2 = point2.getMetrics();
		
		while(i < metricsPoint1.length && Double.compare(metricsPoint1[i], metricsPoint2[i]) == 0){
			++i;
		}

		if(i == metricsPoint1.length){
			return RELATION.INCOMPARABLE;
		}
		
		if(Double.compare(metricsPoint1[i], metricsPoint2[i]) < 0){
			for(++i; i < metricsPoint1.length; ++i){
				if(Double.compare(metricsPoint1[i], metricsPoint2[i]) > 0){
					return RELATION.INCOMPARABLE;
				}
			}
			return RELATION.DOMINATED;
		}
			
		for(++i; i < metricsPoint1.length; ++i){
			if(Double.compare(metricsPoint1[i], metricsPoint2[i]) < 0){
				return RELATION.INCOMPARABLE;
			}
		}
		return RELATION.DOMINATES;
	}
	
	public List<Point> getWindow(){
		return this.window;
	}
}

public class Main {
	
	static final int N = 36;
	public static void main(String args[]) throws Exception{
		
		BufferedReader br = new BufferedReader(new FileReader("/home/rloliveirajr/bapess.txt"));
		Point[] points = new Point[N];
		int i = 0;
		String s = null;
		String[] p = null;
		while(br.ready()){
			s = br.readLine();
			p = s.split(" ");
			
			points[i] = new Point(p[0], i, 1-Double.parseDouble(p[1]), 1-Double.parseDouble(p[2]));
			++i;
		}
		
//		Random rand = new Random(System.currentTimeMillis()); 
//		for(int i = 0; i < N; i++){
//			Point p = new Point((i+1), rand.nextDouble(), rand.nextDouble());
//			points[i] = p;
//		}
		
		Skyline skyline = new Skyline();
		
		for(Point pp : points){
			skyline.addPoint(pp);
		}
		
		List<Point> window = skyline.window;
		
//		List<Point> dominated = skyline.removed;
//		KaldorHicksEfficiency.frontier(window, dominated);
//		System.out.println("#Window");
		
		for(Point w : window){
//			if(Double.compare(w.y,0.1) < 1){
				System.out.println(w);
//			}
		}
		
//		System.out.println("#Dominated");
//		for(Point d : dominated){
//			if(Double.compare(d.y,0.1) < 1){
//				System.out.println(d);
//			}
//		}
		
		
	}
}
