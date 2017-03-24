//PROD
//var MAPS_IMR_NO = "http://maps.imr.no/geoserver/wms?";

//TEST: on local machine - use your ip address instead of localhost 
var MAPS_IMR_NO = "http://geb-test.nodc.no/geoserver/wms?";

//DEV:
//var MAPS_IMR_NO = "http://10.1.9.138:8080/geoserver/wms?";

var NORMAR_GRID = 11;

var BASE_URL = location.href.substring(0,location.href.lastIndexOf('/')) + "/";

//var BASE_URL = "http://10.1.9.230:9090/"; 

var comboboxGrid = ""; 
var comboboxSpecies = "";
//var comboboxParameter = "";
var comboboxPeriod = ""; 
var comboboxDepth = ""; //jQuery("#depthlayer :selected").val(),
var displayType = "";
var layername = "";
var displayName = "";

function drawmap(mapp) {
	jQuery.support.cors = true;

	var onSuccessFunction = function (message) {
	    addLayerToMap(layername, message, mapp);
	    addPdfGenerationToStack(displayName, comboboxSpecies, paramNames.toString(), comboboxPeriod, comboboxDepth, displayType);
	    initializeStack();
	}
	
	var onErrorFunction = function(req, status, errThrown) {
	    alert("ie error req:"+req+" status:"+status+" errThrown:"+errThrown);
	}
	createSLD(onSuccessFunction, onErrorFunction);
}

