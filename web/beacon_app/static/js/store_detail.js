// Render the list of beacons in an interactive overlay over the store blueprint
//      d3 - library object

function Visualiser(root_container_id, beacons, store) {
    // Data array containing a list of beacons
    this.beacons = beacons;
    // Data object containing store metadata
    this.store = store;
    this.root_id = root_container_id;

    this['heatmap'] = this.drawHeatmap;
    this['polyline'] = this.drawPolyline;

    this.vis_method = null;

    // Load position scans from db
    var self = this;
    this.load_scans(function(scans) {
        self.scans = scans;
        if (self.vis_method) {
            self.visualize(self.vis_method);
        }
    });
};

Visualiser.prototype.visualize = function(type) {
    // If scans aren't loaded save vis type for later
    this.vis_method = type;
    if (!this.scans) {
        return
    }

    if (this.hasOwnProperty(type)) {
        this.cleanupStage();
        this[type]();
    }
    else {
        console.log("Error: unrecognized visualization method.");
    }
};

Visualiser.prototype.cleanupStage = function() {
    // Remove any existing canvas or svg object in rootid
    var root = document.getElementById(this.root_id);
    while (root.firstChild) {
        root.removeChild(root.firstChild);
    }
};

Visualiser.prototype.drawHeatmap = function() {
    console.log("Drawing heatmap")

    // Set up store floorplan on a draggable 'map display'
    // Use d3 zoom behavior for this
    var width = 960,
        height = 500;    
    var imWidth = 5100,
        imHeight = 3300;
    scale = width / imWidth;

    var bgImage = $('<img>', { 
        src:this.store.image_url,
        width:Math.round(scale*imWidth),
        height:Math.round(scale*imHeight) 
    });
    $("#"+this.root_id).append(bgImage);


    // Init heatmap canvas
    var root_id = this.root_id;
    var config = {
        element: document.getElementById(root_id),
        radius: 40,
        opacity: 50
    };
    var heatmap = h337.create(config);

    // Draw heatmap over the map
    for (var i = 0; i < this.scans.length; i++) {
        // Fill a string with all of the points to put in the polyline
        var path = this.scans[i];
        for (var j = 0; j < path.length; j++) {
            var scan = path[j];
            var storeX = scan.x * this.store.scale * scale;
            var storeY = scan.y * this.store.scale * scale;
            heatmap.store.addDataPoint(storeX, storeY, Math.floor(Math.random() * 3 + 2));        
        }
    }
};

Visualiser.prototype.drawPolyline = function() {
    console.log("Drawing polyline");

    // Init svg stage
    var svg = this.init_svg(this.root_id);

    // Draw beacons
    for (var i = 0; i < this.beacons.length; i++) {
        // Add this beacon as a circle on the svg overlay
        // x, y, address
        var beacon = this.beacons[i]; 
        var beaconX = this.store.scale * beacon.x;
        var beaconY = this.store.scale * beacon.y;
        svg.append("circle")
            .attr("cx", beaconX)
            .attr("cy", beaconY)
            .attr("r", 24)
            .style("fill", "#ff4136")
            .style("stroke", "#ff291c")
            .style("stroke-width", "3");
    }

    // Draw a polyline for each scan
    for (var i = 0; i < this.scans.length; i++) {
        // Fill a string with all of the points to put in the polyline
        var polyPoints = "";
        var path = this.scans[i];
        for (var j = 0; j < path.length; j++) {
            var scan = path[j];
            var storeX = scan.x * this.store.scale;
            var storeY = scan.y * this.store.scale;
            polyPoints += "" + storeX + "," + storeY + " ";
        }

        // Draw the polyline
        svg.append("polyline")
            .attr("points", polyPoints)
            .style("fill","none")
            .style("stroke-width","8")
            .style("stroke","#0c516c");
    }
};

Visualiser.prototype.draw_scans = function(scans, render_type) {
    // Scans is a nested array of user paths
    if (!scans)
        return;

    if (render_type == 'heatmap') {
        var map = h337.create({
            "element":document.getElementById("app_container"),
            "radius":25,
            "visible":true
        })

        map.get("canvas").onclick = function(ev){
            var pos = h337.util.mousePosition(ev);
            map.store.addDataPoint(pos[0],pos[1]);
        };  
    }
    if (render_type == 'polyline') {
    }
};

Visualiser.prototype.load_scans = function(callback) {
    // Load beacons from json file on server
    console.log("Loading scans...")
    $.getJSON("scans", function(data) {
        // See if loading was a success
        if (data.result === "success") {
            console.log("Scans loaded!");
            callback(data.paths);
        }
        else {
            console.log("ERROR: failed to load scans");
            callback();
        }
    });
};



Visualiser.prototype.init_svg = function(root_container_id) {
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
        .scaleExtent([.2, 10])
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
        .attr("width", 5100)
        .attr("height", 3300)
        .attr("xlink:href",this.store.image_url);

    return content;
};

