function graphPointBTC(series, x){
	var url = "https://api.bitso.com/v3/ticker/";
	var y;
	$.getJSON(url, {
		format : "json"
	}).done(function(data) {
		var payload = data.payload;
		$.each(data.payload, function(i, item) {
			if (item.book === "btc_mxn") {
				y = parseFloat(item.last);
			}
		});
		series.addPoint([x, y], true, true);
		console.info(x + ' - ' + y);
	});
	
	return y;
};


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
//                        graphPointBTC(series, x);
                        var y = Math.random()*1000;
                        series.addPoint([x, y], true, true);
                    }, 1000);
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
            name: 'Random data',
            data: (function () {
                // generate an array of random data
                var data = [],
                    time = (new Date()).getTime(),
                    i;

//                for (i = -19; i <= 0; i += 1) {
//                    data.push({
//                        x: time + i * 1000,
//                        y: Math.random()
//                    });
//                }
				data.push({
					x : time + i * 1000,
					y : Math.random()
				});
                return data;
            }())
        }]
    });
});


