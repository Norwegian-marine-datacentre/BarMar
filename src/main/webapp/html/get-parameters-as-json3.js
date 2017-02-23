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
		var speciesSubGroupList = $('#speciesSubGroupBtn .selectpicker option:selected');
		updateDepthAndTime( "Cod", speciesSubGroupList );
		
		$("#speciesselect").on('changed.bs.select', function(event) {
			emptyBtnList( "#speciesSubGroupselect" );
			var speciesName = $('#speciesBtn .selectpicker option:selected').val();			
			updateSpeciesSubgroup( speciesName );
		});
		
		$("#speciesSubGroupselect").on('changed.bs.select', function(event) {
			emptyBtnList( "#depthBtn" );
			emptyBtnList( "#periodBtn" );

			var speciesSubGroupList = $('#speciesSubGroupBtn .selectpicker option:selected');

			var speciesName = $('#speciesBtn .selectpicker option:selected').val();

			//TODO: create Aggregated union for depth and period
			updateDepthAndTime( speciesName, speciesSubGroupList );
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

function updateDepthAndTime( speciesName, speciesSubGroupList ) {

	emptyBtnList( "#depthBtn" );
	emptyBtnList( "#periodBtn" );
	
	var subgroup = barMarParameters[ speciesName ];
	var lengthAgeOther = "";

	var unionMetadataRef = [];
	var intersectDepthList = [];
	var intersectPeriodList = [];
	for( var i=0; i < speciesSubGroupList.length; i++) {
		var aSpeciesSubGroup = $(speciesSubGroupList[i]).val();
		
		var optGroup = $(speciesSubGroupList[i]).parent()[0].id;
		if ( optGroup === "speciesSubGroupLength" ) lengthAgeOther = "length";
		else if ( optGroup === "speciesSubGroupAge" ) lengthAgeOther = "age"; 
		else if ( optGroup === "speciesSubGroupOther" ) lengthAgeOther = "other";
		
		var metaRef = subgroup[ lengthAgeOther ][aSpeciesSubGroup].metadataRef;
		var depthList = subgroup[ lengthAgeOther ][aSpeciesSubGroup].depths;
		var periodList = subgroup[ lengthAgeOther ][aSpeciesSubGroup].periods;
		
		console.log("depthList:"+depthList);
		if ( metaRef != null && $.inArray(metaRef, unionMetadataRef) == -1 )
			unionMetadataRef.push( metaRef );
		for ( var j=0; depthList != null && j < depthList.length; j++ ) {
			if ( $.inArray(depthList[i], intersectDepthList) == -1 ) 
				intersectDepthList.push( depthList[i] );
		}
		for ( var j=0; periodList != null && j < periodList.length; j++ ) {
			if ( $.inArray(periodList[i], intersectPeriodList) == -1 ) 
				intersectPeriodList.push( periodList[i] );
		}
	}
	
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

	$("#metadata").html( '<p>' + barMarParameters[ metaRef ].metadata + '</p>');
}