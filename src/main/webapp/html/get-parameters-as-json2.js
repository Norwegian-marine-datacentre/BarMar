var SELECT_VALUE = "Select Value";

var addLI = function( optionTxt ) {
	return '<option class="A" id="' + optionTxt + '">' + optionTxt + '</option>';
}

var dropDownAdd = function( buttonId, optionTxt, index, selectedValue ) {
    if ( index === 0 ) {
        $( buttonId ).append( addLI(optionTxt) );
        $(buttonId+'Btn').attr('disabled', false);
    } else {
        $( buttonId ).append( addLI(optionTxt) );
    }
    if ( optionTxt === selectedValue) {
        setSelectedValue( optionTxt );
    } 
	/*
	else if ( selectedValue === undefined ) {
        setSelectedValue( SELECT_VALUE );
    }*/
    $( buttonId+'select').selectpicker('refresh');
	$( '#speciesSubGroupselect').selectpicker('refresh');
};

function getParametersAsJson(grid, species, speciesSubGroup, depth, period, displaytype) {
    return function() {
        var xmlhttp = new XMLHttpRequest();
        var url = "barmar.json?grid="+grid+"&species="+species+"&subSpecies[]="+speciesSubGroup;
        var barMarParameters = null;
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				
                barMarParameters = JSON.parse(xmlhttp.responseText);
                var emptyBtnList = function( buttonId ) {
					$(buttonId+" .selectpicker option").remove();
                }
                
                var gridList = barMarParameters['grid'];
                for (var i=0; i< gridList.length; i++) {
                    dropDownAdd("#grid", gridList[i], i, grid);
                }
    
                var speciesList = barMarParameters['species'];
                for (var i=0; i< speciesList.length; i++) {
                    dropDownAdd( "#species", speciesList[i], i, species);
                }
                
				emptyBtnList( "#speciesSubGroupBtn" );
                var speciesSubgroupLength = barMarParameters['length'];
                var speciesSubgroupAge = barMarParameters['age'];
                var speciesSubgroupOther = barMarParameters['other'];
                for (var i=0; speciesSubgroupLength != null && i < speciesSubgroupLength.length; i++) {
                    dropDownAdd("#speciesSubGroupLength", speciesSubgroupLength[i], i, speciesSubGroup);
                }
                for (var i=0; speciesSubgroupAge != null && i < speciesSubgroupAge.length; i++) {
					dropDownAdd("#speciesSubGroupAge", speciesSubgroupAge[i], i, speciesSubGroup);
                }
                for (var i=0; speciesSubgroupOther != null && i < speciesSubgroupOther.length; i++) {
					dropDownAdd("#speciesSubGroupOther", speciesSubgroupOther[i], i, speciesSubGroup);
                }
                
				emptyBtnList( "#depthBtn", depthList );
                var depthList = barMarParameters['depth'];
                for (var i=0; depthList != null && i< depthList.length; i++) {
                    dropDownAdd("#depth", depthList[i], i, depth);
                }
                $('#depthselect').removeAttr('disabled',false);
				
                emptyBtnList( "#periodBtn", periodList );
                var periodList = barMarParameters['periods'];
                for (var i=0; periodList != null && i< periodList.length; i++) {
                    dropDownAdd("#period", periodList[i], i, period);
                }
				//var tmpp = $('#periodselect');
				$('#periodselect').removeAttr('disabled', false);
                
                var metadataAdd = function( txt ) {
                    $("#metadata").html( '<p>' + txt + '</p>');
                };
                metadataAdd(barMarParameters['metadata']);
                
                $("#speciesselect").on('changed.bs.select', function(event) {
					var tmp = $("[data-id='speciesselect']").prop('title');
					val = $('#speciesBtn .selectpicker option:selected').val();
					getParametersAsJson( 'BarMar', val );
					text = $('#speciesBtn .selectpicker option:selected').text();
				});
				$("#speciesSubGroupselect").on('hide.bs.select', function(event) {
					var dataLoadingText = "Loading..."

					$('#depthselect').append( '<option selected data-hidden="true" id="loading">' + dataLoadingText + '</option>' );
					$('#depthselect').val($('#loading').val());
					emptyBtnList( "#depthBtn", depthList );
					emptyBtnList( "#periodBtn", periodList );
					$('#depthselect').selectpicker('refresh');
					//$('#depthselect').attr('disabled',true);
					//$('#periodselect').attr('disabled', true);

					var species = $('#speciesBtn .selectpicker option:selected').val();
					var speciesSubGroup = $('#speciesSubGroupBtn .selectpicker option:selected').val();
					getParametersAsJson( 'BarMar', species, speciesSubGroup );
					
					//create Aggregated union for depth and period
					text = $('#speciesSubGroupBtn .selectpicker option:selected').text();
					console.log("speciesSubGroupselect hide end:"+text);
				});
				
				$("#depthselect").on('shown.bs.select', function(event) { //hide.bs.select is fired after next bootstrap select dialog is opened !!? Close again				
					console.log("depthselect shown.bs.select start");
					var isDepthBtnDisabled = $('#depthselect').prop('disabled');
					if ( isDepthBtnDisabled === true ) {
						$('#depthselect').triggerNative('hide.bs.select'); 
						$('#depthBtn .btn-group').removeClass('open'); //close it again
					}
					console.log("depthselect shown.bs.select end");
				});
					/*
                        if ( parameterName === 'grid')
                            getParametersAsJson(parameterValue);
                        else if (parameterName === 'species')
                            getParametersAsJson( grid, parameterValue );
                        else if (parameterName === 'speciesSubGroup')
                            getParametersAsJson( grid, species, parameterValue );
						*/

            }
        };
        xmlhttp.open("GET", url, true);
        xmlhttp.send();
    }();
};

var setSelectedValue = function( optionTxt) {
	console.log("#optionTxt:"+$( "#"+optionTxt ).attr('selected'));
	$( "#"+optionTxt ).attr('selected','selected');
    //$(buttonId).parents(".dropdown").find('.btn').html( optionTxt + ' <span class="caret"></span>' );
	//$(buttonId).parents(".dropdown").find('.btn').val( optionTxt );
}

