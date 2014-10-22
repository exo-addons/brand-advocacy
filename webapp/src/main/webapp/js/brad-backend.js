/**
 * Created by exoplatform on 08/10/14.
 */
$(function() {
  $(document).on('click.juzBrad.bk.chkBox','div .spaceIphoneChkBox',function(){
    var input = $(this).find("input:checkbox");
    var val = input.attr("value") == "true" ? "false" : "true";
    input.attr("value", val);
  });
  $("div.spaceIphoneChkBox").children('input:checkbox').each(function () {
    $(this).iphoneStyle({
      checkedLabel:"Yes",
      uncheckedLabel:"No"
    });
    $(this).change(function(){
      $(this).closest("div.spaceIphoneChkBox").trigger("click");
    });
  });
  $(document).on('click.juzbrad.bk.program-tab','.program-tab',function(){
    var jProgramTab = $(this);
    jProgramTab.jzAjax("ManagerController.listProgramManagers()", {
      success: function (data) {
        $("#brad-bk-program-manager-container").html(data);
      }
    });
  });

  function bradBackend(){


  };
  bradBackend.prototype.update = function(){

  }

});