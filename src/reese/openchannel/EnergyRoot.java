package reese.openchannel;

import reese.functions.AnalyticFunction;

public class EnergyRoot extends AnalyticFunction {
	
	double flow, energy, gravity;
	Channel channel;

	public EnergyRoot(Flow newFlow, Channel newChannel) {
		super("Energy");// give it a name
		flow = newFlow.getFlow();
		energy = newFlow.energy();
		channel = newChannel;
		gravity = (channel.isMetric) ? 9.81 : 32.2;
	}

	@Override
	public double f(double depth) {
		double value = (depth - energy)*2.0*gravity*Math.pow(channel.area(depth),2.0) 
				+ Math.pow(flow, 2.0);// / (2*gravity*Math.pow(channel.area(depth),2));
		return value;
	}
	
	@Override
	public double firstDeriv(double depth) {
		double value = 
				(depth - energy)*4.0*gravity*channel.area(depth)*channel.topWidth(depth);
		return value;
	}
	//*
	@Override
	public double newtonsMethod(double depth) {
		return depth - channel.area(depth)/(2.0*channel.topWidth(depth))
				- Math.pow(flow, 2) / 
				((depth - energy)*4.0*gravity*channel.area(depth)*channel.topWidth(depth));
	}
	//*/
}
