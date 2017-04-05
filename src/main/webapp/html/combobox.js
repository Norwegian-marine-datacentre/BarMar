//PROD
//var MAPS_IMR_NO = "http://maps.imr.no/geoserver/wms?";

//TEST: on local machine - use your ip address instead of localhost 
var MAPS_IMR_NO = "http://geb-test.nodc.no/geoserver/wms?";

//DEV:
//var MAPS_IMR_NO = "http://10.1.9.138:8080/geoserver/wms?";

var BASE_URL = location.href.substring(0,location.href.lastIndexOf('/')) + "/";

//var BASE_URL = "http://10.1.9.230:9090/"; 

var comboboxGrid = ""; 

var displayType = "";
var layername = "";
var displayName = "";

function drawmap(mapp) {
	jQuery.support.cors = true;
	

	var onSuccessFunction = function (message) {
	    addLayerToMap(layername, message, mapp);
	    var aggregationfunc = $('#aggregation option:selected').attr('id')
	    addPdfGenerationToStack(displayName, parameterIds, paramNames, periodNames, depthNames, displayType, aggregationfunc);
	    initializeStack();
	}
	
	var onErrorFunction = function(req, status, errThrown) {
	    alert("ie error req:"+req+" status:"+status+" errThrown:"+errThrown);
	}
	readBarMar();
	createSLD(onSuccessFunction, onErrorFunction);
}

function addLayerToMap(layername, message, mapp) {

    var params = "'" + parameterIds.toString().replace(/,/g,' ') + "'";
    var depths = "'" + depthNames.toString().replace(/,/g,' ') + "'"; 
    var periods = "'" + periodNames.toString().replace(/,/g,' ') + "'"; 
    
    var postgisLayer = new ol.layer.Tile({
        source: new ol.source.TileWMS({
            url: MAPS_IMR_NO,
            params: {
                'LAYERS': layername, 
                'TRANSPARENT': 'true',
                sld: BASE_URL + "getsld?file=" + message.filename, 
                viewparams:"agridname:'"+comboboxGrid+"';parameter_ids:"+params+
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
        	var blob = new Blob([message], {type: "text/plain;charset=utf-8"});
        	saveAs(blob, comboboxGrid+"_"+paramNames+"_"+periodNames+"_"+depthNames+".csv");
        },
        error: function(req, status, errThrown) {
            onErrorFunction(req, status, errThrown);
        }
    });
}

function createSLD(onSuccessFunction, onErrorFunction) {
    
    jQuery.ajax({
        url:"createBarMarsld",
        data:{
            grid : comboboxGrid,
            parameter: paramNames,
            time: periodNames,
            depth: depthNames,
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
	//comboboxGrid = "BarMar"; //$("#grid").parents(".dropdown").find('.btn').val();
	if (window.location.href.indexOf("normar") > -1 ) {
		comboboxGrid = "NorMar";
	    layername = "normarPointvalueAggregate";
	    if ( displayType === 'arealvisning' ) {
	    	layername = "normarAreavalueAggregate";
	    }
	} else {
		comboboxGrid = "BarMar";
	    layername = "barmarPointvalueAggregate";
	    if ( displayType === 'arealvisning' ) {
	    	layername = "barmarAreavalueAggregate";
	    }
	}
	
	
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

	displayType = $('#visning option:selected').attr('id');
	displayName = createDisplayName(paramNames.toString(), periodNames.toString(), depthNames.toString(), displayType);
	

}

function createPDF(mapp, queryObj ) {

	
    var onSuccessFunction = function (message) {
        
        var projection = mapp.getView().getProjection().getCode();
        projection = "EPSG:3575";
        console.log("srs:"+projection);
        var pdfUrl = "createpdfreport?bbox="+this.map.getView().calculateExtent(this.map.getSize())+
            "&sld="+BASE_URL + "getsld?file=" + message.filename +
            "&srs="+projection+
            "&layer="+layername+
            "&width="+mapp.getSize()[0]+ //width
            "&height="+mapp.getSize()[1]+ //height
            "&displayLayerName="+displayName+
            "&viewparams=agridname:"+"'BarMar'"+
            	";parameter_ids:'"+queryObj.parameterIds+
            	"';depthlayername:'"+queryObj.depthNames+
            	"';periodname:'"+queryObj.periodNames+
            	"';aggregationfunc:'"+queryObj.aggregationfunc+"'";
        
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

function createDisplayName(param, period, depth, displayType) {
	
    var depths = depthNames.toString().replace(/,/g,'_'); 
    var periods = periodNames.toString().replace(/,/g,'_');
    var aggregationOption = $('#aggregation option:selected').attr('id');
    
	var adisplayName = param;
	adisplayName += "_" + depths;
	adisplayName += "_" + periods;
    adisplayName += "_" + displayType;
    adisplayName +=  "_" + aggregationOption
    return adisplayName;
}


