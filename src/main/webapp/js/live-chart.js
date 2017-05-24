function graphPointBTC(series, x){
	var url = "https://api.bitso.com/v3/ticker/";
	var y;
	$.getJSON(url, {
	}).done(function(data) {
		var payload = data.payload;
		$.each(data.payload, function(i, item) {
			if (item.book === "btc_mxn") {
				y = parseFloat(item.last);
			}
		});
		series.addPoint([x, y], true, true);
	});
	
	return y;
};

function sortJSON(data, key, way) {
    return data.sort(function(a, b) {
        var x = a[key]; var y = b[key];
        if (way === '123' ) { return ((x < y) ? -1 : ((x > y) ? 1 : 0)); }
        if (way === '321') { return ((x > y) ? -1 : ((x < y) ? 1 : 0)); }
    });
}

$.getJSON('https://bitcoin-164116.appspot.com/_ah/api/bitcoin/v1/history?days=0', function (json) {

	$(document).ready(function () {
	    Highcharts.setOptions({
	        global: {
	            useUTC: false
	        }
	    });
	
	    Highcharts.chart('live-chart', {
	        chart: {
	            type: 'spline',
	            animation: Highcharts.svg, // don't animate in old IE
	            marginRight: 10,
	            events: {
	                load: function () {
	                    // set up the updating of the chart each second
	                    var series = this.series[0];
	                    setInterval(function () {
	                        var x = (new Date()).getTime(); // current time
	                        graphPointBTC(series, x);
	//                        series.addPoint([x, y], true, true);
	                    }, 1000 * 60);
	                }
	            }
	        },
	        title: {
	            text: 'Live BTC a MXN'
	        },
	        xAxis: {
	            type: 'datetime',
	            tickPixelInterval: 150
	        },
	        yAxis: {
	            title: {
	                text: 'Value'
	            },
	            plotLines: [{
	                value: 0,
	                width: 1,
	                color: '#808080'
	            }]
	        },
	        tooltip: {
	            formatter: function () {
	                return '<b>' + this.series.name + '</b><br/>' +
	                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
	                    Highcharts.numberFormat(this.y, 2);
	            }
	        },
	        legend: {
	            enabled: false
	        },
	        exporting: {
	            enabled: false
	        },
	        series: [{
	            name: 'BTC to MXN',
	            data: (function () {
	                var data = [],
	                    now = (new Date()).getTime();
	                var history2 = sortJSON(json.history,'created_at', '123');
	                $.each(history2, function( index, item ) {
	                	if(item.book === 'btc_mxn') {
	                		var time = new Date(item.created_at).getTime();
	                        var y = parseFloat(item.value);
	                		for(i=0; i < 30 && time + i * 1000 * 60 * 2 < now; i++)
		                		data.push({x: time + i * 1000 * 60 * 2 , y });
//	                		data.push({
//	                			x: time + index * 1000*60*60,
//	                			y: parseFloat(item.value)
//	                		});
	                	}
	                });
	                return data;
	            }())
	        }]
	    });
	});
});

