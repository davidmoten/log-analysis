
	
function drawGraph(field,tablename,buckets,interval,startTime,metric,extraMetric,plot,refresh,sqlElement) {
	
//	var sql = "select logTimestamp, logValue from " 
//				+ tablename + " where logKey = '" + field + "'" +
//				" and logValue is not null order by logTimestamp";
//	sql = sql.replace(new RegExp(" ", 'g'), "%20");

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
			},
			grid: { hoverable: true, clickable: true }
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
		var metricValue = series.stats[extraMetric];
		console.log("extraMetric="+metricValue);
		var extraMetricGraph = {
			label:extraMetric.toLowerCase(),
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
		};
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
		};
		
		 function showTooltip(x, y, contents) {
		        $('<div id="tooltip">' + contents + '</div>').css( {
		            position: 'absolute',
		            display: 'none',
		            top: y + 5,
		            left: x + 5,
		            border: '1px solid #fdd',
		            padding: '2px',
		            'background-color': '#fee',
		            opacity: 0.80
		        }).appendTo("body").fadeIn(200);
		    }

		    var previousPoint = null;
		    plot.bind("plothover", function (event, pos, item) {
		        $("#x").text(pos.x.toFixed(2));
		        $("#y").text(pos.y.toFixed(2));

		        //if ($("#enableTooltip:checked").length > 0) {
		            if (item) {
		                if (previousPoint != item.dataIndex) {
		                    previousPoint = item.dataIndex;
		                    
		                    $("#tooltip").remove();
		                    var x = item.datapoint[0].toFixed(2),
		                        y = item.datapoint[1].toFixed(2);
		                    
		                    showTooltip(item.pageX, item.pageY,
		                                item.series.label + " = " + y);
		                }
		            }
		            else {
		                $("#tooltip").remove();
		                previousPoint = null;            
		            }
		        //}
		    });

		
		var plotObject;
		if (metric=="COUNT")
			plotObject=$.plot(plot, [ series, extraMetricGraph ], options);
		else 
			plotObject=$.plot(plot, [ series, meanGraph, sdLowerGraph,
				sdUpperGraph, extraMetricGraph ], options);
		 plot.bind("plotclick", function (event, pos, item) {
		        if (item) {
		            //$("#clickdata").text("You clicked point " + item.dataIndex + " in " + item.series.label + ".");
		              var x = item.datapoint[0].toFixed(2);
//                    var y = item.datapoint[1].toFixed(2);
    
		            //plotObject.highlight(item.series, item.datapoint);
		            var gap=300000;
		            var startT=Math.floor(Number(x)-gap);
		            var finishT=Math.floor(Number(x)+gap);
		            window.open('http://localhost:9191/log?start=' + startT + '&finish='+ finishT +'&table=Dummy', 'logWindow', '');
		        }
		    });
	}
	
	function refreshGraph() {

		var dataurl = "data?table="+ tablename + "&field=" + field + "&start=" + startTime
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

function addGraph(main,graphId) {
	var field = getURLParameter("field"+graphId);
	if (field=="null") return;
	
	main
			.append(
					'<div class="graphParent"><div id="title'+graphId+'" class="graphTitle"></div><div id="graph'+graphId+'" class="graph"></div><img id="refresh'+graphId+'" src="images/refresh.png" class="refresh"/><textarea id="sql'+graphId+'" class="sql"></textarea></div>');

	$("#graph" + graphId).css("width", getURLParameter("width"));
	$("#graph" + graphId).css("height", getURLParameter("height"));
	$(".graphParent").css("width", getURLParameter("width"));
//	$(".graphParent").css("height", getURLParameter("height"));
	
	// parse parameters from the url
	var now = new Date().getTime();
	//var field = getURLParameter('field');
	var tablename = getURLParameter("table");
	if (tablename == null || tablename == "null")
		tablename = "Entry";
	var buckets = Number(getURLParameter("buckets"));
	var interval = extractPeriod(getURLParameter("interval"));
	var title = getURLParameter("title"+ graphId);
	if (title=="null")
		title=field;
	$("#title"+graphId).text(title);

	var n;
	if (buckets == 0)
		//no aggregation
		n = 1;
	else
		n = buckets;

	var finishTime = getURLParameter("finish");
	if (finishTime == "now") {
		var startTime = now - n * interval;
	} else
		//TOOD parse finish time/start time from url parameters
		startTime = now - n * interval;
	var metric = getURLParameter("metric");
	var extraMetric = getURLParameter("extraMetric");

	//draw the graphs
	drawGraph(field, tablename, buckets, interval, startTime, metric,extraMetric,
			$("#graph" + graphId), $("#refresh" + graphId), $("#sql"
					+ graphId));
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

function setTitle(title) {
	var s= getURLParameter("title");
	if (s!="null")
		title.text(s);
}