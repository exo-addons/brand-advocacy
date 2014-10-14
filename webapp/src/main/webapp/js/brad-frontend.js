/**
 * Created by exoplatform on 13/10/14.
 */
$(function() {
  $(document).on('click.juzbrad.ft.discovery.view','#brad-ft-discovery',function(){
    var jDiscovery = $(this);
    jDiscovery.jzAjax("JuZFrontEndApplication.loadDiscoveryView()", {
      success: function (data) {
        $("#brad-ft-container").html(data);
        bradFrontend.ftStepDOM = $("#brad-ft-step");
        bradFrontend.ftStepDOM.jzAjax("JuZFrontEndApplication.loadStartView()", {
          success: function (data) {
            bradFrontend.ftStepDOM.html(data);
          }
        });
      }
    });
  });
  $(document).on('click.juzbrad.ft.index.view','.btn-brad-close',function(){
    var jStart = $(this);
    jStart.jzAjax("JuZFrontEndApplication.loadIndexView()",{
      success: function(data){
        $("#brad-ft-container").html(data);
      }
    });
  });

  $(document).on('click.juzbrad.ft.start.view','.btn-start',function(){
    var jStart = $(this);
    jStart.jzAjax("JuZFrontEndApplication.loadProcessView()",{
      success: function(data){
        bradFrontend.ftStepDOM.html(data);
      }
    });
  });

  $(document).on('click.juzbrad.ft.done.view','.btn-brad-done',function(){
    var jStart = $(this);
    var missionId = $(".missionId").val();
    var propositionId = $(".propositionId").val();
    var status = $(".mpStatus").val();
    if(bradFrontend.checkFtForm(missionId,propositionId)){

      bradFrontend.addMissionParticipant(bradFrontend,missionId,propositionId,status);

    }
  });

  $(document).on('click.juzbrad.ft.terminate.view','.btn-brad-terminate',function(){
    var jStart = $(this);
    var fname = $("#brad-participant-fname").val();
    var lname = $("#brad-participant-lname").val();
    var address = $("#brad-participant-address").val();
    var city = $("#brad-participant-city").val();
    var phone = $("#brad-participant-phone").val();
    var country = $("#brad-participant-country").val();
    var size = $("#brad-participant-size").val();
    jStart.jzAjax("JuZFrontEndApplication.loadThankyouView()",{
      data:{fname:fname,lname:lname,address:address,city:city,phone:phone,country:country,size:size},
      success: function(data){
        if(typeof data == "string" && data != "nok")
          bradFrontend.ftStepDOM.html(data);
        else
          alert("something went wrong, please reload this page");
      }
    });
  });

  function bradFrontend(){
    this.ftStepDOM = null;
  };

  bradFrontend.prototype.checkFtForm = function(missionId, propositionId ){
    var res = true;
    if(null == missionId || 0 == missionId || typeof missionId == "undefined")
      res = false;
    else if(null == propositionId || 0 == propositionId || typeof propositionId == "undefined")
      res = false;

    if(!res)
      alert(" something went wrong, cannot identify current mission");
    return res;
  }

  bradFrontend.prototype.addMissionParticipant = function(parent,missionId, propositionId, status){
    this.ftStepDOM.jzAjax("JuZFrontEndApplication.addMissionParticipant()",{
      data:{missionId:missionId,propositionId:propositionId,status:status},
      success: function(data){
        if(typeof data == "string" && data == "ok")
          parent.loadTerminateView();
        else
          alert("something went wrong, cannot add mission participant");
      }
    });
  }
  bradFrontend.prototype.loadTerminateView = function(){
    var jDoneBtn = $(".btn-brad-done");
    jDoneBtn.jzAjax("JuZFrontEndApplication.loadTerminateView()",{
      success: function(data){
        if(typeof data == "string" && data != "nok")
          bradFrontend.ftStepDOM.html(data);
        else
          alert("something went wrong");
      }
    });
  }
  bradFrontend.prototype.loadStartView = function () {
    $("#brandadvocacy-ft").jzLoad("JuZFrontEndApplication.loadStartView()",
      {},
      function(data){
        $("#brandadvocacy-ft").html(data);
      });
  }
  var bradFrontend = new bradFrontend();
});
