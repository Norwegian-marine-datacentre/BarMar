var felayer;

//PROD
var MAPS_IMR_NO = "http://maps.imr.no/geoserver/wms?";

//TEST: on local machine - use your ip address instead of localhost 
//var MAPS_IMR_NO = "http://geb-test.imr.no/geoserver/wms?";

var LAYER_POINTVALUE = "postgis:pointvalue";
var LAYER_AREAVALUE = "postgis:areavalue";
//var LAYER_POINTVALUE = "test:pointvalue";
//var LAYER_AREAVALUE = "test:areavalue";

var PUNKTVISNING = "punktvisning";
var AREALVISNING = "arealvisning";

var NORMAR_GRID = 11;

var BASE_URL = location.href.substring(0,location.href.lastIndexOf('/')) + "../";

var comboboxGrid = "";//jQuery("#grid :selected").val();
var comboboxParameter = "";//jQuery("#parameter :selected").val();
var comboboxPeriod = "";//jQuery("#period :selected").val(),
var comboboxDepth = "";//jQuery("#depthlayer :selected").val(),

function drawmap(mapp){
	jQuery.support.cors = true;

    // create sld 
	var layername = viewChoosen();
	var displayType = "";
	if (layername == LAYER_POINTVALUE) displayType = PUNKTVISNING;
	else displayType = AREALVISNING;
    jQuery.ajax({
        url:"../createsld",
        data:{
            grid : comboboxGrid,
            parameter: comboboxParameter,
            time: comboboxPeriod,
            depth: comboboxDepth,
            displaytype: displayType
        },
        method: "post",
        success: function(message){
            // add layer to map
            if(felayer != null){
                mapp.map.removeLayer(felayer);
            }
            
            addLayerToMap(layername, message, mapp);
        },
        error: function(req, status, errThrown) {
    		alert("ie error req:"+req+" status:"+status+" errThrown:"+errThrown);
    	}
    })
}

function viewChoosen() {
    var punktVisning = jQuery("#punkt").is(':checked');
    var layername = "";
    if(punktVisning){
        layername = LAYER_POINTVALUE;
    }else{
        layername = LAYER_AREAVALUE;
    }  
    return layername;
}

function addLayerToMap(layername, message, mapp) {
    src = MAPS_IMR_NO + "service=WMS&version=1.1.1&request=GetLegendGraphic&layer="+
	layername+"&width=22&height=24&format=image/png&SLD="+BASE_URL + "getsld?file=" + message;
    
    /*felayer = new OpenLayers.Layer.WMS.Post(
        "BarMarGrid",
        MAPS_IMR_NO,
        {
            layers: layername,
            transparent: true,
            sld: BASE_URL + "getsld?file=" + message
        },
        {
            isBaseLayer: false
        }
        );
    */
    felayer = new ol.layer.Image({
        source: new ol.source.ImageWMS({
            url: MAPS_IMR_NO,
            ratio: 1,
            params: {
                'LAYERS': layername,
                'TRANSPARENT': 'true',
                sld: BASE_URL + "getsld?file=" + message
            }
        })
    });
    mapp.addLayer(felayer);
    
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

function createPDF(){
	var showLayers = showVisibleLayers();
	var layername = viewChoosen();
	var displayType = "";
	if (layername == LAYER_POINTVALUE) displayType = PUNKTVISNING;
	else displayType = AREALVISNING;
    jQuery.ajax({
        url: "spring/createsld",
        data:{
            grid : jQuery("#grid :selected").val(),
            parameter: jQuery("#parameter :selected").val(),
            time: jQuery("#period :selected").val(),
            depth: jQuery("#depthlayer :selected").val(),
            displaytype: displayType
        },
        method: "post",
        success: function(message){
        	var mapp = Ext.ComponentMgr.all.find(function(c) {
        		return c instanceof GeoExt.MapPanel;
        	});

            var pdfUrl = "spring/createpdfreport?bbox="+mapp.map.getExtent().toBBOX()+
            	"&sld="+BASE_URL + "getsld?file=" + message +
            	"&srs="+mapp.map.getProjection()+
            	"&layer="+layername+
            	"&layerson="+showLayers+
            	"&width="+mapp.map.getSize().w+
            	"&height="+mapp.map.getSize().h;        
            
            document.getElementById("hidden_pdf").action = pdfUrl;
            jQuery("#hidden_pdf").submit();
        }
    })
}

function showVisibleLayers() {
	var mapp = Ext.ComponentMgr.all.find(function(c) {
		return c instanceof GeoExt.MapPanel;
	});
	var tmpLayers = mapp.map.layers;
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

function rememberState(showSpan) {
	if ( isAdvanced ) showHiddenSelect();
	hideShow(showSpan);
}
/* used in parameter.js to hide/show help feature*/
var newHelp='';
function hideShow(showSpan) {
	newHelp = showSpan;
	if ( newHelp == '') {
		jQuery('.hiddenFirstHelptext').show('fast');
	} else {
		jQuery('.hiddenFirstHelptext').hide('fast');
		jQuery('.hiddenGridHelp').hide('fast');
		jQuery('.hiddenSpeciesHelp').hide('fast');
		jQuery('.hiddenSubgroupHelp').hide('fast');
		jQuery('.hiddenDepthHelp').hide('fast');
		jQuery('.hiddenPeriodHelp').hide('fast');
		jQuery('.'+showSpan).show('fast');
	}
}

var isAdvanced=false;
function showHiddenSelect() {
	jQuery("#advanced").hide('fast');
	jQuery("#simple").show('fast');
	jQuery("#gridColumn").show('fast');
	jQuery("#depthLayerColumn").show('fast');
	jQuery("#periodColumn").show('fast');
	isAdvanced=true;
}

function hideSelect() {
	jQuery("#advanced").show("fast");
	jQuery("#simple").hide('fast');
	jQuery("#gridColumn").hide('fast');
	jQuery("#depthLayerColumn").hide('fast');
	jQuery("#periodColumn").hide('fast');
	isAdvanced=false;
}

function notSelectedDataset(){
	return jQuery('#dataset').val()!="Select value" && jQuery('#dataset').val()!="Velg verdi";
} 

function notSelectedParameter(){
	return jQuery('#parameter').val()!="Select value" && jQuery('#parameter').val()!="Velg verdi";
}