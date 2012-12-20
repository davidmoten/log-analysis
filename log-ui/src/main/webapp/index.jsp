<html>
<h2>Log Analysis</h2>
<h3>Sample graphs</h3>
<ul>
	<li><a
		href="graph.html?buckets=0&finish=now&interval=1h&metric=MAX&extraMetric=MAX&field1=specialNumber&table=Dummy&width=1000px&height=500px">Last
			hour, all points</a></li>
	<li><a
		href="graph.html?buckets=60&finish=now&interval=1m&metric=MAX&extraMetric=MAX&field1=specialNumber&table=Dummy&width=1000px&height=500px&title=SpecialNumber aggregated by minute for the last hour">Last
			hour, aggregated by minute</a></li>
	<li><a href="amsa.html">AMSA graphs</a></li>
</ul>

<h3>Build a graph</h3>
<form style="font-size: 12px; margin-left: 50px;" action="graph.html"
	method="get">
	<p>
		Title: <input type="text" name="title" value="Graph"></input>
	</p>
	<p>
		Buckets: <input type="text" name="buckets" value="24"
			style="width: 5em;"></input>&nbsp;&nbsp; Interval: <input type="text"
			name="interval" value="1h" style="width: 8em;"
			pattern="[0-9]+(d|h|m|s|ms)?"></input> (Number optionally followed by
		unit <i>d,h,m,s,ms</i>)
	</p>

	<div style="min-width: 13em">Metric:</div>
	<select name="metric">
		<option value="MEAN">Mean</option>
		<option value="MIN">Min</option>
		<option value="MAX" selected="MAX">Max</option>
		<option value="COUNT">Count</option>
		<option value="SUM">Sum</option>
		<option value="FIRST">First</option>
		<option value="LAST">Last</option>
		<option value="EARLIEST">Earliest</option>
		<option value="LATEST">Latest</option>
		<option value="MEDIAN">Median</option>
		<option value="MODE">Mode</option>
		<option value="STANDARD_DEVIATION">Standard Deviation</option>
		<option value="SUM_SQUARES">Sum of squares</option>
		<option value="VARIANCE">Variance</option>
		<option value="VARIANCE">Variance (population)</option>
	</select>
	</p>
	<div style="min-width: 13em">Extra metric:</div>
	<select name="extraMetric">
		<option value="MEAN">Mean</option>
		<option value="MIN">Min</option>
		<option value="MAX" selected="MAX">Max</option>
		<option value="COUNT">Count</option>
		<option value="SUM">Sum</option>
		<option value="FIRST">First</option>
		<option value="LAST">Last</option>
		<option value="EARLIEST">Earliest</option>
		<option value="LATEST">Latest</option>
		<option value="MEDIAN">Median</option>
		<option value="MODE">Mode</option>
		<option value="STANDARD_DEVIATION">Standard Deviation</option>
		<option value="SUM_SQUARES">Sum of squares</option>
		<option value="VARIANCE">Variance</option>
		<option value="VARIANCE">Variance (population)</option>
	</select>
	</p>
	<p>
		Field: <input type="text" name="field1" value="elapsedTimeSeconds"></input>
	</p>
	<p>
		Width: <input type="text" name="width" value="1000px"></input>
	</p>
	<p>
		Height: <input type="text" name="height" value="500px"></input>
	</p>
	<input type="submit" value="View graph" />
</form>
</html>