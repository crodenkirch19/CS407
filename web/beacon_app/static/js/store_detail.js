// Render the list of beacons in an interactive overlay over the store blueprint
//      d3 - library object

function Visualiser(root_container_id, beacons, store) {
    // Data array containing a list of beacons
    this.beacons = beacons;

    // Data object containing store metadata
    this.store = store;

    // d3 svg object we use for our interactive Visualiser    
    this.svg = this.init(root_container_id);

    this.draw_beacons(beacons);

    var self = this;
    this.load_scans(function(scans) {
        // 'heatmap', 'polyline'
        self.draw_scans(scans, 'heatmap');
    });
}

Visualiser.prototype.draw_beacons = function(beacons) {
    if (!beacons)
        return;

    for (var i = 0; i < beacons.length; i++) {
        // Add this beacon as a circle on the svg overlay
        // x, y, address
        var beacon = beacons[i]; 
        var beaconX = this.store.scale * beacon.x;
        var beaconY = this.store.scale * beacon.y;
        console.log(beaconX, beaconY);
        this.svg.append("circle")
            .attr("cx", beaconX)
            .attr("cy", beaconY)
            .attr("r", 4)
            .style("fill", "FireBrick")
            .style("stroke", "Maroon");
    }
};

// TODO add more visualisations
// TODO heatmap http://www.patrick-wied.at/static/heatmapjs/#
// TODO cloud of points

Visualiser.prototype.draw_scans = function(scans, render_type) {
    // Scans is a nested array of user paths
    if (!scans)
        return;

    if (render_type == 'heatmap') {
        // var map = h337.create({
        //     "element":document.getElementById("app_container"),
        //     "radius":25,
        //     "visible":true
        // })

        // map.get("canvas").onclick = function(ev){
        //     var pos = h337.util.mousePosition(ev);
        //     map.store.addDataPoint(pos[0],pos[1]);
        // };
            
    }
    if (render_type == 'polyline') {
        // Draw a polyline for each scan
        for (var i = 0; i < scans.length; i++) {
            // Fill a string with all of the points to put in the polyline
            var polyPoints = "";
            var path = scans[i];
            for (var j = 0; j < path.length; j++) {
                var scan = path[j];
                var storeX = scan.x * this.store.scale;
                var storeY = scan.y * this.store.scale;
                polyPoints += "" + storeX + "," + storeY + " ";
            }

            // Draw the polyline
            this.svg.append("polyline")
                .attr("points", polyPoints)
                .style("fill","none")
                .style("stroke-width","4")
                .style("stroke","Sienna");
        }
    }
};

Visualiser.prototype.load_scans = function(callback) {
    // Load beacons from json file on server

    $.getJSON("scans", function(data) {
        // See if loading was a success
        if (data.result === "success") {
            callback(data.paths);
        }
        else {
            console.log("ERROR: failed to load scans");
            callback();
        }
    });
};



Visualiser.prototype.init = function(root_container_id) {
    var zoomed = function() {
      var translate = zoom.translate();
      content.attr("transform", 
        "translate(" + (translate[0]) + "," + (translate[1]) + "), " +
        "scale(" + zoom.scale() + ")");
    };


    // Set up store floorplan on a draggable 'map display'
    // Use d3 zoom behavior for this
    var width = 960,
        height = 500;

    var x = d3.scale.linear()
        .domain([-width / 2, width / 2])
        .range([0, width]);

    var y = d3.scale.linear()
        .domain([-height / 2, height / 2])
        .range([height, 0]);

    var zoom = d3.behavior.zoom()
        .x(x)
        .y(y)
        .scaleExtent([1, 10])
        .on("zoom", zoomed);

    var svg = d3.select("#" + root_container_id).append("svg")
        .attr("width", width)
        .attr("height", height)
      .append("g")
        .call(zoom);

    svg.append("rect")
        .attr("width", width)
        .attr("height", height);

    var content = svg.append("g");

    content.append("image")
        .attr("x","0")
        .attr("y","0")
        .attr("width",width)
        .attr("height",height)
        .attr("xlink:href",this.store.image_url);

    return content;
};

