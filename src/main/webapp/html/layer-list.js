/**
 * Initialize the stack control with the layers in the map.
 */
function initializeStack() {
    var layers = map.getLayers();
    var length = layers.getLength();
    var layerName;
    $('ul.layerstack').html('');
    for (var i = 0; i < length; i++) {
        layerName = layers.item(i).get('name');
        if ( i > 0) { // layer at 0 is baselayer
            $('ul.layerstack').append(
                '<li data-layerid="' + layerName + '"><input type="checkbox" checked="checked"> ' + layerName
                        + '<a href="#" id="'+layerName+'"><img src="imr/images/pdf.jpg" width="16" height="20"/></a></li>');
        } else {
            $('ul.layerstack').append(
                    '<li data-layerid="' + layerName + '"><input type="checkbox" checked="checked"> ' + layerName + '</li>');
        }
    }

    // Change style when select a layer
    $('ul.layerstack li').on('click', function() {
        $('ul.layerstack li').removeClass('selected');
        $(this).addClass('selected');
    });
}

var pdfHash = {};
/** Display icon with pdf generation */
function addPdfGenerationToStack(displayName, parameterIds, paramNames, periodNames, depthNames, displayType, aggregationfunc) {
	//'paramNames' : paramNames, .. , 'displayType':displayType
    pdfHash[displayName] = {'parameterIds': parameterIds, 'periodNames': periodNames, 'depthNames':depthNames, 'aggregationfunc':aggregationfunc};
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
        console.log("layerid:"+layerid);
        var layer = findByName(layerid);
        layer.setVisible( $(this).is(':checked') );
        
        //gray out/in legend of checked layer
        if ( $(this).is(':checked') === false )
            jQuery("#legend p[id='"+layerid+"'").fadeTo(500, 0.2);
        else
            jQuery("#legend p[id='"+layerid+"'").fadeTo(500, 1);
    });
    
    $('ul.layerstack').on('click', 'a', function() {
        var layer = $(this).attr('id');
        var pdfGen = pdfHash[layer];

        console.log("this.map:"+this.map);
        barmarCreatePDF(pdfGen)
    });
});