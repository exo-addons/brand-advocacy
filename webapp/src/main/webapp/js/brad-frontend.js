/**
 * Created by exoplatform on 13/10/14.
 */
(function($) {

  var _ftStepContainer;
  var bradObj = {};

  var _checkFtForm = function(missionId, propositionId ){
    var res = true;
    if(null == missionId || 0 == missionId || typeof missionId == "undefined")
      res = false;
    else if(null == propositionId || 0 == propositionId || typeof propositionId == "undefined")
      res = false;

    if(!res)
      alert(" something went wrong, cannot identify current mission");
    return res;
  };
  var _loadTerminateView = function(){
    $(".jz").jzAjax("JuZFrontEndApplication.loadTerminateView()",{
      success: function(data){
        if(typeof data == "string" && data != "nok")
          _ftStepContainer.html(data);
        else
          alert("something went wrong");
      }
    });
  };
  var _loadStartView = function () {
    $(".jz").jzLoad("JuZFrontEndApplication.loadStartView()",
    function(data){
      $("#brandadvocacy-ft").html(data);
    });
  };

  var _addEventToBtnDiscovery = function(){
    $(document).on('click.juzbrad.ft.discovery.view','#brad-ft-discovery',function(){
      var jDiscovery = $(this);
      jDiscovery.jzAjax("JuZFrontEndApplication.loadStepContainerView()", {
        success: function (data) {
          $("#brad-ft-container").html(data);
          _ftStepContainer = $(".brad-container-step-container");
          $(".jz").jzAjax("JuZFrontEndApplication.initView()", {
            success: function (data) {
              _ftStepContainer.html(data);
            }
          });
        }
      });
    });
  };

  var _addEventToBtnClose = function(){
    $(document).on('click.juzbrad.ft.index.view','.btn-brad-close',function(){
      var jClose = $(this);
      jClose.jzAjax("JuZFrontEndApplication.loadIndexView()",{
        success: function(data){
          $("#brad-ft-container").html(data);
        }
      });
    });
  };

  var _addEventToBtnStart = function(){
    $(document).on('click.juzbrad.ft.start.view','.btn-start',function(){
      var jStart = $(this);
      jStart.jzAjax("JuZFrontEndApplication.loadProcessView()",{
        success: function(data){
          if(typeof data == "string" && data != "nok")
            _ftStepContainer.html(data);
          else
            alert("something went wrong !!!, please try later");
        }
      });
    });
  };

  var _addEventToBtnDone = function(){
    $(document).on('click.juzbrad.ft.done.view','.btn-brad-done',function(){
      var missionId = $(".missionId").val();
      var propositionId = $(".propositionId").val();
      if(-_checkFtForm(missionId,propositionId)) {
        _loadTerminateView();
      }
    });
  };

  var _addEventToBtnTerminate = function(){
    $(document).on('click.juzbrad.ft.terminate.view','.btn-brad-terminate',function(){
      var jTerminate = $(this);
      var url = $("#brad-participant-url-submitted").val();
      var fname = $("#brad-participant-fname").val();
      var lname = $("#brad-participant-lname").val();
      var address = $("#brad-participant-address").val();
      var city = $("#brad-participant-city").val();
      var phone = $("#brad-participant-phone").val();
      var country = $("#brad-participant-country").val();
      var size = $("#brad-participant-size").val();
      jTerminate.jzAjax("JuZFrontEndApplication.loadThankyouView()",{
        data:{'url':url,fname:fname,lname:lname,address:address,city:city,phone:phone,country:country,size:size},
        success: function(data){
          if(typeof data == "string" && data != "nok")
            _ftStepContainer.html(data);
          else
            alert("something went wrong, please reload this page");
        }
      });
    });
  }
  bradObj.init = function(){
    _addEventToBtnDiscovery();
    _addEventToBtnStart();
    _addEventToBtnDone();
    _addEventToBtnTerminate();
    _addEventToBtnClose();
  }
  return bradObj;
})($);