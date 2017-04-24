function readParametersClosure() {

	var addLI = function( optionTxt, optionTxtId ) { //id is used by setSelectedValue, value is used by readBarMar() (unable to read id)
		return '<option value="' + optionTxt + '" id="' + optionTxtId + '">' + optionTxt + '</option>';
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
			if ( location.href.indexOf("barmar") > -1) { 
				setSelectedValue( "Cod", "speciesselect" );
				updateSpeciesSubgroup( "Cod" );
				setSelectedValue( "214", "speciesSubGroupselect" );
				var speciesSubGroupList = $('#speciesSubGroupBtn .selectpicker option:selected');
				updateDepthAndTime( "Cod", speciesSubGroupList );
			} else if (location.href.indexOf("normar") > -1) {
				setSelectedValue( "BlueWhiting", "speciesselect" );
				updateSpeciesSubgroup( "BlueWhiting" );
				setSelectedValue( "34", "speciesSubGroupselect" );
				var speciesSubGroupList = $('#speciesSubGroupBtn .selectpicker option:selected');
				updateDepthAndTime( "BlueWhiting", speciesSubGroupList );
			}
			
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
			$("#periodselect").on('changed.bs.select', function( event, clickedIndex, newValue, oldValue ) {
				//console.log("event:"+event+" clickedIndex:"+clickedIndex+" newValue:"+newValue+" oldValue:"+oldValue);
				removeDefaultAggregatedAll('#periodBtn', '#period', clickedIndex);
			} );
			$("#depthselect").on('changed.bs.select', function( event, clickedIndex ) {
				removeDefaultAggregatedAll('#depthBtn', '#depth', clickedIndex); 
			});
			function removeDefaultAggregatedAll( LIselector, OPTIONselector, clickedIndex ) {
				var aria = $( LIselector +' [data-original-index=0] a' );
				var displayText = $( aria ).find( ".text").html();
				if ( clickedIndex != 0 ) {
					$( LIselector +' [data-original-index=0]').removeClass( "selected" );
					$( aria ).attr('aria-selected', false);
					$(OPTIONselector + " [value='"+displayText+"']").removeAttr('selected');
				} else {
					var allSelected = $( LIselector +' .selected' );
					for ( i=0; i < allSelected.length; i++ ) {
						if ( $(allSelected[i]).attr('data-original-index') != "0" ) {
							var theItem = allSelected[i];
							$(allSelected[i]).removeClass( "selected" ); 
							$(allSelected[i]).find("a").attr('aria-selected', false );
						}
					}
					$( OPTIONselector + " option" ).filter(function() {
						if ( this.selected == true && $(this).html() != displayText ) {
							this.selected = false;
						}
					});
				}
				var titleArray = $(OPTIONselector + " option").map(function () {  
					if ( this.selected) {
						return $(this).html();
					}
				});
				var titleArray2 = titleArray.toArray();
				$( LIselector + ' button').attr( 'title', titleArray2.join(', ') );
			}
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
		if ( subspecies === undefined ) return;
		
		if ( speciesName === 'Salinity' || speciesName === 'Temperature' ) {
			$(depthBtn).show(1500)
		} else $(depthBtn).hide(1500);
		
		emptyBtnList( "#speciesSubGroupBtn" );
		emptyBtnList( "#depthBtn" );
		emptyBtnList( "#periodBtn" );
		if ( speciesName === 'Salinity' || speciesName === 'Temperature' ) {
			$( $("#depthBtn")[0] ).fadeTo('slow',.6);
		}
		$( $("#periodBtn")[0] ).fadeTo('slow',.6);
		
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
		if ( speciesName === 'Salinity' || speciesName === 'Temperature' ) {
			$( $("#depthBtn")[0] ).fadeTo('slow',1);
		}
		$( $("#periodBtn")[0] ).fadeTo('slow',1);
		
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
			
			if ( metaRef != null && $.inArray(metaRef, unionMetadataRef) == -1 )
				unionMetadataRef.push( metaRef );
			
			
			if ( i == 0 ) {// set all depth/periods - for first subspecies
				intersectDepthList = depthList;
				intersectPeriodList = periodList;
			} else { //allow only elements which are an intersection of periods when more than one subspecies
				intersectDepthList = intersectDepthList.filter(function (n) { //n => depthList.indexOf(n) != -1 
					return depthList.indexOf(n) != -1; 
				});
				intersectPeriodList = intersectPeriodList.filter(function (n) {
					return periodList.indexOf(n) != -1;
				});
			}
		}
		
		for (var i=0; i < intersectDepthList.length; i++) {
			var depthId = intersectDepthList[i]
			var depthDisplayName = displayDepth(depthId);
			dropDownAdd( "#depth", depthDisplayName, i, depthId );
		}
		$( '#depthselect' ).selectpicker('refresh');
		
		for (var i=0; i < intersectPeriodList.length; i++) {
			var periodId = intersectPeriodList[i]
			var periodDisplayName = displayPeriodClosure()(periodId);			
			dropDownAdd("#period", periodDisplayName, i, periodId );
		}
		$( '#periodselect' ).selectpicker('refresh');

		setSelectedValue( "F", "depthselect" );
		setSelectedValue( "F", "periodselect" );

		if ( metaRef != undefined )
			$("#metadata").html( '<p id="">' + barMarParameters[ metaRef ].metadata + '</p>');
		else $("#metadata").html( '');
	}
}