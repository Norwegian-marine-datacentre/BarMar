var felayer;

//PROD
//var MAPS_IMR_NO = "http://maps.imr.no/geoserver/wms?";

//TEST: on local machine - use your ip address instead of localhost 
//var MAPS_IMR_NO = "http://geb-test.nodc.no/geoserver/wms?";

var MAPS_IMR_NO = "http://10.1.9.230:8080/geoserver/wms?";

var NORMAR_GRID = 11;

var BASE_URL = location.href.substring(0,location.href.lastIndexOf('/')) + "/";

var BASE_URL = "http://10.1.9.230:9090/"; // for development

var comboboxGrid = ""; 
var comboboxParameter = ""; 
var comboboxPeriod = ""; 
var comboboxDepth = ""; //jQuery("#depthlayer :selected").val(),
var displayType = "";
var layername = "";

function drawmap(mapp) {
	jQuery.support.cors = true;

	var onSuccessFunction = function (message) {
	    addLayerToMap(layername, message, mapp);
	    initializeStack();
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
            }
        }),
        'zIndex': 2000,
        'name': comboboxParameter
    });
    mapp.addLayer(felayer);
    
    felayer.getSource().on('imageloadstart', function() {
        $("#progress").removeAttr("style");
        $("#progress").css("display", "inline");
      });

    felayer.getSource().on('imageloadend', function() {
        $("#progress").removeAttr("style");
        $("#progress").css("display", "none");
      });
    felayer.getSource().on('imageloaderror', function() {
        alert("feil ved lasting av postgis lag")
      });
    
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
        
        var pdfUrl = "createpdfreport?bbox="+mapp.getView().calculateExtent(map.getSize())+
            "&sld="+BASE_URL + "getsld?file=" + message +
            "&srs="+mapp.getView().getProjection()+
            "&layer="+layername+
            "&layerson="+showLayers+
            "&width="+mapp.getSize()[0]+ //width
            "&height="+mapp.getSize()[1]+ //height
            "&displayLayerName="+comboboxParameter;         
        
        $("#progress").removeAttr("style");
        $("#progress").css("display", "inline");
        jQuery.ajax({
            url: pdfUrl,
            method: "post",
            success: function(data){
                var mapImageLink = jQuery("<a id='downloadPrintMap' href='/getPDF?printFilename="+data.filename+"' hidden download='" + data.filename + "'></a>");
                jQuery('body').append(mapImageLink);
                var formDocument = document.getElementById("downloadPrintMap");
                formDocument.click();
                formDocument.remove();

                $("#progress").removeAttr("style");
                $("#progress").css("display", "none");
            },
            error: function(req, status, errThrown) {
                $("#progress").removeAttr("style");
                $("#progress").css("display", "none");
                alert("feil ved lasting av postgis lag");
            }
        });    
    }
    
    var onErrorFunction = function(req, status, errThrown) {
        alert("ie error req:"+req+" status:"+status+" errThrown:"+errThrown);
    }
    
    createSLD(onSuccessFunction, onErrorFunction);
}

function ShowLoading() {
    $("#progress").removeAttr("style");
    $("#progress").css("display", "inline");
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
	return strLayers;
}


