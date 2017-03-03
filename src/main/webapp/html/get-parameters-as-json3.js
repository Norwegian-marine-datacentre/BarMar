function readParametersClosure() {

	var addLI = function( optionTxt, optionTxtId ) { //id is used by setSelectedValue, value is used by readBarMar() (unable to read id)
		return '<option value="' + optionTxtId + '" id="' + optionTxtId + '">' + optionTxt + '</option>';
	}

	var dropDownAdd = function( buttonId, optionTxt, index, optionTxtId ) {
		$( buttonId ).append( addLI(optionTxt, optionTxtId) );
	};

	var emptyBtnList = function( buttonId ) {
		$(buttonId+" .selectpicker option").remove();
	}

	var setSelectedValue = function( optionTxt, selectpickerGroup) {

		$( "#"+selectpickerGroup + " #"+optionTxt ).attr('selected','selected');
		$( "#"+selectpickerGroup ).selectpicker("refresh");
	}

	var barMarParameters = null;
	return function getParametersAs(grid) {
		var xmlhttp = new XMLHttpRequest();
		var url = "barmarDb.json?grid="+grid;

		xmlhttp.onload = function() {
			barMarParameters = JSON.parse(xmlhttp.responseText);
			updateSpeciesList();
			setSelectedValue( "Cod", "speciesselect" );
			
			updateSpeciesSubgroup( "Cod" );
			//setSelectedValue( "Cod_survey_trawl_ecosystem_0-4cm", "speciesSubGroupselect" );
			setSelectedValue( "214", "speciesSubGroupselect" );
			var speciesSubGroupList = $('#speciesSubGroupBtn .selectpicker option:selected');
			updateDepthAndTime( "Cod", speciesSubGroupList );
			setSelectedValue("punktvisning");
			
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
			dropDownAdd( "#species", speciesList[i], i, speciesList[i]);
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
			var asdf = subspecies['length'][speciesSubgroupLength[i]];
			dropDownAdd("#speciesSubGroupLength", speciesSubgroupLength[i], i, subspecies['length'][speciesSubgroupLength[i]].id );
		}
		for (var i=0; speciesSubgroupAge != null && i < speciesSubgroupAge.length; i++) {
			dropDownAdd("#speciesSubGroupAge", speciesSubgroupAge[i], i, subspecies['age'][speciesSubgroupAge[i]].id );
		}
		for (var i=0; speciesSubgroupOther != null && i < speciesSubgroupOther.length; i++) {
			dropDownAdd("#speciesSubGroupOther", speciesSubgroupOther[i], i, subspecies['other'][speciesSubgroupOther[i]].id);
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
			var optionElement = $( speciesSubGroupList[i] )[0];
			var aSpeciesSubGroup = $( optionElement ).text();
			
			var optGroup = $(speciesSubGroupList[i]).parent()[0].id;
			if ( optGroup === "speciesSubGroupLength" ) lengthAgeOther = "length";
			else if ( optGroup === "speciesSubGroupAge" ) lengthAgeOther = "age"; 
			else if ( optGroup === "speciesSubGroupOther" ) lengthAgeOther = "other";
			
			var metaRef = subgroup[ lengthAgeOther ][aSpeciesSubGroup].metadataRef;
			var depthList = subgroup[ lengthAgeOther ][aSpeciesSubGroup].depths;
			var periodList = subgroup[ lengthAgeOther ][aSpeciesSubGroup].periods;
			
			console.log("period:"+periodList+" id:"+subgroup[ lengthAgeOther ][aSpeciesSubGroup].id);
			if ( metaRef != null && $.inArray(metaRef, unionMetadataRef) == -1 )
				unionMetadataRef.push( metaRef );
			
			
			if ( i == 0 ) {// set all depth/periods - for first subspecies
				intersectDepthList = depthList;
				intersectPeriodList = periodList;
			} else { //allow only elements which are an intersection of periods when more than one subspecies
				intersectDepthList = intersectDepthList.filter(n => depthList.indexOf(n) != -1);
				intersectPeriodList = intersectPeriodList.filter(n => periodList.indexOf(n) != -1);
			}
		}
		
		for (var i=0; i < intersectDepthList.length; i++) {
			var depthId = intersectDepthList[i]
			var depthDisplayName = displayPeriodClosure()(depthId);
			dropDownAdd( "#depth", depthDisplayName, i, depthId );
		}
		$( '#depthselect' ).selectpicker('refresh');
		
		for (var i=0; i < intersectPeriodList.length; i++) {
			var periodId = intersectPeriodList[i]
			var periodDisplayName = displayDepth(periodId);			
			dropDownAdd("#period", periodDisplayName, i, periodId );
		}
		$( '#periodselect' ).selectpicker('refresh');

		setSelectedValue( "F", "depthselect" );
		setSelectedValue( "F", "periodselect" );

		if ( metaRef != undefined )
			$("#metadata").html( '<p>' + barMarParameters[ metaRef ].metadata + '</p>');
		else $("#metadata").html( '');
	}
}