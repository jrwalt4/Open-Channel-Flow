package reese.openchannel;

import reese.functions.AnalyticFunction;

public class FroudeRoot extends AnalyticFunction {
	
	double flow;
	Channel channel;
	
	public FroudeRoot(Flow newFlow, Channel newChannel) {
		flow = newFlow.getFlow();
		channel = newChannel;
		functionName = "Froude #";
	}

	@Override
	public double f(double depth) {
		double gravity;
		if (channel.isMetric) gravity = 9.81;
		else gravity = 32.2;
		return (gravity * Math.pow(channel.area(depth), 3)) - Math.pow(flow, 2) * channel.topWidth(depth);
	}

}
