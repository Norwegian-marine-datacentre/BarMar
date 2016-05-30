proj4.defs('EPSG:32633', '+proj=utm +zone=33 +ellps=WGS84 +datum=WGS84 +units=m +no_defs');
var proj32633 = ol.proj.get('EPSG:32633');
proj32633.setExtent([-2500000.0,3500000.0,3045984.0,9045984.0]);

