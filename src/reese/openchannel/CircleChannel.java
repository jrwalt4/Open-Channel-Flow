package reese.openchannel;


public class CircleChannel extends Channel {
	protected double diameter;
	
	public CircleChannel(double slope, boolean isMetric, double roughness,
			double diameter) {
		super(slope, isMetric, roughness);
		type = "Circular";
		this.diameter = diameter;
		setMaxDepth(diameter);
		/*String units;
		if (isMetric) units = "m"; else units = "ft"; 
		System.out.println("New "+type+" channel with "+
				"slope = "+slope+" and roughness = "+roughness+
				" and diameter = "+diameter+units);*/
	}

	@Override
	public double area(double depth) {
		if (depth > diameter) return Math.PI * Math.pow(diameter, 2.0) / 4.0;
		double theta = theta(depth);
		return (theta - Math.sin(theta)) * Math.pow(diameter, 2.0) / 8.0;
	}

	@Override
	public double perimeter(double depth) {
		if (depth > diameter) return diameter*Math.PI;
		else return (diameter / 2.0) * theta(depth);
	}
	
	@Override
	public double hydRadius(double depth) {
		if (depth == 0.0) return 0.0;
		else return diameter / 4.0 *
				(theta(depth) - Math.sin(theta(depth)))/ theta(depth);
	}
	
	@Override
	public double topWidth(double depth) {
		if (depth > diameter) return 0.0;
		else return diameter * Math.sin(theta(depth)/2);
	}

	@Override
	public double centroid(double depth) {
		double theta = this.theta(depth);
		if (theta <= 0.0) return 0.0;
		else return diameter / 2.0 * 
				(1.0 - (Math.sin(theta / 2.0) - 1.0/3.0 * Math.sin(3.0/2.0 *theta))
						/ (theta - Math.sin(theta)));
	}
	
	public double depth(double theta) {
		if (theta > 2.0 * Math.PI) return diameter;
		else return this.diameter * (1.0 - Math.cos(theta/2.0)) / 2.0;
	}
	
	private double theta(double depth) {
		if (depth > this.diameter) depth = diameter;
		return 2.0 * Math.acos(1.0 - 2.0 * (depth / diameter));
	}
	
	/*
	@Override
	public double critDepth(Flow flow) {
		FroudeRoot froudeRoot = new FroudeRoot(flow, this);
		return froudeRoot.rootFinder(0.0001,diameter);
	}
	
	@Override
	public double normDepth(Flow flow) {
		ManningRoot manRoot = new ManningRoot(flow, this);
		return manRoot.rootFinder(0.0,0.955*diameter);
	}
	
	@Override
	public double altDepth(Flow flow) {
		EnergyRoot engRoot = new EnergyRoot(flow, this);
		double x0, x1;
		if (flow.froudeNumber() < 1.0) {
			x0 = 0.0;
			x1 = flow.getDepth() - 0.001;
		}
		else {
			x0 = flow.getDepth() + 0.001;
			x1 = diameter;
		}
		return engRoot.rootFinder(x0,x1);
	}
	
	@Override
	public double conjDepth(Flow flow) {
		MomentumRoot momRoot = new MomentumRoot(flow, this);
		double x0, x1;
		if (flow.froudeNumber() < 1.0) {
			x0 = 0.0;
			x1 = flow.getDepth() - .001;
		}
		else {
			x0 = flow.getDepth() + .001;
			x1 = diameter;
		}
		return momRoot.rootFinder(x0,x1);
	}
	//*/
}
