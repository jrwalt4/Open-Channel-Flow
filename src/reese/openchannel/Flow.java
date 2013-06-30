package reese.openchannel;

public class Flow {
	
	protected double flow, depth;
	protected Channel channel;
	protected boolean isMetric;
	
	public Flow(double newFlow, Channel newChannel, double newDepth) {
		flow = newFlow;
		channel = newChannel;
		isMetric = channel.isMetric;
		depth = newDepth;
	}
	
	public void adjustForward(double stepSize) {
		
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public double getFlow() {
		return flow;
	}
	
	public double getFlow(double x) {
		return getFlow();
	}
	
	public void setFlow(double newFlow) {
		flow = newFlow;
	}
	
	public double getDepth() {
		return depth;
	}
	
	public void setDepth(double newDepth) {
		depth = newDepth;
	}

	public double energy() {
		double gravity;
		if(isMetric) gravity = 9.81;
		else gravity = 32.2;
		return depth + Math.pow(flow, 2) 
				/ (2*gravity*Math.pow(channel.area(depth),2));
	}
	
	public double momentum () {
		double gravity;
		if(isMetric) gravity = 9.81;
		else gravity = 32.2;
		return channel.centroid(this.depth)*channel.area(this.depth) + Math.pow(this.flow, 2.0) 
				/ (gravity*this.channel.area(this.depth));
	}
	
	public static double froudeNumber(Channel channel, double depth, double flow, boolean isMetric) {
		double gravity = (isMetric) ? 9.81 : 32.2;
		return Math.sqrt(Math.pow(flow,2)*channel.topWidth(depth) 
				/ (gravity*Math.pow(channel.area(depth),3)));
	}

	public double froudeNumber(double depth, double flow) {
		return froudeNumber(channel,depth,flow,isMetric);
	}
	
	public double froudeNumber(double depth) {
		return froudeNumber(channel,depth,flow,isMetric);
	}
	
	public double froudeNumber() {
		return froudeNumber(channel,depth,flow,isMetric);
	}
	
	public double critDepth() {
		return channel.critDepth(this);
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
	
	/**
	 * Calculates the slope based on the Gradually Varied Flow Equation 
	 * @param depth
	 * @return
	 */
	public double slope (double depth) {
		double units = (isMetric) ? 1.0 : 1.49;
		double Sf = Math.pow(flow * channel.roughness
				/ units / channel.area(depth) 
				/ Math.pow(channel.hydRadius(depth), 2.0/3.0), 2.0);
		return (channel.slope - Sf)
				/ (1.0 - Math.pow(froudeNumber(depth), 2.0));
	}
}
