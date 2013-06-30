package reese.openchannel;

import reese.diffeq.ODE;

public class GVF extends ODE {

	Channel channel;
	Flow flow;
	
	public GVF(Flow flow) {
		this.flow = flow;
		this.channel = flow.channel;
	}
	
	@Override
	public double dydx(double x, double depth) {
		double units = (flow.isMetric) ? 1.0 : 1.49;
		double Sf = Math.pow(flow.getFlow(x) * channel.roughness
				/ units / channel.area(depth) 
				/ Math.pow(channel.hydRadius(depth), 2.0/3.0), 2.0);
		return (channel.slope - Sf)
				/ (1.0 - Math.pow(flow.froudeNumber(depth), 2.0));
	}
}
