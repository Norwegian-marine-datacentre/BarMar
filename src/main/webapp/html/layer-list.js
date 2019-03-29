function updateLayerStack( alayerName, map ) {
    $('ul.layerstack').append(
            '<li data-layerid="' + alayerName + '"><img id="close_red" src="imr/images/close_red.png"><input type="checkbox" checked="checked"> ' + alayerName
                    + '<a class="pdf" href="#" id="'+alayerName+'"><img src="imr/images/pdf.jpg" width="16" height="20"/></a>'
                    + '<a class="jpg" href="#" id="'+alayerName+'"><img src="imr/images/jpg.png" width="16" height="20"/></a></li>');
    // Change style when select a layer
    $('ul.layerstack li').on('click', function() {
        $('ul.layerstack li').removeClass('selected');
        $(this).addClass('selected');
    });
    removeLayerFromStack(alayerName, map);
}

function removeLayerFromStack( alayername, map ) {
	$('[data-layerid="'+alayername+'"] #close_red').on('click', function() {
		$(this).closest('li').remove();
		$('#legend > #'+alayername).remove();
		
		for (var i=0; i < map.getLayers().getLength(); i++){
			var aTileLayer = map.getLayers().item(i);
			var name = aTileLayer.get('name');
			if ( name == alayername ) {
				map.getLayers().removeAt( i );
			}
		}
	});
}

var pdfHash = {};
/** Display icon with pdf generation */
function addPdfGenerationToStack(displayName, parameterIds, paramNames, periodNames, depthNames, aggregationfunc, logscale, displayType, metadataRef) {
    pdfHash[displayName] = 
    	{	'parameterIds': parameterIds, 
    		'paramNames':paramNames,
    		'periodNames': periodNames, 
    		'depthNames':depthNames, 
    		'aggregationfunc':aggregationfunc, 
    		"logscale":logscale, 
    		"displayType":displayType,
    		"metadataRef":metadataRef,
		};
}

/**
 * Returns the index of the layer within the collection.
 * @param {type} layers
 * @param {type} layer
 * @returns {Number}
 */
function indexOf(layers, layer) {
    var length = layers.getLength();
    for (var i = 0; i < length; i++) {
        if (layer === layers.item(i)) {
            return i;
        }
    }
    return -1;
}
/**
 * Finds a layers given a 'name' attribute.
 * @param {type} name
 * @returns {unresolved}
 */
function findByName(name) {
    var layers = map.getLayers();
    var length = layers.getLength();
    if ( layers != undefined ) {
        for (var i = 0; i < length; i++) { // dont find base layer (=1)
            if (name === layers.item(i).get('name')) {
                return layers.item(i);
            }
        }
    }
    return null;
}

/**
 * Raise a layer one place.
 * @param {type} layer
 * @returns {undefined}
 */
function raiseLayer(layer) {
    if ( layer === undefined ) return;
    var layers = map.getLayers();
    var index = indexOf(layers, layer);
    if (index > 0) { // > 1 instead of > 0 so not to raise layer above base layer 
        var next = layers.item(index - 1);
        layers.removeAt(index);
        layers.insertAt(index-1, layer);

        // Moves li element up
        var elem = $('ul.layerstack li[data-layerid="' + layer.get('name') + '"]');
        elem.prev().before(elem);
    }
}

/**
 * Lowers a layer once place.
 * @param {type} layer
 * @returns {undefined}
 */
function lowerLayer(layer) {
    if ( layer === undefined ) return;
    var layers = map.getLayers();
    var index = indexOf(layers, layer);
    if (index < layers.getLength() - 1) {
        var prev = layers.item(index + 1);
        layers.removeAt(index);
        layers.insertAt(index+1, layer);
        
        // Moves li element down
        var elem = $('ul.layerstack li[data-layerid="' + layer.get('name')+ '"]');
        elem.next().after(elem);
    }
}

$(document).ready(function() {

    $('#raise').on('click', function() {
        var layerid = $('ul.layerstack li.selected').data('layerid');
        if (layerid) {
            var layer = findByName(layerid);
            raiseLayer(layer);
        }
    });

    $('#lower').on('click', function() {
        var layerid = $('ul.layerstack li.selected').data('layerid');
        if (layerid) {
            var layer = findByName(layerid);
            lowerLayer(layer);
        }
    });
    $('ul.layerstack').on('click', 'input[type="checkbox"]', function() {
        var layerid = $(this).parent().data('layerid');
        var layer = findByName(layerid);
        layer.setVisible( $(this).is(':checked') );
        
        //gray out/in legend of checked layer
        if ( $(this).is(':checked') === false )
            jQuery("#legend p[id='"+layerid+"'").fadeTo(500, 0.2);
        else
            jQuery("#legend p[id='"+layerid+"'").fadeTo(500, 1);
    });
    
    $('ul.layerstack').on('click', 'a', function() {
    	if ( $(this).attr('class') == 'pdf' ) {
        	var layer = $(this).attr('id');
        	var pdfGen = pdfHash[layer];

        	barmarCreatePDF(pdfGen)
    	} else {
        	var layer = $(this).attr('id');
        	var pdfGen = pdfHash[layer];

        	barmarCreateJPG(pdfGen)    		
    	}
    });
});