	$("li").on('click', function(){
		var base 		= $(this).closest(".bootstrap-select");
		var select 	= base.find("select");

		if(base.length && select.prop("multiple")){ // check if this .dropdown-header is part of bootstrap-select AND multiple or not

			var group 	= $(this).data("optgroup")-1;
			var group2 	= group+1;
			var select 	= $(this).closest(".bootstrap-select").find("select");

			if($(this).hasClass("dropdown-header")){

				if(base.find("li[data-optgroup=" + group2 + "]:not(.selected)").length === 1)
					$(this).closest(".bootstrap-select").find("select > optgroup:eq(" + group + ") > option").prop("selected",false);
				else
					$(this).closest(".bootstrap-select").find("select > optgroup:eq(" + group + ") > option").prop("selected",true);

				select.selectpicker('render');
				var rs 	= select.mySelect();
				$(".result").text(rs);

			}else{
				setTimeout(function() { // set timeout because waiting for some bootstrap-select event done.
					var rs 	= select.mySelect();
					$(".result").text(rs);
				}, 100);
			}
		}else{
			$(".result").text("not a bootstrap-select");
		}
	});

	/* function onchange */
	$.fn.mySelect = function(){ 
		var arr = new Array();
		$(this).find("option").each(function()
		{
			if($(this).is(":selected"))
			{             
				id = $(this).attr("id");
				class_dept = $(this).attr("class");
				if(id == class_dept){
					//do something
				}

				if(arr.indexOf(id) == -1)
				{
					arr.push(id); 
				}
			}
			else
			{
				id = $(this).attr("id");
				arr = jQuery.grep(arr, function(value) {
					return value != id;
				});             
			}
		});

		return "Last selected : " + arr[arr.length-1] +" | array_item:"+ arr;

	}