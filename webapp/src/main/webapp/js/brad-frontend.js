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
    jStart.jzAjax("JuZFrontEndApplication.loadTerminateView()",{
      success: function(data){
        bradFrontend.ftStepDOM.html(data);
      }
    });
  });

  $(document).on('click.juzbrad.ft.terminate.view','.btn-brad-terminate',function(){
    var jStart = $(this);
    jStart.jzAjax("JuZFrontEndApplication.loadThankyouView()",{
      success: function(data){
        bradFrontend.ftStepDOM.html(data);
      }
    });
  });

  function bradFrontend(){
    this.ftStepDOM = null;
  };

  bradFrontend.prototype.loadStartView = function () {
    $("#brandadvocacy-ft").jzLoad("JuZFrontEndApplication.loadStartView()",
      {},
      function(data){
        $("#brandadvocacy-ft").html(data);
      });
  }
  var bradFrontend = new bradFrontend();
});
