/**
 * Created by exoplatform on 08/10/14.
 */
$(function() {
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
  bradBackend.prototype.loadProgramManagers = function(){

  }

});