package reese.openchannel;


public abstract class Channel {
	
	protected double slope;
	protected boolean isMetric;
	protected double roughness;
	public String type;
	protected double maxDepth = 50;
	
	public void setMaxDepth(double depth) {
		maxDepth = depth;
	}
	
	public Channel(double slope, boolean isMetric, double roughness) {
		this.slope = slope;
		this.isMetric = isMetric;
		this.roughness = roughness;
	}
	
	public abstract double area(double depth);
	public abstract double perimeter(double depth);
	public abstract double topWidth(double depth);
	public abstract double centroid(double depth);
	
	public double hydRadius(double depth) {
		if (depth == 0) return 0;
		else return area(depth)/perimeter(depth);
	}
	
	public double critDepth(Flow flow) {
		FroudeRoot froudeRoot = new FroudeRoot(flow, this);
		return froudeRoot.findRoot(0, maxDepth);
	}

	public double normDepth(Flow flow) {
		ManningRoot manRoot = new ManningRoot(flow, this);
		return manRoot.findRoot(0, maxDepth);
	}
	
	public double altDepth(Flow flow) {
		EnergyRoot engRoot = new EnergyRoot(flow, this);
		double x0, x1;
		if (flow.froudeNumber() < 1.0) {
			x0 = 0.00;
			x1 = flow.depth-.001;
		}
		else {
			x0 = flow.getDepth()+.001;
			x1 = maxDepth;
		}
		return engRoot.findRoot(x0,x1);
	}
	
	public double conjDepth(Flow flow) {
		MomentumRoot momRoot = new MomentumRoot(flow, this);
		double x0, x1;
		if (flow.froudeNumber() < 1.0) {
			x0 = 0.0;
			x1 = flow.getDepth()-.001;
		}
		else {
			x0 = flow.getDepth()+.001;
			x1 = maxDepth;
		}
		return momRoot.findRoot(x0,x1);
	}
	
	public class NFlow {
		protected Double flow;
		
		public NFlow(Double newFlow) {
			flow = newFlow;
			/*String units;
			units = isMetric? "cms m" : "cfs ft";
			System.out.println("New flow of "+flow+units.substring(0, 3)+
					" for "+channel.type+" channel with a depth of "
					+depth+units.substring(4, units.length()));*/
		}
		
		public Double getFlow() {
			return flow;
		}
		
		public void adjustForward(double stepSize) {
			
		}

		public double energy(Double depth) {
			double gravity = (isMetric) ? 9.81 : 32.2;
			return depth + Math.pow(flow, 2) 
					/ (2*gravity*Math.pow(area(depth),2));
		}
		
		public double momentum (Double depth) {
			double gravity = (isMetric) ? 9.81 : 32.2;
			return centroid(depth)*area(depth) + Math.pow(flow, 2.0) 
					/ (gravity*area(depth));
		}
		
		public double froudeNumber(double depth, double flow) {
			double gravity = (isMetric) ? 9.81 : 32.2;
			return Math.sqrt(Math.pow(flow,2)*topWidth(depth) 
					/ (gravity*Math.pow(area(depth),3)));
		}
		
		public double froudeNumber(double depth) {
			return froudeNumber(depth,flow);
		}
		/*
		public double MycritDepth() {
			return critDepth(this);
		}
		
		public double normDepth() {
			return channel.normDepth(this);
		}
		
		public double altDepth() {
			return channel.altDepth(this);
		}
		
		public double conjDepth() {
			return channel.conjDepth(this);
		}
		
		public void setFlow(Double newFlow) {
			flow = newFlow;
		}

		public double slope(double depth) {
			return slope(depth, 0);
		}

		public double slope(double depth, double stepSize) {
			double units = (isMetric) ? 1.0 : 1.49;
			double Sf = Math.pow(flow * roughness
					/ units / area(depth) 
					/ Math.pow(hydRadius(depth), 2.0/3.0), 2.0);
			return (slope - Sf)
					/ (1.0 - Math.pow(froudeNumber(depth), 2.0));
		}
		//*/
	}
	
}