function addLayerToMap(layername, message, mapp) {

    var params = "'" + parameterIds.toString().replace(/,/g,' ') + "'";
    var depths = "'" + comboboxDepth.toString().replace(/,/g,' ') + "'";
    var periods = "'" + comboboxPeriod.toString().replace(/,/g,' ') + "'";
    
    var layername = "barmarPointvalueAggregate";
    if ( $("#visning option:selected").attr('id') === 'arealvisning' ) {
    	layername = "barmarAreavalueAggregate";
    }
    
    var postgisLayer = new ol.layer.Tile({
        source: new ol.source.TileWMS({
            url: MAPS_IMR_NO,
            params: {
                'LAYERS': layername, 
                'TRANSPARENT': 'true',
                sld: BASE_URL + "getsld?file=" + message.filename, 
                viewparams:"agridname:'BarMar';parameter_ids:"+params+
            	";depthlayername:"+depths+
            	";periodname:"+periods+
            	";aggregationfunc:'"+ $('#aggregation option:selected').attr('id') +"'"
            }
        }),
        'name': displayName
    });    

    var layerSrc = postgisLayer.getSource(); 
//    layerSrc.on('tileloadstart', function() { //sld creation takes too long
//    	console.log('imageloadstart event fired');
//        $("#progress").removeAttr("style");
//        $("#progress").css("display", "inline");
//      });
    layerSrc.on('tileloadend', function() {
    	console.log('imageloaded event fired');
        $("#progress").removeAttr("style");
        $("#progress").css("display", "none");
      });
    layerSrc.on('tileloaderror', function() {
        $("#progress").removeAttr("style");
        $("#progress").css("display", "none");
        console.log("feil ved lasting av postgis lag");
      });

    
    mapp.addLayer(postgisLayer);
    
    var src = MAPS_IMR_NO + "service=WMS&version=1.1.1&request=GetLegendGraphic&layer="+layername+
    	"&width=22&height=24&format=image/png&SLD="+BASE_URL + "getsld?file=" + message.filename;
    jQuery("#legend").append('<p id='+displayName+'>'+displayName+'<br/><img src='+src+' /></p>');
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

function downloadMap() {
	readBarMar();
    jQuery.ajax({
        url:"downloadData",
        data:{
            grid : comboboxGrid,
            parameter: paramNames,
            time: periodNames,
            depth: depthNames
        },
        method: "post",
        success: function(message){
            //alert(message);
        	var blob = new Blob([message], {type: "text/plain;charset=utf-8"});
        	saveAs(blob, comboboxGrid+"_"+paramNames+"_"+periodNames+"_"+depthNames+".csv");
        },
        error: function(req, status, errThrown) {
            onErrorFunction(req, status, errThrown);
        }
    });
}

function createSLD(onSuccessFunction, onErrorFunction) {
    
	readBarMar();
    jQuery.ajax({
        url:"createBarMarsld",
        data:{
            grid : comboboxGrid,
            parameter: paramNames,
            time: comboboxPeriod,
            depth: comboboxDepth,
            displaytype: displayType,
            aggregationFunc: $('#aggregation option:selected').attr('id')
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

var paramNames = [];
var depthNames = [];
var periodNames = [];
var parameterIds = "";
function readBarMar() {
	comboboxGrid = "BarMar"; //$("#grid").parents(".dropdown").find('.btn').val();
	comboboxSpecies = $("#speciesselect").val();
	
	var parameterNames = $("#speciesSubGroupselect option:selected");
	for ( i=0; i < parameterNames.length; i++ ) {
		paramNames[i] = $(parameterNames[i]).text();  
	}
	var depthNamesJquery = $("#depthselect option:selected");
	for ( i=0; i < depthNamesJquery.length; i++ ) {
		depthNames[i] = $(depthNamesJquery[i]).attr("id");  
	}
	var periodNamesJquery = $("#periodselect option:selected");
	for ( i=0; i < periodNamesJquery.length; i++ ) {
		periodNames[i] = $(periodNamesJquery[i]).attr("id");  
	}
	

	$('#speciesSubGroupselect option:selected').each(function(i, selected){
		if ( i > 0 ) parameterIds += " "+$(selected).attr("id"); 
		else parameterIds = $(selected).attr("id");
	});

	//TOD: remove me
	$('#depthselect option:selected').each(function(i, selected){
		if ( i > 0 ) comboboxDepth += " "+$(selected).text(); 
		else comboboxDepth = $(selected).text()
	});
	//TOD: remove me
	$('#periodselect option:selected').each(function(i, selected){
		if ( i > 0 ) comboboxPeriod += " "+$(selected).attr('id'); 
		else comboboxPeriod = $(selected).attr('id');
	});

	displayType = $('#visning option:selected').attr('id');
	displayName = createDisplayName(paramNames.toString(), comboboxPeriod, comboboxDepth, displayType);
	
	if ( comboboxPeriod.indexOf("Aggregated") != -1 ) comboboxPeriod = "F";
	if ( comboboxDepth.indexOf("Aggregated") != -1 ) comboboxDepth = "F";
}

function createPDF(mapp) {
    
	readBarMar();
	
    var onSuccessFunction = function (message) {
        var showLayers = showVisibleLayers(mapp);
        
        var projection = mapp.getView().getProjection().getCode();
        console.log("srs:"+projection);
        var pdfUrl = "createpdfreport?bbox="+mapp.getView().calculateExtent(map.getSize())+
            "&sld="+BASE_URL + "getsld?file=" + message.filename +
            "&srs="+projection+
            "&layer="+layername+
            "&layerson="+showLayers+
            "&width="+mapp.getSize()[0]+ //width
            "&height="+mapp.getSize()[1]+ //height
            "&displayLayerName="+displayName;         
        
        $("#progress").removeAttr("style");
        $("#progress").css("display", "inline");
        jQuery.ajax({
            url: pdfUrl,
            method: "post",
            success: function(data){
                var mapImageLink = jQuery("<a id='downloadPrintMap' href='getPDF?printFilename="+data.filename+"' hidden download='" + data.filename + "'></a>");
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
                console.log("feil ved lasting av postgis lag:"+errThrown);
            }
        });    
    }
    
    var onErrorFunction = function(req, status, errThrown) {
        console.log("ie error req:"+req+" status:"+status+" errThrown:"+errThrown);
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

function createDisplayName(param, period, depth, displayType) {
	
	var adisplayName = param;
    if ( period.indexOf( 'Aggregated' ) > -1 ) {
        adisplayName+= "_All";
    } 
    if ( depth.indexOf( 'Aggregated' ) > -1 ) {
        adisplayName+= "_All";
    }
    adisplayName += "_" + displayType;
    adisplayName +=  "_ " + $('#aggregation option:selected').attr('id');
    return adisplayName;
}


