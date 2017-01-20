var SELECT_VALUE = "Select Value";

var dropDownAdd = function( buttonId, optionTxt, index, selectedValue ) {
    if ( index === 0 ) {
        $( buttonId ).html( addLI(optionTxt) );
        $(buttonId+'Btn').attr('disabled', false);
    } else {
        $( buttonId ).append( addLI(optionTxt) );
    }
    if ( optionTxt === selectedValue) {
        setSelectedValue( buttonId, optionTxt );
    } else if ( selectedValue === undefined ) {
        setSelectedValue( buttonId, SELECT_VALUE );
    }
};

function getParametersAsJson(grid, species, speciesSubGroup, depth, period, displaytype) {
    return function() {
        var xmlhttp = new XMLHttpRequest();
        var url = "barmar.json?grid="+grid+"&species="+species+"&subSpecies="+speciesSubGroup;
        var barMarParameters = null;
        xmlhttp.onreadystatechange = function() {
            if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
                barMarParameters = JSON.parse(xmlhttp.responseText);
                
                var emptyBtnList = function( buttonId, theBtnListElement ) {
                    if ( theBtnListElement == null || theBtnListElement.length == 0 ) {
                        setSelectedValue( buttonId, SELECT_VALUE );
                        $(buttonId+'Btn').attr('disabled', true);
                        $(buttonId).html( '' );
                    }
                }
                
                var gridList = barMarParameters['grid'];
                for (var i=0; i< gridList.length; i++) {
                    dropDownAdd("#grid", gridList[i], i, grid);
                }
    
                var speciesList = barMarParameters['species'];
                for (var i=0; i< speciesList.length; i++) {
                    dropDownAdd( "#species", speciesList[i], i, species);
                }
                
                var speciesSubgroupList = barMarParameters['speciesSubgroup']
                for (var i=0; speciesSubgroupList != null && i < speciesSubgroupList.length; i++) {
                    dropDownAdd("#speciesSubGroup", speciesSubgroupList[i], i, speciesSubGroup);
                }
                emptyBtnList( "#speciesSubGroup", speciesSubgroupList );
                
                var depthList = barMarParameters['depth'];
                for (var i=0; depthList != null && i< depthList.length; i++) {
                    dropDownAdd("#depth", depthList[i], i, depth);
                }
                emptyBtnList( "#depth", depthList );
                
                var periodList = barMarParameters['periods'];
                for (var i=0; periodList != null && i< periodList.length; i++) {
                    dropDownAdd("#period", periodList[i], i, period);
                }
                emptyBtnList( "#period", periodList );
                
                var metadataAdd = function( txt ) {
                    $("#metadata").html( '<p>' + txt + '</p>');
                };
                metadataAdd(barMarParameters['metadata']);
                
                $(".dropdown-menu li a").click(function(event) {
                    console.log(".dropdown-menu li a:" + $(this).parents(".dropdown").find('.btn').val() + " value:"+$(this).data('value') );
                    var oldChoice = $(this).parents(".dropdown").find('.btn').val();
                    $(this).parents(".dropdown").find('.btn').html($(this).text() + ' <span class="caret"></span>');
                    $(this).parents(".dropdown").find('.btn').val($(this).data('value'));
                    if ( oldChoice != $(this).data('value') ) {
                        console.log( "oldChoice:" + oldChoice + " new:" + $(this).data('value') );
                        var parameterName = $(this).parent().parent()[0].id;
                        console.log("name:"+parameterName);
                        var parameterValue = $(this).data('value')
                        
                        var grid = $("#grid").parents(".dropdown").find('.btn').val();
                        var species = $("#species").parents(".dropdown").find('.btn').val();
                        //var speciesSubGroup = $("#speciesSubGroup").parents(".dropdown").find('.btn').val();
                        //var time = $("#period").parents(".dropdown").find('.btn').val();
                        //var depth = $("#depth").parents(".dropdown").find('.btn').val();
                        
                        if ( parameterName === 'grid')
                            getParametersAsJson(parameterValue);
                        else if (parameterName === 'species')
                            getParametersAsJson( grid, parameterValue );
                        else if (parameterName === 'speciesSubGroup')
                            getParametersAsJson( grid, species, parameterValue );
                    }
                });
            }
        };
        xmlhttp.open("GET", url, true);
        xmlhttp.send();
    }();
};

var setSelectedValue = function( buttonId, optionTxt) {
    $(buttonId).parents(".dropdown").find('.btn').html( optionTxt + ' <span class="caret"></span>' );
    $(buttonId).parents(".dropdown").find('.btn').val( optionTxt );
}

var addLI = function( optionTxt ) {
    return '<li><a href="#" data-value="' + optionTxt + '">' + optionTxt + '</a></li>';
}