package reese.openchannel;

public class TrapezoidChannel extends Channel {
	protected double bottomWidth;
	protected double sideSlope;

	public TrapezoidChannel(double slope, boolean isMetric, double roughness,
			double bottomWidth, double sideSlope) {
		super(slope, isMetric, roughness);
		type = "Trapezoidal";
		this.bottomWidth = bottomWidth;
		this.sideSlope = sideSlope;
		/*String units;
		if (isMetric) units = "m"; else units = "ft";
		System.out.println("New "+type+" channel with "+
				"slope = "+slope+" and roughness = "+roughness+
				" and bottom width = "+bottomWidth+units+
				" and side slope = "+sideSlope);*/
	}

	@Override
	public double area(double depth) {
		if (depth < 0) return 0;
		return bottomWidth*depth + sideSlope*Math.pow(depth,2.0);
	}

	@Override
	public double perimeter(double depth) {
		double perimeter = bottomWidth + 2.0 * depth * Math.sqrt(1+Math.pow(sideSlope, 2.0));
		return perimeter;
	}

	@Override
	public double topWidth(double depth) {
		return (bottomWidth + 2.0 * sideSlope*depth);
	}

	@Override
	public double centroid(double depth) {
		if (depth < 0) return 0;
		else return (bottomWidth / 2.0 * depth + 2.0/3.0 * sideSlope * Math.pow(depth, 2.0))
				 / (bottomWidth + sideSlope*depth);
	}
}
