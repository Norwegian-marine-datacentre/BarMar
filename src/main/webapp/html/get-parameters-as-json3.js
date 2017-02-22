var SELECT_VALUE = "Select Value";

var addLI = function( optionTxt ) {
	return '<option class="A" id="' + optionTxt + '">' + optionTxt + '</option>';
}

var dropDownAdd = function( buttonId, optionTxt, index ) {
    if ( index === 0 ) {
        $( buttonId ).append( addLI(optionTxt) );
        $(buttonId+'Btn').attr('disabled', false);
    } else {
        $( buttonId ).append( addLI(optionTxt) );
    }
};

var emptyBtnList = function( buttonId ) {
	$(buttonId+" .selectpicker option").remove();
}

var setSelectedValue = function( optionTxt, selectpickerGroup) {

	$( "#"+optionTxt ).attr('selected','selected');
	$( "#"+selectpickerGroup ).selectpicker("refresh");
}

var barMarParameters = null;
function getParametersAsJson(grid) {
    var xmlhttp = new XMLHttpRequest();
    var url = "barmarDb.json?grid="+grid;

    xmlhttp.onload = function() {
    	barMarParameters = JSON.parse(xmlhttp.responseText);
    	updateSpeciesList();
		setSelectedValue( "Cod", "speciesselect" );
		
        updateSpeciesSubgroup( "Cod" );
		setSelectedValue( "Cod_survey_trawl_ecosystem_0-4cm", "speciesSubGroupselect" );
		updateDepthAndTime( "Cod", "length", "Cod_survey_trawl_ecosystem_0-4cm" );
		
		$("#speciesselect").on('changed.bs.select', function(event) {
			emptyBtnList( "#speciesSubGroupselect" );
			var speciesName = $('#speciesBtn .selectpicker option:selected').val();			
			updateSpeciesSubgroup( speciesName );
		});
		
		$("#speciesSubGroupselect").on('changed.bs.select', function(event) {
			emptyBtnList( "#depthBtn" );
			emptyBtnList( "#periodBtn" );

			var speciesSubGroupObj = $('#speciesSubGroupBtn .selectpicker option:selected');
			var speciesSubGroup = $(speciesSubGroupObj).val();
			var speciesName = $('#speciesBtn .selectpicker option:selected').val();

			//TODO: create Aggregated union for depth and period
			var group = $( speciesSubGroupObj ).parent()[0].id;
			console.log("group:"+group);
			if ( group === "speciesSubGroupLength" ) updateDepthAndTime( speciesName, "length", speciesSubGroup ); 
			else if ( group === "speciesSubGroupAge" ) updateDepthAndTime( speciesName, "age", speciesSubGroup ); 
			else if ( group === "speciesSubGroupOther" ) updateDepthAndTime( speciesName, "other", speciesSubGroup ); 
		});		
    };
    xmlhttp.open("GET", url, true);
    xmlhttp.send();
        
    return barMarParameters;    
};

function updateSpeciesList() {

    var speciesList = barMarParameters['species'];
    for (var i=0; i< speciesList.length; i++) {
        dropDownAdd( "#species", speciesList[i], i);
    }
	$( '#speciesselect' ).selectpicker('refresh');
}

function updateSpeciesSubgroup( speciesName ) {
	
    var subspecies = barMarParameters[ speciesName ];
    
	emptyBtnList( "#speciesSubGroupBtn" );
	emptyBtnList( "#depthBtn" );
	emptyBtnList( "#periodBtn" );	
	
    var speciesSubgroupLength = Object.keys( subspecies['length'] );
    var speciesSubgroupAge = Object.keys( subspecies['age'] );
    var speciesSubgroupOther = Object.keys( subspecies['other'] );
    for (var i=0; speciesSubgroupLength != null && i < speciesSubgroupLength.length; i++) {
        dropDownAdd("#speciesSubGroupLength", speciesSubgroupLength[i], i);
    }
    for (var i=0; speciesSubgroupAge != null && i < speciesSubgroupAge.length; i++) {
		dropDownAdd("#speciesSubGroupAge", speciesSubgroupAge[i], i);
    }
    for (var i=0; speciesSubgroupOther != null && i < speciesSubgroupOther.length; i++) {
		dropDownAdd("#speciesSubGroupOther", speciesSubgroupOther[i], i);
    }
	$( '#speciesSubGroupselect' ).selectpicker('refresh');
}

function updateDepthAndTime( speciesName, lengthAgeOther, speciesSubGroup ) {
	emptyBtnList( "#depthBtn" );
	emptyBtnList( "#periodBtn" );
	
    var subgroup = barMarParameters[ speciesName ];
	var metadataRef = subgroup[ lengthAgeOther ][speciesSubGroup].metadataRef;
	var depthList = subgroup[ lengthAgeOther ][speciesSubGroup].depths;
	var periodList = subgroup[ lengthAgeOther ][speciesSubGroup].periods;
	
    for (var i=0; depthList != null && i< depthList.length; i++) {
        dropDownAdd( "#depth", depthList[i], i );
    }

    for (var i=0; periodList != null && i< periodList.length; i++) {
        dropDownAdd("#period", periodList[i], i);
    }
	setSelectedValue( "Aggregated all data", "depthselect" );
	setSelectedValue( "Aggregated all", "periodselect" );
	$( '#depthselect' ).selectpicker('refresh');
	$( '#periodselect' ).selectpicker('refresh');

	$("#metadata").html( '<p>' + barMarParameters[ metadataRef ].metadata + '</p>');
}