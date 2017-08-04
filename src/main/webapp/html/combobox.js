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

var onErrorFunction = function(req, status, errThrown) {
	$("div#divLoading").removeClass('show');
    console.log("Error loading postgis layer - error req:"+req+" status:"+status+" errThrown:"+errThrown);
}
function drawmap(mapp, bboxValue) {
	jQuery.support.cors = true;

	var onSuccessFunction = function (message) {
	    addLayerToMap(layername, message, mapp, bboxValue);
	    var metadataRef = $('#metadata p').attr('id');
	    addPdfGenerationToStack(displayName, parameterIds, paramNames, periodNames, depthNames, aggregationfunc, logscale, displayType, metadataRef);
	    updateLayerStack( displayName, mapp );
	}
	readBarMar();
	createSLD(onSuccessFunction);
}

function addLayerToMap(layername, message, mapp, bboxValue) {

    var params = "'" + parameterIds.toString().replace(/,/g,' ') + "'";
    var depths = "'" + depthNames.toString().replace(/,/g,' ') + "'"; 
    var periods = "'" + periodNames.toString().replace(/,/g,' ') + "'"; 
    
    var bbox = this.map.getView().calculateExtent(this.map.getSize());
    if ( bboxValue != null) 
    	bbox = bboxValue;
    var postgisLayer = new ol.layer.Tile({
    	extent: bbox,
        source: new ol.source.TileWMS({
            url: MAPS_IMR_NO,
            params: {
                'LAYERS': layername, 
                'TRANSPARENT': 'true',
                sld: BASE_URL + "getsld?file=" + message.filename, 
                viewparams:"agridname:'"+comboboxGrid+"';parameter_ids:"+params+
            	";depthlayername:"+depths+
            	";periodname:"+periods+
            	";aggregationfunc:'"+ aggregationfunc +"'"
            }
        }),
        'name': displayName
    });    

    var layerSrc = postgisLayer.getSource(); 
    layerSrc.on('tileloadend', function() {
    	$('div#divLoading').removeClass('show');
    });
    layerSrc.on('tileloaderror', function() {
    	$('div#divLoading').removeClass('show');
        console.log("feil ved lasting av postgis lag");
    });
    mapp.addLayer(postgisLayer);
    
    var src = MAPS_IMR_NO + "service=WMS&version=1.1.1&request=GetLegendGraphic&layer="+layername+
    	"&width=22&height=24&format=image/png&SLD="+BASE_URL + "getsld?file=" + message.filename;
    jQuery("#legend").append('<p id='+displayName+'>'+displayName+'<br/><img src='+src+' /></p>');
}

function downloadMap(token, header) {
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
        //headers: {"Authorization": token},
        success: function(message){
        	$("div#divLoading").removeClass('show');
        	var blob = new Blob([message], {type: "text/plain;charset=utf-8"});
        	saveAs(blob, comboboxGrid+"_"+paramNames+"_"+periodNames+"_"+depthNames+".csv");
        },
        error: function(req, status, errThrown) {
            onErrorFunction(req, status, errThrown);
        }
    });
}

function createSLD(onSuccessFunction) {
    jQuery.ajax({
        url:"createBarMarsld",
        data:{
            grid : comboboxGrid,
            parameter: paramNames,
            time: periodNames,
            depth: depthNames,
            displaytype: displayType,
            aggregationfunc: aggregationfunc,
            logscale: logscale
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
var aggregationfunc = "";
var logscale = "";
function readBarMar() {
	paramNames = [];
	depthNames = ["F"]; //default
	periodNames = [];
	parameterIds = "";
	aggregationfunc = "";
	logscale = "";
	displayName = "";
	displayType = $('#visning option:selected').attr('id');
	
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

    aggregationfunc = "avg" //$('#aggregation option:selected').attr('id'); -- temporarily avg is only aggregation func
    logscale = $('#logarithm option:selected').attr('id');
    if (logscale == undefined ) logscale = "noLnScale";
	displayName = createDisplayName(paramNames.toString(), periodNames.toString(), depthNames.toString(), displayType);
}

function createPDF( mapp, queryObj ) {

    var onSuccessFunction = function (message) {
        var projection = mapp.getView().getProjection().getCode();
        projection = "EPSG:3575";
        var pdfUrl = "createpdfreport?bbox="+this.map.getView().calculateExtent(this.map.getSize())+
            "&sld="+BASE_URL + "getsld?file=" + message.filename +
            "&srs="+projection+
            "&layer="+layername+
            "&width="+mapp.getSize()[0]+ //width
            "&height="+mapp.getSize()[1]+ //height
            "&displayLayerName="+displayName+
            "&metadataRef="+queryObj.metadataRef+
            "&viewparams=agridname:'"+comboboxGrid+
            	"';parameter_ids:'"+queryObj.parameterIds+
            	"';depthlayername:'"+queryObj.depthNames+
            	"';periodname:'"+queryObj.periodNames+
            	"';aggregationfunc:'"+queryObj.aggregationfunc+"'";

        jQuery.ajax({
            url: pdfUrl,
            method: "post",
            success: function(data){
                var mapImageLink = jQuery("<a id='downloadPrintMap' href='getPDF?printFilename="+data.filename+"' hidden download='" + data.filename + "'></a>");
                jQuery('body').append(mapImageLink);
                var formDocument = document.getElementById("downloadPrintMap");
                formDocument.click();
                if (typeof formDocument.remove === 'function') {
                	formDocument.remove();
                } else {
                	$(formDocument).remove();
                }

                $("div#divLoading").removeClass('show');
            },
            error: function(req, status, errThrown) {
            	onErrorFunction(req, status, errThrown);
            }
        });    
    }
    
    parameterIds = queryObj.parameterIds;
    paramNames = queryObj.paramNames;
    depthNames = queryObj.depthNames;
    periodNames = queryObj.periodNames;
    aggregationfunc = queryObj.aggregationfunc;
    logscale = queryObj.logscale;
    displayType = queryObj.displayType; 
    
    createSLD(onSuccessFunction);
}

function createDisplayName(param, period, depth, displayType) {
	
    var depths = depthNames.toString().replace(/,|\:|\./g,'_');
    var periods = periodNames.toString().replace(/,/g,'_');
    var params = param.toString().replace(/,/g,'_');
    var aggregationOption = $('#aggregation option:selected').attr('id');
    
	var adisplayName = params;
	adisplayName += "_" + depths;
	adisplayName += "_" + periods;
    adisplayName += "_" + displayType;
    //adisplayName +=  "_" + aggregationOption;
    adisplayName +=  "_" + logscale;
    return adisplayName;
}


