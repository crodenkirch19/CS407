// Render the list of beacons in an interactive overlay over the store blueprint
//      beacons - array of javascript beacons
//      d3 - library object

var margin = {top: 20, right: 20, bottom: 30, left: 40},
    width = 960 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

var x = d3.scale.linear()
    .domain([-width / 2, width / 2])
    .range([0, width]);

var y = d3.scale.linear()
    .domain([-height / 2, height / 2])
    .range([height, 0]);

var xAxis = d3.svg.axis()
    .scale(x)
    .orient("bottom")
    .tickSize(-height);

var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left")
    .ticks(5)
    .tickSize(-width);

var zoom = d3.behavior.zoom()
    .x(x)
    .y(y)
    .scaleExtent([1, 10])
    .on("zoom", zoomed);

var svg = d3.select("#app_container").append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
    .call(zoom);

svg.append("rect")
    .attr("width", width)
    .attr("height", height);

svg.append("image")
    .attr("x","0")
    .attr("y","0")
    .attr("width",width)
    .attr("height",height)
    .attr("xlink:href",store.image_url)

svg.append("g")
    .attr("class", "x axis")
    .attr("transform", "translate(0," + height + ")")
    .call(xAxis);

svg.append("g")
    .attr("class", "y axis")
    .call(yAxis);

function zoomed() {
  svg.select(".x.axis").call(xAxis);
  svg.select(".y.axis").call(yAxis);
  var translate = zoom.translate();
  svg.select("image").attr("transform", 
    "translate(" + (margin.left+translate[0]) + "," + (margin.top+translate[1]) + "), " +
    "scale(" + zoom.scale() + ")");
}