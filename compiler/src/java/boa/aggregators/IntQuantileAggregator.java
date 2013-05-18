package boa.aggregators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import boa.io.EmitKey;

/**
 * A Boa aggregator to calculate the quantiles for the values in a dataset.
 * 
 * @author anthonyu
 */
@AggregatorSpec(name = "quantile", type = "int", formalParameters = { "int" })
public class IntQuantileAggregator extends QuantileAggregator {
	private SortedCountingSet<Long> list;

	/**
	 * Construct a IntQuantileAggregator.
	 * 
	 * @param n
	 *            A long representing the number of quantiles to calculate
	 */
	public IntQuantileAggregator(final long n) {
		super(n);
	}

	/** {@inheritDoc} */
	@Override
	public void start(final EmitKey key) {
		super.start(key);

		this.list = new SortedCountingSet<Long>();
	}

	/** {@inheritDoc} */
	@Override
	public void aggregate(final String data, final String metadata) throws IOException {
		if (data.indexOf('.') != -1)
			this.aggregate(Double.valueOf(data).longValue(), metadata);
		else
			this.aggregate(Long.parseLong(data), metadata);
	}

	/** {@inheritDoc} */
	@Override
	public void aggregate(final long data, final String metadata) throws IOException {
		this.list.add(Long.valueOf(data), super.count(metadata));
	}

	/** {@inheritDoc} */
	@Override
	public void aggregate(final double data, final String metadata) throws IOException {
		this.aggregate(Double.valueOf(data).longValue(), metadata);
	}

	/** {@inheritDoc} */
	@Override
	public List<Pair<String, Long>> getTuples() {
		final List<Pair<String, Long>> list = new ArrayList<Pair<String, Long>>();

		// convert the map entries into a list of Pair
		for (final Entry<Long, Long> e : this.list.getEntries())
			list.add(new Pair<String, Long>(e.getKey().toString(), e.getValue()));

		return list;
	}
}
