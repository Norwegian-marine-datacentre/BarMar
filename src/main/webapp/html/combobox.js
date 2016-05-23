var felayer;

//PROD
//var MAPS_IMR_NO = "http://maps.imr.no/geoserver/wms?";

//TEST: on local machine - use your ip address instead of localhost 
//var MAPS_IMR_NO = "http://geb-test.nodc.no/geoserver/wms?";

var MAPS_IMR_NO = "http://10.1.9.230:8080/geoserver/wms?";

var NORMAR_GRID = 11;

var BASE_URL = location.href.substring(0,location.href.lastIndexOf('/')) + "/";

//var BASE_URL = "http://10.1.9.230:9090/"; // for development

var comboboxGrid = ""; 
var comboboxParameter = ""; 
var comboboxPeriod = ""; 
var comboboxDepth = ""; //jQuery("#depthlayer :selected").val(),
var displayType = "";
var layername = "";

function drawmap(mapp){
	jQuery.support.cors = true;

	var onSuccessFunction = function (message) {
	    if(felayer != null){
	        mapp.removeLayer(felayer);
	    }
	    addLayerToMap(layername, message, mapp);
	}
	
	var onErrorFunction = function(req, status, errThrown) {
	    alert("ie error req:"+req+" status:"+status+" errThrown:"+errThrown);
	}
	createSLD(onSuccessFunction, onErrorFunction);
}

function addLayerToMap(layername, message, mapp) {
    src = MAPS_IMR_NO + "service=WMS&version=1.1.1&request=GetLegendGraphic&layer="+
	layername+"&width=22&height=24&format=image/png&SLD="+BASE_URL + "getsld?file=" + message;
    
    felayer = new ol.layer.Image({
        source: new ol.source.ImageWMS({
            url: MAPS_IMR_NO,
            params: {
                'LAYERS': layername,
                'TRANSPARENT': 'true',
                sld: BASE_URL + "getsld?file=" + message,  
            },
        'zIndex': 2000
        })
    });
    mapp.addLayer(felayer);

    var layerss = mapp.getLayers();
    for ( var i=0; i < mapp.getLayers().array_.length;i++) {
        var alayer = mapp.getLayers().array_[i]
        console.log("zIndex:"+alayer.values_.zIndex);
    }
    
    /** dns redirect to crius.nodc.no/geoserver/wms */
    var src = MAPS_IMR_NO + "service=WMS&version=1.1.1&request=GetLegendGraphic&layer="+
    	layername+"&width=22&height=24&format=image/png&SLD="+BASE_URL + "getsld?file=" + message;
    jQuery("#legend").attr("src",src);      
}

function getHTTPObject() {
    if (typeof XMLHttpRequest != 'undefined') {
        return new XMLHttpRequest();
    }
    try {
        return new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
        try {
            return new ActiveXObject("Microsoft.XMLHTTP");
        } catch (e) {}
    }
    return false;
}

function createSLD(onSuccessFunction, onErrorFunction) {
    
    jQuery.ajax({
        url:"createsld",
        data:{
            grid : comboboxGrid,
            parameter: comboboxParameter,
            time: comboboxPeriod,
            depth: comboboxDepth,
            displaytype: displayType
        },
        method: "post",
        success: function(message){
            onSuccessFunction(message);
        },
        error: function(req, status, errThrown) {
            onErrorFunction(req, status, errThrown);
        }
    });    
}

function createPDF(mapp) {
    
    var onSuccessFunction = function (message) {
        var showLayers = showVisibleLayers(mapp);
        
        console.log("bbox:"+mapp.getView().calculateExtent(mapp.getSize()));
        console.log("width:"+mapp.getSize()[0]);
        console.log("width:"+mapp.getSize().w);
        
        var pdfUrl = "createpdfreport?bbox="+mapp.getView().calculateExtent(map.getSize())+
            "&sld="+BASE_URL + "getsld?file=" + message +
            "&srs="+mapp.getView().getProjection()+
            "&layer="+layername+
            "&layerson="+showLayers+
            "&width="+mapp.getSize()[0]+ //width
            "&height="+mapp.getSize()[1]; //height        
        
        document.getElementById("hidden_pdf").action = pdfUrl;
        jQuery("#hidden_pdf").submit();        
    }
    
    var onErrorFunction = function(req, status, errThrown) {
        alert("ie error req:"+req+" status:"+status+" errThrown:"+errThrown);
    }
    
    createSLD(onSuccessFunction, onErrorFunction);
    
//	var showLayers = showVisibleLayers();
//	var displayType = "";
//	if (layername == LAYER_POINTVALUE) displayType = PUNKTVISNING;
//	else displayType = AREALVISNING;
//    jQuery.ajax({
//        url: "spring/createsld",
//        data:{
//            grid : jQuery("#grid :selected").val(),
//            parameter: jQuery("#parameter :selected").val(),
//            time: jQuery("#period :selected").val(),
//            depth: jQuery("#depthlayer :selected").val(),
//            displaytype: displayType
//        },
//        method: "post",
//        success: function(message){
//
//        }
//    });
}

function showVisibleLayers(mapp) {
	var tmpLayers = mapp.getLayers();
	var strLayers = "";
	for(var i=0; i<tmpLayers.length; i++) {
		var aLayer = tmpLayers[i];
		if ( aLayer.getVisibility() == true && !(aLayer instanceof OpenLayers.Layer.Vector) 
				&& aLayer.name != "BarMarGrid" 
					&& aLayer.name != "Europakart" ) {
			var tmpUrl = aLayer.getFullRequestString({});
			tmpUrl = escape(tmpUrl);
			strLayers += tmpUrl + "-";
		}
	}
//	alert("strLayers:"+strLayers);
	return strLayers;
}


