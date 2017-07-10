var chartArray = [];
var timerId;

function graphPointBTC(x) {
	var url = "https://api.bitso.com/v3/ticker/";
	var y;
	$.getJSON(url, {}).done(function(data) {
		var payload = data.payload;
		$.each(chartArray, function(i, chart) {
			$.each(data.payload, function(j, item) {
				if (item.book === chart.currency + "_mxn") {
					var value;
					if(chart.type === "bid")
						value = item.bid;
					if(chart.type === "ask")
						value = item.ask;
					if(chart.type === "last")
						value = item.last;
					y = parseFloat(value);
				}
			});
			chart.chart.series[0].addPoint([ x, y ], true, true);
			if (Math.abs(chart.last - y) / chart.last > 0.05)
				audioElement.play();
			chart.last = y;
		});
	});

	return y;
};

function sortJSON(data, key, way) {
	return data.sort(function(a, b) {
		var x = a[key];
		var y = b[key];
		if (way === '123') {
			return ((x < y) ? -1 : ((x > y) ? 1 : 0));
		}
		if (way === '321') {
			return ((x > y) ? -1 : ((x < y) ? 1 : 0));
		}
	});
}

function createLiveChart(currency, type, json) {
	return {
		currency : currency,
		type : type,
		chart : Highcharts.chart('live-chart-' + currency, {
			chart : {
				type : 'spline',
				animation : Highcharts.svg, // don't animate in old IE
				marginRight : 10,
				events : {
					load : function() {
						if (chartArray.length == 0) {
							timerId  = setInterval(function() {
								var x = (new Date()).getTime(); // current time
								graphPointBTC(x);
							}, 1000 * 30);
						}
					}
				}
			},
			title : {
				text : 'Live ' + currency.toUpperCase() + ' a MXN'
			},
			xAxis : {
				type : 'datetime',
				tickPixelInterval : 150
			},
			yAxis : {
				title : {
					text : 'Value'
				},
				plotLines : [ {
					value : 0,
					width : 1,
					color : '#808080'
				} ]
			},
			tooltip : {
				formatter : function() {
					return '<b>'
							+ this.series.name
							+ '</b><br/>'
							+ Highcharts
									.dateFormat('%Y-%m-%d %H:%M:%S', this.x)
							+ '<br/>' + Highcharts.numberFormat(this.y, 2);
				}
			},
			legend : {
				enabled : false
			},
			exporting : {
				enabled : false
			},
			series : [ {
				name : currency.toUpperCase() + ' to MXN',
				data : (function() {
					var data = [];
					$.each(json.history, function(index, item) {
						if (item.book.indexOf(currency) != -1) {
							var created = item.created_at.replace(' ', 'T')
									.replace(':00+00:00', 'Z');
							var time = new Date(created).getTime();
							var value;
							if(type === "bid")
								value = item.bid;
							if(type === "ask")
								value = item.ask;
							if(type === "last")
								value = item.last;
							var y = parseFloat(value);
							data.push({
								x : time - (1000 * 60),
								y : y
							});
							data.push({
								x : time,
								y : y
							});
						}
					});
					return data;
				}())
			} ]
		})
	}
}
function startLiveChart(type) {
	clearInterval(timerId);
	chartArray = [];
			$.getJSON('https://bitcoin-164116.appspot.com/_ah/api/bitcoin/v1/history?hours=6',
				function(json) {
					$(document).ready(
							function() {
								Highcharts.setOptions({
									global : {
										useUTC : false
									}
								});
								json.history = sortJSON(json.history,
										'created_at', '123');
								chartArray.push(createLiveChart('btc', type, json));
								chartArray.push(createLiveChart('eth', type, json));
								chartArray.push(createLiveChart('xrp', type, json));
							});
				});
};

if (typeof(Storage) !== "undefined") {
	var typeStored = localStorage.getItem("typeStored");
	if(typeStored != null) {
		$('#nav-'+typeStored).addClass('active');
		startLiveChart(typeStored);
	} else {
		$('#nav-bid').addClass('active');
		startLiveChart('bid');
	}
} else {
	$('#nav-bid').addClass('active');
	startLiveChart('bid');
}
function changeChart(type) {
	if (typeof(Storage) !== "undefined") {
		localStorage.setItem("typeStored", type);
	}
	if(type == 'bid') {
		$('#nav-bid').addClass('active');
		$('#nav-ask').removeClass('active');
	}
	if(type == 'ask') {
		$('#nav-ask').addClass('active');
		$('#nav-bid').removeClass('active');
	}
	startLiveChart(type);
}

var audioElement = document.createElement('audio');
audioElement.setAttribute('src',
		'https://bitcoin-164116.appspot.com/sound/chart-notification.mp3');
