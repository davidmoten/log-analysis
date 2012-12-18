
	
function drawGraph(field,tablename,buckets,interval,startTime,metric,plot,refresh,sqlElement) {
	
	var sql = "select logTimestamp, " + field + " as value from " 
				+ tablename + " where " + field 
				+" is not null order by logTimestamp";
	sql = sql.replace(new RegExp(" ", 'g'), "%20");

	var barOptions = {
		show : true,
		align : "center",
		barWidth : interval
	};

	function onDataReceived(series) {
		console.log(series);
		var n;
		if (buckets == 0) {
			n = 1;
			barOptions.barWidth = 0;
		} else {
			n = buckets;
		}

		//series.lines = { show: true, steps: true, fill: true };
		series.label=field;
		series.bars = barOptions;
		series.points = {
			show : true
		};
		var options = {
			xaxis : {
				mode : "time",
				timeformat : "%H:%M"
			}
		//,colors: ["#d18b2c", "#dba255","#dba255", "#919733","#919733"]
		};

		var finishTime = startTime + interval * n;

		var meanGraph = {
			label:"mean",
			data : [ [ startTime, series.stats.MEAN ],
					[ finishTime, series.stats.MEAN ] ],
			lines : {
				show : true
			}
		};
		console.log(series.stats);
		var metricValue = series.stats[metric];
		console.log("metric="+metricValue);
		var metricGraph = {
			label:metric.toLowerCase(),
			data : [ [ startTime, metricValue ],
					[ finishTime, metricValue ] ],
			lines : {
				show : true
			}
		};
		var sdUpperGraph = {
				label:"mean+sd",
			data : [
					[
							startTime,
							series.stats.MEAN
									+ series.stats.STANDARD_DEVIATION ],
					[
							finishTime,
							series.stats.MEAN
									+ series.stats.STANDARD_DEVIATION ] ],
			lines : {
				show : true
			}
		}
		var sdLowerGraph = {
			label: "mean-sd",
			data : [
					[
							startTime,
							series.stats.MEAN
									- series.stats.STANDARD_DEVIATION ],
					[
							finishTime,
							series.stats.MEAN
									- series.stats.STANDARD_DEVIATION ] ],
			lines : {
				show : true
			}
		}
		$.plot(plot, [ series, meanGraph, sdLowerGraph,
				sdUpperGraph, metricGraph ], options);
	}
	sqlElement.text(sql.replace(new RegExp("%20", 'g'), " "));
	
	function refreshGraph() {
		var sql2 = sqlElement.val().replace(new RegExp(" ", 'g'), "%20");

		var dataurl = "data?sql=" + sql2 + "&start=" + startTime
				+ "&interval=" + interval + "&buckets=" + buckets
				+ "&metric=" + metric;
		console.log("dataurl=" + dataurl);

		$.ajax({
			url : dataurl,
			method : 'GET',
			dataType : 'json',
			success : onDataReceived,
			error : function(request, textStatus, errorThrown) {
				console.log(errorThrown);
				$("#error").text(errorThrown);
			},
			timeout : 60000
		});
	}
	refreshGraph();
	refresh.click(function(event) {
		event.preventDefault();
		refreshGraph();
	});

}

function extractPeriod(s) {
	if (endsWith(s, "d"))
		return s.substring(0, s.length - 1) * 24 * 3600000;
	else if (endsWith(s, "h"))
		return s.substring(0, s.length - 1) * 3600000;
	else if (endsWith(s, "m"))
		return s.substring(0, s.length - 1) * 60000;
	else if (endsWith(s, "s"))
		return s.substring(0, s.length - 1) * 1000;
	else if (endsWith(s, "ms"))
		return s.substring(0, s.length - 1);
	else
		return Number(s);
}

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

function getURLParameter(name) {
	return decodeURIComponent((RegExp(name + '=' + '(.+?)(&|$)')
			.exec(location.search) || [ , null ])[1]);
}

function getAbsolutePath() {
	var loc = window.location;
	var pathName = loc.pathname.substring(0, loc.pathname
			.lastIndexOf('/') + 1);
	return loc.href
			.substring(
					0,
					loc.href.length
							- ((loc.pathname + loc.search + loc.hash).length - pathName.length));
}